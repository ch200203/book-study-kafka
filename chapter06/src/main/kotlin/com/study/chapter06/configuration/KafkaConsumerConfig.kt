package com.study.chapter06.configuration

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory

@Configuration
class KafkaConsumerConfig {

    companion object {
        const val BOOTSTRAP_SERVERS = "localhost:19092,localhost:19093,localhost:19094"
    }

    private fun baseConsumerConfig(groupId: String) = mapOf<String, Any>(
        ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to BOOTSTRAP_SERVERS,
        ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
        ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
        ConsumerConfig.GROUP_ID_CONFIG to groupId,
        ConsumerConfig.AUTO_OFFSET_RESET_CONFIG to "earliest"
    )

    @Bean("DefaultConsumerFactory")
    fun rangeConsumerFactory(): ConsumerFactory<String, String> =
        DefaultKafkaConsumerFactory(
            baseConsumerConfig("range-group") + mapOf(
                ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG to "org.apache.kafka.clients.consumer.RangeAssignor"
            )
        )

    @Bean("roundRobinConsumerFactory")
    fun roundRobinConsumerFactory(): ConsumerFactory<String, String> =
        DefaultKafkaConsumerFactory(
            baseConsumerConfig("roundrobin-group") + mapOf(
                ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG to "org.apache.kafka.clients.consumer.RoundRobinAssignor"
            )
        )

    @Bean("stickyConsumerFactory")
    fun stickyConsumerFactory(): ConsumerFactory<String, String> =
        DefaultKafkaConsumerFactory(
            baseConsumerConfig("sticky-group") + mapOf(
                ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG to "org.apache.kafka.clients.consumer.StickyAssignor"
            )
        )

    @Bean("cooperativeStickyConsumerFactory")
    fun cooperativeStickyConsumerFactory(): ConsumerFactory<String, String> =
        DefaultKafkaConsumerFactory(
            baseConsumerConfig("coop-sticky-group") + mapOf(
                ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG to "org.apache.kafka.clients.consumer.CooperativeStickyAssignor"
            )
        )

    @Bean("exactlyOnceConsumerFactory")
    fun exactlyOnceConsumerFactory(): ConsumerFactory<String, String> =
        DefaultKafkaConsumerFactory(
            baseConsumerConfig("exactly-once-group") + mapOf(
                ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG to false, // 명시적 커밋
                ConsumerConfig.ISOLATION_LEVEL_CONFIG to "read_committed"
            )
        )


    @Bean("DefaultContainerFactory")
    fun rangeContainerFactory(@Qualifier("DefaultConsumerFactory") factory: ConsumerFactory<String, String>) =
        ConcurrentKafkaListenerContainerFactory<String, String>().apply { consumerFactory = factory }

    @Bean("roundRobinContainerFactory")
    fun roundRobinContainerFactory(@Qualifier("roundRobinConsumerFactory") factory: ConsumerFactory<String, String>) =
        ConcurrentKafkaListenerContainerFactory<String, String>().apply { consumerFactory = factory }

    @Bean("stickyContainerFactory")
    fun stickyContainerFactory(@Qualifier("stickyConsumerFactory") factory: ConsumerFactory<String, String>) =
        ConcurrentKafkaListenerContainerFactory<String, String>().apply { consumerFactory = factory }

    @Bean("cooperativeStickyContainerFactory")
    fun cooperativeStickyContainerFactory(@Qualifier("cooperativeStickyConsumerFactory") factory: ConsumerFactory<String, String>) =
        ConcurrentKafkaListenerContainerFactory<String, String>().apply { consumerFactory = factory }

    @Bean("exactlyOnceContainerFactory")
    fun exactlyOnceContainerFactory(
        @Qualifier("exactlyOnceConsumerFactory") factory: ConsumerFactory<String, String>
    ): ConcurrentKafkaListenerContainerFactory<String, String> =
        ConcurrentKafkaListenerContainerFactory<String, String>().apply {
            consumerFactory = factory
        }
}
