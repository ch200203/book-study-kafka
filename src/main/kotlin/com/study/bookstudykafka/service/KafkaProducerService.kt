package com.study.bookstudykafka.service

import org.springframework.boot.autoconfigure.kafka.KafkaProperties.Retry.Topic
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class KafkaProducerService(
    private val kafkaTemplate: KafkaTemplate<String, String>
) {
    fun send(topic: String, message: String?) {
        kafkaTemplate.send(topic, message)
    }
}
