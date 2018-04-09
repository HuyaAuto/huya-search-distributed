package com.huya.search.facing.producer;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.huya.search.facing.subscriber.KafkaShardConsumer;
import com.huya.search.settings.Settings;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyCodeSource;
import groovy.lang.Singleton;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import java.util.Properties;

@Singleton
public class KafkaProducer implements Producer {

    private static final GroovyClassLoader GROOVY_CLASS_LOADER = new GroovyClassLoader(KafkaShardConsumer.class.getClassLoader());

    private Properties properties;

    @Inject
    public KafkaProducer(@Named("Kafka-Producer") Settings settings) {
        this.properties = settings.asProperties();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void producerData(String topic, String scriptName, long num) {
        org.apache.kafka.clients.producer.KafkaProducer<String, String> producer
                = new org.apache.kafka.clients.producer.KafkaProducer<>(properties);

        producer.partitionsFor(topic);

        ProducerEngine engine = initGroovyProducerScript(scriptName, topic, num);

        for (ProducerRecord record : engine) {
            producer.send(record);
        }
    }

    @SuppressWarnings("unchecked")
    private ProducerEngine initGroovyProducerScript(String scriptName, String topic,  long num) {
        try {
            Class groovyClass = GROOVY_CLASS_LOADER.parseClass(
                    new GroovyCodeSource(Objects.requireNonNull(this.getClass().getClassLoader().getResource("producer/" + scriptName)))
            );
            return (ProducerEngine) groovyClass.getConstructor(String.class, long.class).newInstance(topic, num);
        } catch ( IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }


}
