package com.ing.assignment.ordermanager.config;

import com.ing.assignment.ordercommon.dto.PlaceOrder;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuration class for kafka. Contains {@link Bean}s required for the producers and consumers with
 * the necessary config values. Takes care of creating topics for process orders workflow.
 */
@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.max-poll-records}")
    private String maxPollRecords;

    @Value("${spring.kafka.topic.process-truck-orders}")
    private String processTruckOrdersTopic;

    @Value("${spring.kafka.topic.process-car-orders}")
    private String processCarOrdersTopic;

    @Value("${spring.kafka.consumer.car-orders-fb.group-id}")
    private String carFBGroupId;

    @Value("${spring.kafka.consumer.car-orders-fb.group-id}")
    private String truckFBGroupId;

    private static final short DEFAULT_REPLICATION_FACTOR = 1;
    private static final int DEFAULT_PARTITIONS_FACTOR = 1;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        return new KafkaAdmin(Map.of(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers));
    }

    @Bean
    public KafkaTemplate<String, PlaceOrder> placeOrderKafkaTemplate() {
        Map<String, Object> producerConfigs = new HashMap<>();
        producerConfigs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        producerConfigs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerConfigs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(producerConfigs));
    }

    @Bean
    public NewTopic processCarOrdersTopic() {
        return new NewTopic(processCarOrdersTopic, DEFAULT_PARTITIONS_FACTOR, DEFAULT_REPLICATION_FACTOR);
    }

    @Bean
    public NewTopic processTruckOrdersTopic() {
        return new NewTopic(processTruckOrdersTopic, DEFAULT_PARTITIONS_FACTOR, DEFAULT_REPLICATION_FACTOR);
    }

    @Bean
    public ConsumerFactory<String, Object> carFBConsumerFactory() {
        return createConsumerFactory(carFBGroupId);
    }

    @Bean
    public ConsumerFactory<String, Object> truckFBConsumerFactory() {
        return createConsumerFactory(truckFBGroupId);
    }

    private ConsumerFactory<String, Object> createConsumerFactory(String groupId) {
        Map<String, Object> props = new HashMap<>();

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, maxPollRecords);

        return new DefaultKafkaConsumerFactory<>(props);
    }
}
