package com.huya.search;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import com.huya.search.index.IndexEngine;
import com.huya.search.index.block.TableShardServiceAble;
import com.huya.search.index.lucene.DistributedLazyShardLuceneService;
import com.huya.search.index.meta.RealMetaService;
import com.huya.search.inject.ModulesBuilder;
import com.huya.search.localImport.LocalImportMetaService;
import com.huya.search.localImport.LocalImportService;
import com.huya.search.localImport.LogSource;
import com.huya.search.module.localImport.LocalImportHDFSDataBlockManagerModule;
import com.huya.search.module.localImport.LocalImportServiceModule;
import com.huya.search.module.search.IndexEngineModule;
import com.huya.search.module.search.LuceneServiceModule;
import com.huya.search.module.search.MetaServiceModule;
import com.huya.search.node.NodeEntry;
import com.huya.search.util.IpUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static com.huya.search.GlobalServicePriority.*;

/**
 * Created by zhangyiqun1@yy.com on 2018/1/16.
 */
public class HuyaSearchLocalImport implements Application {

    private OrderServiceItem<LocalImportMetaService> localImportService
            = new OrderServiceItem<>(LocalImportMetaService.class, LOCAL_IMPORT_SERVICE, new LocalImportServiceModule());

    private OrderServiceItem<DistributedLazyShardLuceneService> luceneService
            = new OrderServiceItem<>(DistributedLazyShardLuceneService.class, LUCENE_SERVICE, new LuceneServiceModule());

    private OrderServiceItem<IndexEngine> engineService
            = new OrderServiceItem<>(IndexEngine.class, INDEX_ENGINE, new IndexEngineModule(), new LocalImportHDFSDataBlockManagerModule());

    private OrderServiceItem<RealMetaService> metaService
            = new OrderServiceItem<>(RealMetaService.class, META_SERVICE, new MetaServiceModule());

    private OrderServiceItem[] serviceItems = {
            metaService,  engineService, luceneService, localImportService
    };

    private LogSource logSource;

    private static final class HuyaSearchLocalImportHandle {
        private static final HuyaSearchLocalImport INSTANCE = new HuyaSearchLocalImport();
    }

    public static HuyaSearchLocalImport getInstance() {
        return HuyaSearchLocalImportHandle.INSTANCE;
    }

    public static void main(String[] args) throws IllegalAccessException, InstantiationException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException {
        String table = args[0];
        String logSourceAttr = args[1];
        long lowUnixTime = Long.parseLong(args[2]);
        long upUnixTime  = Long.parseLong(args[3]);
        String logSourceImplClass = args[4];
        String dataConvertClassName = args[5];


        HuyaSearchLocalImport searchLocalImport = HuyaSearchLocalImport.getInstance();
        searchLocalImport.init(table, logSourceAttr, lowUnixTime, upUnixTime, logSourceImplClass, dataConvertClassName);
    }

    @SuppressWarnings("unchecked")
    private void init(String table, String logSourceAttr, long lowUnixTime, long upUnixTime, String logSourceImplClass, String dataConvertClassName) throws IllegalAccessException, ClassNotFoundException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        ModulesBuilder mb = ModulesBuilder.getInstance();

        mb.add(new AbstractModule() {
            @Override
            protected void configure() {
                bind(Application.class).toProvider(HuyaSearchLocalImport::getInstance);
            }
        });

        mb.add(new AbstractModule() {
            @Override
            protected void configure() {
                bind(NodeEntry.class).to(HuyaSearchNodeEntry.class).in(Singleton.class);
                bind(String.class).annotatedWith(Names.named("Host")).toProvider(IpUtils::getHostName);
                bind(Integer.class).annotatedWith(Names.named("Port")).toProvider(() -> -1);
            }
        });

        for (int i = 0; i <= serviceItems.length - 1; i++) {
            mb.add(serviceItems[i].getAbstractModules());
        }
        mb.createInjector();
        for (int i = 0; i <= serviceItems.length - 1; i++) {
            serviceItems[i].init();
        }

        shutdownHook();
        for (int i = 0; i <= serviceItems.length - 1; i++) {
            serviceItems[i].start();
        }

        TableShardServiceAble tableShardServiceAble = mb.createInjector().getInstance(TableShardServiceAble.class);

        initLogSource(table, logSourceAttr, lowUnixTime, upUnixTime, logSourceImplClass, tableShardServiceAble.getShardInfos(table).getShardInfos().shardNum(), dataConvertClassName);
    }

    @SuppressWarnings("unchecked")
    private void initLogSource(String table, String logSourceAttr, long lowUnixTime, long upUnixTime, String logSourceImplClass, int shardNum, String dataConvertClassName) throws IllegalAccessException, InstantiationException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException {

        Class<? extends LogSource> clazz =
                (Class<? extends LogSource>) this.getClass().getClassLoader().loadClass(logSourceImplClass);

        Class<?>[] parTypes = {String.class, String.class, long.class, long.class, int.class, String.class};

        Constructor<? extends LogSource> constructor = clazz.getConstructor(parTypes);

        Object[] pars = {table, logSourceAttr, lowUnixTime, upUnixTime, shardNum, dataConvertClassName};

        logSource = constructor.newInstance(pars);
    }

    @Override
    public void start() {
        LocalImportService localImportService = new LocalImportService(getEngine());
        localImportService.start(logSource);
    }

    @Override
    public void close() {
        for (int i = 0; i < serviceItems.length; i++) {
            serviceItems[i].close();
        }
    }

    private void shutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::close));
    }

    private IndexEngine getEngine() {
        return engineService.getAos();
    }

}
