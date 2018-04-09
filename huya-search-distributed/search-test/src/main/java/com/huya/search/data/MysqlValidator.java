package com.huya.search.data;

import com.huya.search.index.data.SearchData;
import com.huya.search.index.meta.impl.FieldFactory;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.index.IndexableField;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by zhangyiqun1@yy.com on 2017/10/11.
 */
public class MysqlValidator implements Validator {

    public static MysqlValidator newInstance() {
        return new MysqlValidator();
    }

    private DataSource dataSource;



    private MysqlValidator() {
        this.dataSource = getMySQLDataSource();
    }

    private DataSource getMySQLDataSource() {
        Properties props = new Properties();
        MysqlDataSource mysqlDS = null;
        try {
            InputStream is = this.getClass().getClassLoader().getResourceAsStream("validatorDb.properties");
            props.load(is);
            mysqlDS = new MysqlDataSource();
            mysqlDS.setURL(props.getProperty("url"));
            mysqlDS.setUser(props.getProperty("user"));
            mysqlDS.setPassword(props.getProperty("password"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mysqlDS;
    }

    private ResultSetHandler<SearchData<? extends Iterable<IndexableField>>> handler = rs -> {
        List<Iterable<IndexableField>> collection = new ArrayList<>();
        while(rs.next()) {
            List<IndexableField> temp = new ArrayList<>();
            ResultSetMetaData meta = rs.getMetaData();
            int cols = meta.getColumnCount();
            for (int i = 1; i <= cols; i++) {
                String name = meta.getColumnName(i);
                if (MysqlColumn.VARCHAR.startsWith(meta.getColumnTypeName(i))) {
                    temp.add(FieldFactory.createIndexableField(name, rs.getString(i)));
                } else if (meta.getColumnTypeName(i).equals(MysqlColumn.INT)) {
                    temp.add(FieldFactory.createIndexableField(name, rs.getInt(i)));
                } else if (meta.getColumnTypeName(i).equals(MysqlColumn.BIGINT)) {
                    temp.add(FieldFactory.createIndexableField(name, rs.getLong(i)));
                } else if (MysqlColumn.FLOAT.startsWith(meta.getColumnTypeName(i))) {
                    temp.add(FieldFactory.createIndexableField(name, rs.getFloat(i)));
                } else if (MysqlColumn.DOUBLE.startsWith(meta.getColumnTypeName(i))) {
                    temp.add(FieldFactory.createIndexableField(name, rs.getDouble(i)));
                }
            }
            collection.add(temp);
        }
        return SearchData.newInstance(collection);
    };

    @Override
    public void injectData(String table, SearchData<? extends Iterable<IndexableField>> searchData) {
        Set<MysqlColumn> columnSet = new HashSet<>();
        searchData.getCollection().forEach(iterable -> iterable.forEach(field -> columnSet.add(new MysqlColumn(field))));
        MysqlColumn[] columns = new MysqlColumn[columnSet.size()];
        columnSet.toArray(columns);
        createTable(table, columns);
        insertData(table, searchData, columns);
    }

    private void insertData(String table, SearchData<? extends Iterable<IndexableField>> searchData, MysqlColumn[] columns) {
        QueryRunner queryRunner = new QueryRunner(dataSource);
        StringBuilder placeholder = new StringBuilder();
        for (int i = 0; i < columns.length; i++) {
            placeholder.append("?");
            if (i != columns.length - 1) placeholder.append(", ");
        }

        Object[][] insertObject = convertData(searchData, columns);
        try {
            queryRunner.batch("INSERT INTO " + table + " VALUES (" + placeholder.toString() + ") ", insertObject);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Object[][] convertData(SearchData<? extends Iterable<IndexableField>> searchData, MysqlColumn[] columns) {
        Object[][] temp = new Object[searchData.size()][columns.length];
        List<? extends Iterable<IndexableField>> collection = searchData.getCollection();
        for (int j = 0; j < collection.size(); j++) {
            Object[] objects = new Object[columns.length];
            collection.get(j).forEach(field -> {
                for (int i = 0; i < columns.length; i++) {
                    if (columns[i].name.equals(field.name())) {
                        Number number = field.numericValue();
                        if (number != null) objects[i] = field.numericValue();

                        String str = field.stringValue();
                        if (str != null) objects[i] = field.stringValue();
                        break;
                    }
                }
            });
            temp[j] = objects;
        }
        return temp;
    }


    private void createTable(String table, MysqlColumn[] columns) {
        String columnInfos = StringUtils.join(columns, ",");
        QueryRunner queryRunner = new QueryRunner(dataSource);
        try {
            queryRunner.update("CREATE TABLE " + table + " (" + columnInfos + ")");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public SearchData<? extends Iterable<IndexableField>> query(String sql) {
        QueryRunner queryRunner = new QueryRunner(dataSource);
        try {
            return queryRunner.query(sql, handler);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void remove(String table) {
        QueryRunner queryRunner = new QueryRunner(dataSource);
        try {
            queryRunner.update("DROP TABLE " + table);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    class MysqlColumn {

        public static final String INT     = "INT";
        public static final String BIGINT  = "BIGINT";
        public static final String FLOAT   = "FLOAT(20,20)";
        public static final String DOUBLE  = "DOUBLE(20,20)";
        public static final String VARCHAR = "VARCHAR(100)";

        private String name;
        private String type;

        public MysqlColumn(IndexableField field) {
            this.name = field.name();
            this.type = getMysqlType(field);
        }

        private String getMysqlType(IndexableField field) {
            Number number = field.numericValue();
            if (number != null) {
                if (number instanceof Integer) return INT;
                else if (number instanceof Long) return BIGINT;
                else if (number instanceof Float) return FLOAT;
                else return DOUBLE;
            }
            return VARCHAR;
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) return true;
            if (object == null || getClass() != object.getClass()) return false;

            MysqlColumn that = (MysqlColumn) object;

            if (name != null ? !name.equals(that.name) : that.name != null) return false;
            return type != null ? type.equals(that.type) : that.type == null;
        }

        @Override
        public int hashCode() {
            int result = name != null ? name.hashCode() : 0;
            result = 31 * result + (type != null ? type.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return name + " " + type;
        }
    }
}
