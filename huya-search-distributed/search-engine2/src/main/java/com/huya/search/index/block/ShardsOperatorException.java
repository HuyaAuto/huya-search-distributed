package com.huya.search.index.block;

import com.huya.search.index.opeation.*;

import java.util.List;

import static com.huya.search.index.block.ShardsOperatorException.ExceptionType.SHARDS_IO_EXCEPTION;
import static com.huya.search.index.block.ShardsOperatorException.ExceptionType.SHARD_IO_EXCEPTION;
import static com.huya.search.index.block.ShardsOperatorException.ExceptionType.SHARD_QUERY_IO_EXCEPTION;

/**
 * Created by zhangyiqun1@yy.com on 2018/1/9.
 */
public abstract class ShardsOperatorException extends Exception {

    private ExceptionType type;

    private ShardsOperatorException(ExceptionType type) {
        this.type = type;
    }

    public static ShardsOperatorException couldNotGetLock(ShardCoordinate shardCoordinate) {
        return new ShardsGetLockOperatorException(shardCoordinate, ExceptionType.COULD_NOT_GET_LOCK){};
    }

    public static ShardsOperatorException closeIOException(CyclesCoordinate cyclesCoordinate) {
        return new ShardsCloseOperatorException(cyclesCoordinate, SHARD_IO_EXCEPTION) {};
    }
    public static ShardsOperatorException closeIOException(List<ShardsOperatorException> list) {
        return new ShardsListCloseOperatorException(list, SHARD_IO_EXCEPTION);
    }

    public static ShardsOperatorException lockIOException(ShardCoordinate shardCoordinate) {
        return new ShardsLockOperatorException(shardCoordinate, SHARDS_IO_EXCEPTION) {};
    }

    public static ShardsOperatorException queryIOException(CycleCoordinate cycleCoordinate) {
        return new ShardQueryOperatorException(cycleCoordinate, SHARD_QUERY_IO_EXCEPTION) {};
    }

    public static ShardsOperatorException createShardFail(ShardCoordinate shardCoordinate) {
        return new CreateShardOperatorException(shardCoordinate, ExceptionType.CREATE_SHARD_FAIL) {};
    }

    public static ShardsOperatorException interrupted(ShardCoordinate shardCoordinate) {
        return new ShardsOperatorInterruptedException(shardCoordinate, ExceptionType.INTERRUPTED) {};
    }

    public static ShardsOperatorException luceneWriterIsClose(CycleCoordinate cycleCoordinate) {
        return new ShardsLuceneWriterIsCloseException(cycleCoordinate, ExceptionType.SHARD_INSERT_LUCENE_CLOSE_EXCEPTION);
    }

    public static ShardsOperatorException addDocumentIOException(CycleCoordinate cycleCoordinate) {
        return new ShardsAddDocumentIOException(cycleCoordinate, ExceptionType.SHARD_ADD_DOCUMENT_EXCEPTION);
    }

    public ExceptionType getType() {
        return type;
    }



    public enum ExceptionType {
        COULD_NOT_GET_LOCK,
        CREATE_SHARD_FAIL,
        SHARD_IO_EXCEPTION,
        SHARDS_IO_EXCEPTION,
        SHARD_QUERY_IO_EXCEPTION,
        SHARD_INSERT_LUCENE_CLOSE_EXCEPTION,
        SHARD_ADD_DOCUMENT_EXCEPTION,
        INTERRUPTED
    }

    private static class ShardCoordinateException extends ShardsOperatorException implements ShardCoordinate  {

        private ShardCoordinate shardCoordinate;

        private ShardCoordinateException(ShardCoordinate shardCoordinate, ExceptionType type) {
            super(type);
            this.shardCoordinate = shardCoordinate;
        }

        @Override
        public int getShardId() {
            return shardCoordinate.getShardId();
        }

        @Override
        public String getTable() {
            return shardCoordinate.getTable();
        }
    }

    private static class CycleCoordinateException extends ShardsOperatorException implements CycleCoordinate {

        private CycleCoordinate cycleCoordinate;

        private CycleCoordinateException(CycleCoordinate cycleCoordinate, ExceptionType type) {
            super(type);
            this.cycleCoordinate = cycleCoordinate;
        }

        @Override
        public long getCycle() {
            return cycleCoordinate.getCycle();
        }

        @Override
        public int getShardId() {
            return cycleCoordinate.getShardId();
        }

        @Override
        public String getTable() {
            return cycleCoordinate.getTable();
        }
    }

    public static class ShardsGetLockOperatorException extends ShardCoordinateException {

        private ShardsGetLockOperatorException(ShardCoordinate shardCoordinate, ExceptionType type) {
            super(shardCoordinate, type);
        }
    }

    public static class CreateShardOperatorException extends ShardCoordinateException {

        private CreateShardOperatorException(ShardCoordinate shardCoordinate, ExceptionType type) {
            super(shardCoordinate, type);
        }
    }

    public static class ShardsOperatorInterruptedException extends ShardCoordinateException {

        private ShardsOperatorInterruptedException(ShardCoordinate shardCoordinate, ExceptionType type) {
            super(shardCoordinate, type);
        }
    }

    public static class ShardsCloseOperatorException extends ShardsOperatorException implements CyclesCoordinate {

        private CyclesCoordinate cyclesCoordinate;


        private ShardsCloseOperatorException(CyclesCoordinate cyclesCoordinate, ExceptionType type) {
            super(type);
            this.cyclesCoordinate = cyclesCoordinate;
        }

        @Override
        public String getTable() {
            return cyclesCoordinate.getTable();
        }

        @Override
        public List<Long> getCycles() {
            return cyclesCoordinate.getCycles();
        }

        @Override
        public int getShardId() {
            return cyclesCoordinate.getShardId();
        }
    }

    public static class ShardQueryOperatorException extends ShardsOperatorException implements CycleCoordinate {

        private CycleCoordinate cycleCoordinate;

        private ShardQueryOperatorException(CycleCoordinate cycleCoordinate, ExceptionType type) {
            super(type);
            this.cycleCoordinate = cycleCoordinate;
        }

        @Override
        public long getCycle() {
            return cycleCoordinate.getCycle();
        }

        @Override
        public int getShardId() {
            return cycleCoordinate.getShardId();
        }

        @Override
        public String getTable() {
            return cycleCoordinate.getTable();
        }
    }

    public static class ShardsLockOperatorException extends ShardsOperatorException implements ShardCoordinate {

        private ShardCoordinate shardCoordinate;

        private ShardsLockOperatorException(ShardCoordinate shardCoordinate, ExceptionType type) {
            super(type);
            this.shardCoordinate = shardCoordinate;
        }

        @Override
        public int getShardId() {
            return shardCoordinate.getShardId();
        }

        @Override
        public String getTable() {
            return shardCoordinate.getTable();
        }
    }

    public static class ShardsLuceneWriterIsCloseException extends CycleCoordinateException {

        private ShardsLuceneWriterIsCloseException(CycleCoordinate cycleCoordinate, ExceptionType type) {
            super(cycleCoordinate, type);
        }

    }

    public static class ShardsAddDocumentIOException extends CycleCoordinateException {

        private ShardsAddDocumentIOException(CycleCoordinate cycleCoordinate, ExceptionType type) {
            super(cycleCoordinate, type);
        }

    }

    private static class ShardsListCloseOperatorException extends ShardsOperatorException {

        private List<ShardsOperatorException> list;

        public ShardsListCloseOperatorException(List<ShardsOperatorException> list, ExceptionType type) {
            super(type);
            this.list = list;
        }

        public List<ShardsOperatorException> getList() {
            return list;
        }
    }
}