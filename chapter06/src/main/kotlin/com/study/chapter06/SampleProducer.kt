package com.study.chapter06

import com.study.chapter06.configuration.logger
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class SampleProducer(
    private val kafkaTemplate: KafkaTemplate<String, String>
) {

    companion object {
        val log = logger()
    }

    // 단일 전송
    fun send(topic: String, message: String?) {
        kafkaTemplate.send(topic, message)
    }

}
