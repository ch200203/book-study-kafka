package com.study.chapter06.consumer

import com.study.chapter06.configuration.logger
import org.apache.kafka.clients.consumer.ConsumerGroupMetadata
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.OffsetAndMetadata
import org.apache.kafka.common.TopicPartition
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class SampleConsumer(
    @Qualifier("exactlyOnceKafkaTemplate")
    private val kafkaTemplate: KafkaTemplate<String, String>
) {

    companion object {
        val log = logger()
    }

    @KafkaListener(topics = ["test-topic"], containerFactory = "DefaultContainerFactory")
    fun rangeListener(record: ConsumerRecord<String, String>) {
        log.info("[Range] Partition: ${record.partition()}, Offset: ${record.offset()}, Message: ${record.value()}")
    }

    @KafkaListener(topics = ["test-topic"], containerFactory = "roundRobinContainerFactory")
    fun roundRobinListener(record: ConsumerRecord<String, String>) {
        log.info("[RoundRobin] Partition: ${record.partition()}, Offset: ${record.offset()}, Message: ${record.value()}")
    }

    @KafkaListener(topics = ["test-topic"], containerFactory = "stickyContainerFactory")
    fun stickyListener(record: ConsumerRecord<String, String>) {
        log.info("[Sticky] Partition: ${record.partition()}, Offset: ${record.offset()}, Message: ${record.value()}")
    }

    @KafkaListener(topics = ["test-topic"], containerFactory = "cooperativeStickyContainerFactory")
    fun coopStickyListener(record: ConsumerRecord<String, String>) {
        log.info("[CooperativeSticky] Partition: ${record.partition()}, Offset: ${record.offset()}, Message: ${record.value()}")
    }

    /**
     * sendOffsetsToTransaction() 방식은 acknowledgment 수동 커밋을 필요하지 않음
     * 필요하다만 acknowledgment.acknowledge() 형태로 사용
     *
     * 이걸 사용하려 AckMode를 MANUAL 또는 MANUAL_IMMEDIATE로 설정해야함
     */
    @KafkaListener(topics = ["exactly-topic"], containerFactory = "exactlyOnceContainerFactory")
    @Transactional("kafkaTransactionManager")
    fun processExactlyOnce(record: ConsumerRecord<String, String>) {
        log.info("[processExactlyOnce] Partition: ${record.partition()}, Offset: ${record.offset()}, Message: ${record.value()}")
        // 오프셋 커밋을 트랜잭션에 포함
        val offsets = mapOf(
            TopicPartition(record.topic(), record.partition()) to OffsetAndMetadata(record.offset() + 1)
        )

        kafkaTemplate.sendOffsetsToTransaction(offsets, ConsumerGroupMetadata("exactly-once-group"))

        log.info("Exactly-once handled: ${record.value()}")
    }
}
