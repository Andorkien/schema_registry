package com.github.mrodriguez.v2;

import com.example.Customer;
import com.hortonworks.registries.schemaregistry.client.SchemaRegistryClient;
import com.hortonworks.registries.schemaregistry.serdes.avro.kafka.KafkaAvroSerializer;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Collections;
import java.util.Properties;

public class KafkaAvroJavaProducerV2Demo {
    public static void main(String[] args) {
        Properties properties = new Properties();
        // normal producer
        properties.put("bootstrap.servers", "c389-node4.coelab.cloudera.com:9092");
        properties.put("acks", "all");
        properties.put("retries", "10");
        // avro part
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class.getName());
        properties.put("security.protocol", "SASL_PLAINTEXT");
        //properties.setProperty("schema.registry.url", "http://c389-node4.coelab.cloudera.com:7788");
        properties.putAll(Collections.singletonMap(SchemaRegistryClient.Configuration.SCHEMA_REGISTRY_URL.name(),"http://c389-node4.coelab.cloudera.com:7788/api/v1/"));
        Producer<String, Customer> producer = new KafkaProducer<String, Customer>(properties);

        String topic = "schema1";

        // copied from avro examples
        // copied from avro examples
        Customer customer = Customer.newBuilder()
                .setAge(34)
                .setFirstName("John")
                .setLastName("Doe")
                .setHeight(178f)
                .setWeight(75f)
                .setEmail("john.doe@gmail.com")
                .setPhoneNumber("(123)-456-7890")
                .build();

        ProducerRecord<String, Customer> producerRecord = new ProducerRecord<String, Customer>(
                topic, customer
        );

        System.out.println(customer);
        producer.send(producerRecord, new Callback() {
            @Override
            public void onCompletion(RecordMetadata metadata, Exception exception) {
                if (exception == null) {
                    System.out.println(metadata);
                } else {
                    exception.printStackTrace();
                }
            }
        });

        producer.flush();
        producer.close();

    }
}
