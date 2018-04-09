package com.huya.facing.convert;

import com.alibaba.fastjson.JSONObject;
import com.duowan.datawarehouse.model.ActionLog;
import com.duowan.datawarehouse.utils.ActionLogParser;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;

/**
 * @author ZhangXueJun
 * @date 2018年03月16日
 */
public class IndexConfGenerator {

    private static final String table = "taf_log_data";
    private static final int partitionNum = 50;
    private static final String topic = "beelogsvr_taf";
    private static String kafkaBootstrapServers = "foshan0-kafka-beelogsvr.huya.com:9092,foshan1-kafka-beelogsvr.huya.com:9092,foshan2-kafka-beelogsvr.huya.com:9092,foshan3-kafka-beelogsvr.huya.com:9092";
    private static final String host = "ip-10-64-80-193.yygamedev.com";

    private static final int[] PORT_RANGE = {2888, 2889, 2890, 2891, 2892, 2893, 2894};

    public static void main(String[] args) throws Exception {
        StringBuilder sb = new StringBuilder();
//        kafkaBootstrapServers = "search-transfer-kk01.huya.com:9092,search-transfer-kk02.huya.com:9092";
        // TableMeta
        sb.append("##################################################################").append("\n");
        sb.append("INSERT INTO `TableMeta` (`table`, `topic`, `kafkaBootstrapServers`, `indexDeserializer`, `accuracy`, `valid`)").append("\n");
        sb.append("VALUES ('" + table + "', '" + topic + "', '" + kafkaBootstrapServers + "', "
                + "'com.huya.search.facing.convert.HiidoLiveServerWaterConvert', '0.00000000000000000000', '1');").append("\n");
        sb.append("##################################################################").append("\n");

        // LogContext
        sb.append("INSERT INTO `LogContext` (`table`, `columnKeys`, `context`) VALUES ('" + table + "', NULL, 'timestamp');").append("\n");
        sb.append("##################################################################").append("\n");

        //  TableColumn
        sb.append("INSERT INTO `TableColumn` (`table`, `partitionName`, `columnInfo`, `valid`) VALUES ('" + table + "', '2018-03-10_13_01', '" + column_json + "', '1');\n").append("\n");
        sb.append("##################################################################").append("\n");

        // TaskInfo

        int portIndex = 0;
        for (int partition = 0; partition < partitionNum; partition++) {
            if ((partitionNum + 1) / 2 == partition) {
                portIndex++;
            }
            int port = PORT_RANGE[portIndex];
            sb.append("INSERT INTO `TaskInfo` (`table`, `shardId`, `serverUrl`) VALUES ('" + table + "', '" + partition + "', '" + host + ":" + port + "');").append("\n");
        }

        sb.append(" delete from TableMeta where `table`= '" +  table+ "';").append("\n");
        sb.append(" delete from LogContext where `table`= '" +  table+ "';").append("\n");
        sb.append(" delete from TableColumn where `table`= '" +  table+ "';").append("\n");
        sb.append(" delete from TaskInfo where `table`= '" +  table+ "';").append("\n");
        System.out.println(sb);
    }


    @Test
    public void test01() throws Exception {
        System.out.println(genColumnJson());
    }

    @Test
    public void test02() throws Exception {
        parseLog();
    }

    private static void parseLog() throws Exception {
        BufferedReader fileReader = new BufferedReader(new FileReader(new File("E:/other/huya-search/huya-search-distributed/data.txt")));
        String line = null;
        while ((line = fileReader.readLine()) != null) {
//            if (line.length() == "1521268931041".length()) {
//                continue;
//            }
//
            if (StringUtils.isEmpty(line)) {
                continue;
            }
//
//            if (line.equals("\n")) {
//                continue;
//            }

            List<ActionLog> actionLogs = ActionLogParser.parseHiidoPBToActionLogs(line.getBytes(), false, null);
            System.out.println(actionLogs);
        }
    }

    private static String genColumnJson() throws Exception {
        BufferedReader fileReader = new BufferedReader(new FileReader(new File("E:/other/huya-search/huya-search-distributed/data.txt")));
        TreeMap<String, TreeSet<Object>> groupMap = Maps.newTreeMap();
        String line = null;
        while ((line = fileReader.readLine()) != null) {
            if (line.length() == "1521268931041".length()) {
                continue;
            }

            if (StringUtils.isEmpty(line)) {
                continue;
            }

            if (line.equals("\n")) {
                continue;
            }

            String[] splitLines = line.split(",hiido_time");

            int i = 0;
            for (String valueLine : splitLines) {
                if (i == 0) {
                    i++;
                    continue;
                }
                valueLine = "hiido_time" + valueLine;
                String[] arr = valueLine.split("&");
                for (String pair : arr) {
                    String[] s = pair.split("=");
                    String key = s[0];

                    if (StringUtils.isEmpty(key)) {
                        continue;
                    }

                    TreeSet<Object> objects = groupMap.get(key);
                    if (CollectionUtils.isEmpty(objects)) {
                        objects = new TreeSet<>();
                        groupMap.put(key, objects);
                    }
                    if (s.length >= 2) {
                        String value = s[1];
                        objects.add(value);
                    }
                }
            }
        }


        JSONObject jsonObject = new JSONObject();
        for (String key : groupMap.keySet()) {
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("type", "string");
            jsonObject1.put("sort", "false");
            jsonObject.put(key, jsonObject1);
        }
        return jsonObject.toJSONString();
    }


    //    private static String column_json = "{\r\n        \"sguid\": {\r\n            \"type\": \"string\",\r\n            \"sort\": \"false\"\r\n        },\r\n        \"_uid\": {\r\n            \"type\": \"long\",\r\n            \"sort\": \"false\"\r\n        },\r\n        \"_ip\": {\r\n            \"type\": \"string\",\r\n            \"sort\": \"true\"\r\n        },\r\n        \"_area\": {\r\n            \"type\": \"text\",\r\n            \"sort\": \"true\"\r\n        },\r\n        \"_isp\": {\r\n            \"type\": \"string\",\r\n            \"sort\": \"true\"\r\n        },\r\n        \"success\": {\r\n            \"type\": \"integer\",\r\n            \"sort\": \"true\"\r\n        },\r\n        \"retcode\": {\r\n            \"type\": \"integer\",\r\n            \"sort\": \"true\"\r\n        },\r\n        \"extdesc\": {\r\n            \"type\": \"string\",\r\n            \"sort\": \"false\"\r\n        },\r\n        \"schema\": {\r\n            \"type\": \"string\",\r\n            \"sort\": \"false\"\r\n        },\r\n        \"authority\": {\r\n            \"type\": \"string\",\r\n            \"sort\": \"false\"\r\n        },\r\n        \"path\": {\r\n            \"type\": \"string\",\r\n            \"sort\": \"false\"\r\n        },\r\n        \"datatype\": {\r\n            \"type\": \"string\",\r\n            \"sort\": \"false\"\r\n        },\r\n        \"value\": {\r\n            \"type\": \"integer\",\r\n            \"sort\": \"true\"\r\n        },\r\n        \"experiment\": {\r\n            \"type\": \"string\",\r\n            \"sort\": \"true\"\r\n        },\r\n        \"device\": {\r\n            \"type\": \"string\",\r\n            \"sort\": \"true\"\r\n        },\r\n        \"platform\": {\r\n            \"type\": \"string\",\r\n            \"sort\": \"true\"\r\n        },\r\n        \"version\": {\r\n            \"type\": \"string\",\r\n            \"sort\": \"true\"\r\n        },\r\n        \"_sysits\": {\r\n            \"type\": \"long\",\r\n            \"sort\": \"false\"\r\n        }\r\n    }";
    private static String column_json = "{\n" +
            "  \"__app__\": {\"sort\": \"true\"},\n" +
            "  \"__module__\": {\"sort\": \"true\"},\n" +
            "  \"__type__\": {\"sort\": \"true\"},\n" +
            "  \"__inode__\": {\"sort\": \"true\", \"type\":\"long\"},\n" +
            "  \"__line_num__\": {\"sort\": \"true\", \"type\":\"long\"},\n" +
            "  \"filename\": {\"sort\": \"true\"},\n" +
            "  \"ip\": {\"sort\": \"true\"},\n" +
            "  \"dir\": {\"sort\": \"true\"},\n" +
            "  \"content\": {\"sort\": \"true\", \"type\":\"text\"},\n" +
            "  \"timestamp\": {\"sort\": \"true\", \"type\":\"long\"}\n" +
            "}";
}
