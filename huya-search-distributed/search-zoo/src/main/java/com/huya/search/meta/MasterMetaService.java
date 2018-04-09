package com.huya.search.meta;

import com.huya.search.index.meta.MetaDefine;
import com.huya.search.index.meta.TimelineMetaDefine;
import com.huya.search.service.AbstractOrderService;

import java.util.Iterator;

/**
 * Created by zhangyiqun1@yy.com on 2017/10/18.
 */
public abstract class MasterMetaService extends AbstractOrderService {

    public abstract void createMeta(TimelineMetaDefine metaDefine);

    public abstract void removeMeta(String table);

    public abstract void updateMeta(String table, MetaDefine metaDefine);

    public abstract void openMeta(String table);

    public abstract void closeMeta(String table);

    public abstract Iterator<TimelineMetaDefine> metas();

    public abstract TimelineMetaDefine meta(String table);

}
