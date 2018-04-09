package com.huya.search.node;


import com.huya.search.index.opeation.PullContext;

/**
 * Created by zhangyiqun1@yy.com on 2017/10/16.
 */
public class NodePath {

    public static final String HUYA_SEARCH = "/huya-search";

    public static final String MASTER = HUYA_SEARCH + "/master";

    public static final String SALVERS = HUYA_SEARCH + "/salvers";

    public static final String ASSIGN = HUYA_SEARCH + "/assign";

    public static final String TASKS = HUYA_SEARCH + "/tasks";

    public static final String META = HUYA_SEARCH + "/meta";

    public static final String LUCENE = HUYA_SEARCH + "/lucene";

    public static final String PULL = "pull";

    public static String tablePath(String table) {
        return META + "/" + table;
    }

    public static String salverPath(String salver) {
        return SALVERS + "/" + salver;
    }

    public static String tasksPath(String salver) {
        return TASKS + "/" + salver;
    }

    public static String tasksPath(String salver, String command) {
        return TASKS + "/" + salver + "/" + command;
    }

    public static String salverReplaceTasksPath(String path) {
        return path.replace(SALVERS, TASKS);
    }

    public static String pullShard(PullContext pullContext) {
        return pullContext.getTable() + "_" + pullContext.getShardId() + "_" + PULL;
    }

    public static String lucenePath(String path) {
        return LUCENE + path;
    }
}
