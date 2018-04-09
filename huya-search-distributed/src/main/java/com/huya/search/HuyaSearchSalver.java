package com.huya.search;

import com.google.common.collect.Maps;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import com.huya.search.facing.subscriber.KafkaSubscriberService;
import com.huya.search.facing.subscriber.SubscriberService;
import com.huya.search.index.IndexEngine;
import com.huya.search.index.lucene.DistributedLazyShardLuceneService;
import com.huya.search.index.lucene.LuceneService;
import com.huya.search.index.meta.MetaService;
import com.huya.search.index.meta.RealMetaService;
import com.huya.search.inject.ModulesBuilder;
import com.huya.search.module.KafkaSubscriberServiceModule;
import com.huya.search.module.search.*;
import com.huya.search.node.NodeEntry;
import com.huya.search.node.ZooKeeperOperator;
import com.huya.search.node.sep.SearchSalverNodeService;
import com.huya.search.service.LifecycleListener;
import com.huya.search.util.IpUtils;
import com.huya.search.util.PortUtil;
import com.huya.search.util.PropertiesUtils;
import org.apache.hadoop.util.ShutdownHookManager;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Properties;

import static com.huya.search.GlobalServicePriority.*;

/**
 * Created by zhangyiqun1@yy.com on 2018/2/26.
 */
public class HuyaSearchSalver implements Application {

    private static final int[] PORT_RANGE = {2888, 2889, 2890, 2891, 2892, 2893, 2894};

    private static final String HOST = IpUtils.getHostName();

    private static final int PORT = PortUtil.getPortFromArray(PORT_RANGE);

    private static final String LOG4J = "log4j.properties";

    static {
        org.apache.log4j.LogManager.resetConfiguration();
        Properties logProperties;
        logProperties = PropertiesUtils.getProperties(LOG4J);
        assert logProperties != null;
        String path = logProperties.getProperty("placeholder.log4j.appender.dayfile.File");
        assert path != null;
        logProperties.setProperty("log4j.appender.dayfile.File", path.replace("${serverURL}", HOST + "_" + PORT));
        PropertyConfigurator.configure(logProperties);
    }

    static{
        String OS = System.getProperty("os.name").toLowerCase();
        if (!OS.contains("mac")) {
            System.setProperty("HADOOP_USER_NAME", "hadoop");   //此参数只在正式环境上起作用
        }
    }

    private static final Logger LOG = LoggerFactory.getLogger(HuyaSearchSalver.class);

    private static final int SERVICE_NUM = 6;

    private static final int MODULE_MASK = 63;

    //MASK 32
    private OrderServiceItem<ZooKeeperOperator> zookeeperService
            = new OrderServiceItem<>(ZooKeeperOperator.class, ZOOKEEPER_SERVICE, new ZookeeperServiceModule());

    //MASK 16
    private OrderServiceItem<DistributedLazyShardLuceneService> luceneService
            = new OrderServiceItem<>(DistributedLazyShardLuceneService.class, LUCENE_SERVICE, new LuceneServiceModule());

    //MASK 8
    private OrderServiceItem<IndexEngine> engineService
            = new OrderServiceItem<>(IndexEngine.class, INDEX_ENGINE, new IndexEngineModule(), new HDFSDataBlockManagerModule());

    //MASK 4
    private OrderServiceItem<KafkaSubscriberService> subscriberService
            = new OrderServiceItem<>(KafkaSubscriberService.class, KAFKA_SUBSCRIBE_SERVICE, new KafkaSubscriberServiceModule());

    //MASK 2
    private OrderServiceItem<RealMetaService> metaService
            = new OrderServiceItem<>(RealMetaService.class, META_SERVICE, new MetaServiceModule());

    //MASK 1
    private OrderServiceItem<SearchSalverNodeService> nodeService
            = new OrderServiceItem<>(SearchSalverNodeService.class, NODE_SERVICE, new SalverNodeServiceModule());

    private OrderServiceItem[] serviceItems = {
            nodeService, metaService, subscriberService, engineService, luceneService, zookeeperService
    };

    private Map<Class, OrderServiceItem> map = Maps.newHashMap();

    {
        for (OrderServiceItem item : serviceItems) {
            map.put(item.getClazz(), item);
        }
    }

    private boolean[] booleans;

    private NodeEntry nodeEntry;

    private static final class HuyaSearchSalverHandle {
        private static final HuyaSearchSalver INSTANCE = new HuyaSearchSalver();
    }

    public static HuyaSearchSalver getInstance() {
        return HuyaSearchSalverHandle.INSTANCE;
    }

    @Inject
    private HuyaSearchSalver() {}

    private static boolean[] mask(int mask) {
        boolean[] booleanMask = new boolean[SERVICE_NUM];
        for (int i = 0; i < SERVICE_NUM; i++) {
            int maskCut = (int) Math.pow(2, SERVICE_NUM - i - 1);
            if (mask >= maskCut) {
                booleanMask[SERVICE_NUM - i -1] = true;
                mask -= maskCut;
            }
        }
        return booleanMask;
    }

    public static void main(String[] args) {
        try {
            HuyaSearchSalver searchSalver = HuyaSearchSalver.getInstance();
            searchSalver.init(MODULE_MASK);
            searchSalver.start();
        } catch (Exception e) {
            LOG.error("huya-search error", e);
        }
    }

    public static HuyaSearchSalver noBlockTestStart(String[] args) {
        main(args);
        return HuyaSearchSalver.getInstance();
    }

    @SuppressWarnings("unchecked")
    private void init(int moduleMask) {
        this.booleans = mask(moduleMask);
        ModulesBuilder mb = ModulesBuilder.getInstance();

        mb.add(new AbstractModule() {
            @Override
            protected void configure() {
                bind(Application.class).toProvider(HuyaSearchSalver::getInstance);
            }
        });

        mb.add(new AbstractModule() {
            @Override
            protected void configure() {
                bind(NodeEntry.class).to(HuyaSearchNodeEntry.class).in(Singleton.class);
                bind(String.class).annotatedWith(Names.named("Host")).toProvider(() -> HOST);
                bind(Integer.class).annotatedWith(Names.named("Port")).toProvider(() -> PORT);
            }
        });

        for (int i = 0; i < booleans.length; i++) {
            if (booleans[i]) mb.add(serviceItems[i].getAbstractModules());
        }
        mb.createInjector();
        for (int i = 0; i < booleans.length; i++) {
            if (booleans[i]) serviceItems[i].init();
        }

        nodeEntry = mb.createInjector().getInstance(NodeEntry.class);
    }

    public void start() {
        shutdownHook();
        for (int i = booleans.length - 1; i >= 0 ; i--) {
            if (booleans[i]) {
                if (!serviceItems[i].start()) {
                    break;
                }
            }
        }
    }

    public void close() {
        for (int i = 0; i < booleans.length; i++) {
            if (booleans[i]) serviceItems[i].close();
        }
    }

    private void shutdownHook() {
        ShutdownHookManager.get().addShutdownHook(new Thread(this::close), Integer.MAX_VALUE);
    }

    public IndexEngine getEngine() {
        return engineService.getAos();
    }

    public LuceneService getLuceneService() {
        return luceneService.getAos();
    }

    public MetaService getMetaService() {
        return metaService.getAos();
    }

    public SubscriberService getSubscriberService() {
        return subscriberService.getAos();
    }

    public SearchSalverNodeService getNodeService() {
        return nodeService.getAos();
    }

    public void addListener(Class clazz, LifecycleListener lifecycleListener) {
        OrderServiceItem item = map.get(clazz);
        if (item != null) {
            item.add(lifecycleListener);
        }
    }

    public NodeEntry getNodeEntry() {
        return nodeEntry;
    }

}
