package com.study.bookstudykafka.service

import com.study.bookstudykafka.config.logger
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class KafkaConsumerService {

    companion object {
        val log = logger()
    }

    @KafkaListener(topics = ["test-topic"], groupId = "test-group")
    fun listen(message: String) {
        log.info("Received: $message")
    }

    @KafkaListener(
        topics = ["batch-test-topic"],
        groupId = "batch-group",
        containerFactory = "batchKafkaListenerContainerFactory"
    )
    fun listenBatch(messages: List<String>) {
        log.info("===== Batch Received (${messages.size} messages) =====")
        messages.forEach { println(it) }
    }
}
