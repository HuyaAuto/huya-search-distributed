package com.huya.search.facing.subscriber;

import com.github.ssedano.hash.JumpConsistentHash;
import com.huya.search.index.opeation.PullContext;
import org.apache.commons.lang.StringUtils;

import java.util.Objects;

import static com.huya.search.index.opeation.PullContext.PullMethod.END;
import static com.huya.search.index.opeation.PullContext.PullMethod.FOLLOW;

/**
 * Created by zhangyiqun1@yy.com on 2017/11/10.
 */
public class KafkaPullContext extends PullContext {

    private String table;

    private int shardId;

    private PullMethod method;

    public static PullContext newInstance(String table, int shardId, String method) {
        if (StringUtils.isEmpty(method)) {
            method = System.getProperty(PULL_METHOD_PREFIX + table);
        }

        if (method == null || Objects.equals(method.toLowerCase(), FOLLOW.toString().toLowerCase())) {
            return newInstance(table, shardId);
        }
        else {
            return newEndInstance(table, shardId);
        }
    }


    public static PullContext newInstance(String table, int shardId) {
        return new KafkaPullContext(table, shardId, FOLLOW);
    }

    public static PullContext newEndInstance(String table, int shardId) {
        return new KafkaPullContext(table, shardId, END);
    }

    private KafkaPullContext(String table, int shardId, PullMethod method) {
        this.table = table;
        this.shardId = shardId;
        this.method = method;
    }

    @Override
    public String getTable() {
        return table;
    }

    @Override
    public int getShardId() {
        return shardId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        KafkaPullContext that = (KafkaPullContext) o;

        if (shardId != that.shardId) return false;
        return table.equals(that.table);
    }

    @Override
    public PullMethod getMethod() {
        return method;
    }

    @Override
    public int hash() {
        return JumpConsistentHash.jumpConsistentHash(table + shardId, Integer.MAX_VALUE);
    }

    @Override
    public int hashCode() {
        int result = table.hashCode();
        result = 31 * result + shardId;
        return result;
    }

    @Override
    public String toString() {
        return "KafkaPullContext{" +
                "table='" + table + '\'' +
                ", shardId=" + shardId +
                ", method=" + method +
                '}';
    }


}
