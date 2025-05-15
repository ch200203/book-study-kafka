package com.study.chapter06.configuration

import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import org.springframework.kafka.transaction.KafkaTransactionManager

@Configuration
class KafkaProducerConfig {

    companion object {
        const val bootstrapServers = "localhost:19092, localhost:19093, localhost:19094"
    }

    // 여기서 여러개의 팩토리를 지정해서 사용가능함
    @Bean
    fun producerFactory(): ProducerFactory<String, String> =
        DefaultKafkaProducerFactory(
            mapOf(
                // 필수로 작성 필요 bootStrapServer, Serializer
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,

                ProducerConfig.ACKS_CONFIG to "1", // ack = 0, 1, all
                ProducerConfig.RETRIES_CONFIG to 10, // 재시도

                // 배치 전송 설정 (batch-size만큼 모으거나 linger.ms 경과시 전송)
                ProducerConfig.BATCH_SIZE_CONFIG to 16384,
                ProducerConfig.LINGER_MS_CONFIG to 20, // 지연(20ms)
                ProducerConfig.BUFFER_MEMORY_CONFIG to 33554432, // 버퍼 메모리 사이즈
            )
        )

    @Bean
    fun kafkaTemplate(): KafkaTemplate<String, String> = KafkaTemplate(producerFactory())

    /**
     * 빠른 전송용 Producer
     */
    @Bean
    fun fastProducerFactory(): ProducerFactory<String, String> =
        DefaultKafkaProducerFactory(
            mapOf(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
                ProducerConfig.ACKS_CONFIG to "1", // 빠른 전송
                ProducerConfig.LINGER_MS_CONFIG to 100,
                ProducerConfig.BATCH_SIZE_CONFIG to 16384,
                ProducerConfig.COMPRESSION_TYPE_CONFIG to "gzip"
            )
        )

    @Bean
    fun fastKafkaTemplate() = KafkaTemplate(fastProducerFactory())

    /**
     * 안전한 전송용 Producer
     */
    @Bean
    fun safeProducerFactory(): ProducerFactory<String, String> =
        DefaultKafkaProducerFactory(
            mapOf(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
                ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG to true,
                ProducerConfig.ACKS_CONFIG to "all",
                ProducerConfig.RETRIES_CONFIG to Int.MAX_VALUE,
                ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION to 5,
                ProducerConfig.TRANSACTIONAL_ID_CONFIG to "safe-tx-1"
            )
        )

    @Bean
    fun safeKafkaTemplate() = KafkaTemplate(safeProducerFactory()).apply {
        transactionIdPrefix = "safe-tx-"
    }

    // 트랜잭션 테스트를 위한 프로듀서 팩토리
    /**
     * Exactly Once 전송용 ProducerFactory
     */
    @Bean
    fun exactlyOnceProducerFactory(): ProducerFactory<String, String> =
        DefaultKafkaProducerFactory<String, String>(
            mapOf(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,

                ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG to true,
                ProducerConfig.ACKS_CONFIG to "all",
                ProducerConfig.RETRIES_CONFIG to Int.MAX_VALUE,
                ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION to 5,
                ProducerConfig.TRANSACTIONAL_ID_CONFIG to "tx-id"
            )
        )

    /**
     * Exactly Once KafkaTemplate (트랜잭션 사용)
     */
    @Bean
    fun exactlyOnceKafkaTemplate(): KafkaTemplate<String, String> =
        KafkaTemplate(exactlyOnceProducerFactory()).apply {
            transactionIdPrefix = "exactly-tx"
        }

    /**
     * 트랜잭션 매니저 (Spring @Transactional 또는 Listener 트랜잭션용)
     */
    @Bean
    open fun kafkaTransactionManager(): KafkaTransactionManager<String, String> =
        KafkaTransactionManager(exactlyOnceProducerFactory())

}
