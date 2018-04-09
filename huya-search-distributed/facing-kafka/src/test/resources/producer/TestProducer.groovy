package producer

import com.fasterxml.jackson.databind.node.ObjectNode
import com.huya.search.facing.producer.ProducerEngine
import com.huya.search.util.JsonUtil
import org.apache.kafka.clients.producer.ProducerRecord
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter

/**
 * 生成 Test 测试数据的 Groovy 脚本
 * Created by zhangyiqun1@yy.com on 2018/3/12.
 */
class TestProducer extends ProducerEngine {

    private static final DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")

    private String topic

    private long num

    TestProducer(String topic, long num) {
        this.topic = topic
        this.num = num
    }

    @Override
    Iterator<ProducerRecord<String, String>> iterator() {
        return new Iterator<ProducerRecord<String, String>>() {

            private long createNum

            private Random random = new Random()

            @Override
            boolean hasNext() {
                return createNum < num
            }

            @Override
            ProducerRecord<String, String> next() {
                createNum ++
                ObjectNode object = new ObjectNode(JsonUtil.getObjectMapper().getNodeFactory())
                object.put("level", random.nextInt())
                object.put("content", "start |"+ random.nextInt() + "| up|2342342342|asdasd|asda123213")
                object.put("keyWord", random.nextBoolean() ? "yes" : "no")
                object.put("num", random.nextFloat())
                object.put("timestamp", formatter.print(DateTime.now()))
                return new ProducerRecord<>(topic, object.toString())
            }
        }
    }
}
