package com.study.chapter06.consumer

import com.study.chapter06.configuration.logger
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class SampleConsumer {

    companion object {
        val log = logger()
    }

    @KafkaListener(topics = ["test-topic"], groupId = "test-group")
    fun listen(record: ConsumerRecord<String, String>) {
        val topic = record.topic()
        val partition = record.partition()
        val offset = record.offset()
        val message = record.value()

        log.info("Received message topic: $topic, partition: $partition, offset: $offset, value: $message")
    }
}
