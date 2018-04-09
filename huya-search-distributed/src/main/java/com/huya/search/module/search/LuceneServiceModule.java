package com.huya.search.module.search;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import com.huya.search.IndexSettings;
import com.huya.search.index.lucene.*;
import com.huya.search.memory.LuceneMemoryCore;
import com.huya.search.memory.MemoryCore;
import com.huya.search.settings.Settings;
import org.apache.lucene.store.LockFactory;

/**
 * Created by zhangyiqun1@yy.com on 2017/8/15.
 */
public class LuceneServiceModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(Settings.class).annotatedWith(Names.named("Settings")).to(IndexSettings.class).in(Singleton.class);
        bind(LazyFileSystem.class).to(LazyHdfsFileSystem.class).in(Singleton.class);
        bind(new TypeLiteral<MemoryCore<WriterAndReadLuceneOperator>>() {}).annotatedWith(Names.named("LuceneOperator")).to(LuceneMemoryCore.class).in(Singleton.class);
        bind(LockFactory.class).to(HuyaSearchNoLockFactory.class).in(Singleton.class);
    }
}
