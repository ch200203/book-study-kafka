package com.study.bookstudykafka.service

import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class KafkaConsumerService(

) {
    @KafkaListener(topics = ["test-topic"], groupId = "test-group")
    fun listen(message: String) {
        println("Received: $message")
    }
}
