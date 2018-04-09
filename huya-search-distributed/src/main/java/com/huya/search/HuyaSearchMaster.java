package com.huya.search;

import com.google.common.collect.Maps;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import com.huya.search.facing.producer.KafkaProducer;
import com.huya.search.facing.producer.KafkaProducerService;
import com.huya.search.index.meta.MetaService;
import com.huya.search.index.meta.RealMetaService;
import com.huya.search.inject.ModulesBuilder;
import com.huya.search.module.KafkaProducerServiceModule;
import com.huya.search.module.NettyServiceModule;
import com.huya.search.module.search.MasterNodeServiceModule;
import com.huya.search.module.search.MetaServiceModule;
import com.huya.search.module.search.ZookeeperServiceModule;
import com.huya.search.node.NodeEntry;
import com.huya.search.node.ZooKeeperOperator;
import com.huya.search.node.sep.SearchMasterNodeService;
import com.huya.search.restful.NettyService;
import com.huya.search.service.LifecycleListener;
import com.huya.search.util.IpUtils;
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
@Singleton
public class HuyaSearchMaster implements Application {

    private static final String HOST = IpUtils.getHostName();

    private static final int PORT = 28888;

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

    private static final Logger LOG = LoggerFactory.getLogger(HuyaSearchMaster.class);

    private static final int SERVICE_NUM = 5;

    //正常启动
    private static final int MODULE_MASK = 15;

    //MASK 8
    private OrderServiceItem<ZooKeeperOperator> zookeeperService
            = new OrderServiceItem<>(ZooKeeperOperator.class, ZOOKEEPER_SERVICE, new ZookeeperServiceModule());

    //MASK 4
    private OrderServiceItem<RealMetaService> metaService
            = new OrderServiceItem<>(RealMetaService.class, META_SERVICE, new MetaServiceModule());

    //MASK 2
    private OrderServiceItem<SearchMasterNodeService> nodeService
            = new OrderServiceItem<>(SearchMasterNodeService.class, NODE_SERVICE, new MasterNodeServiceModule());

    //MASK 1
    private OrderServiceItem<NettyService> nettyService
            = new OrderServiceItem<>(NettyService.class, NETTY_SERVICE, new NettyServiceModule());

    private OrderServiceItem[] serviceItems = {
            nettyService, nodeService, metaService, zookeeperService
    };

    private Map<Class, OrderServiceItem> map = Maps.newHashMap();

    {
        for (OrderServiceItem item : serviceItems) {
            map.put(item.getClazz(), item);
        }
    }

    private boolean[] booleans;

    private NodeEntry nodeEntry;

    private static final class HuyaSearchMasterHandle {
        private static final HuyaSearchMaster INSTANCE = new HuyaSearchMaster();
    }

    public static HuyaSearchMaster getInstance() {
        return HuyaSearchMasterHandle.INSTANCE;
    }

    @Inject
    private HuyaSearchMaster() {}

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
            HuyaSearchMaster searchMaster = HuyaSearchMaster.getInstance();
            searchMaster.init(MODULE_MASK);
            searchMaster.start();
        } catch (Exception e) {
            LOG.error("huya-search error", e);
        }
    }

    public static HuyaSearchMaster noBlockTestStart(String[] args) {
        main(args);
        return HuyaSearchMaster.getInstance();
    }

    @SuppressWarnings("unchecked")
    private void init(int moduleMask) {
        this.booleans = mask(moduleMask);
        ModulesBuilder mb = ModulesBuilder.getInstance();

        mb.add(new AbstractModule() {
            @Override
            protected void configure() {
                bind(Application.class).toProvider(HuyaSearchMaster::getInstance);
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

    public MetaService getMetaService() {
        return metaService.getAos();
    }

    public SearchMasterNodeService getNodeService() {
        return nodeService.getAos();
    }

    public NettyService getNettyService() {
        return nettyService.getAos();
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
