package com.study.chapter06

import com.study.chapter06.configuration.logger
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SampleProducer(
    private val kafkaTemplate: KafkaTemplate<String, String>,
    @Qualifier("exactlyOnceKafkaTemplate")
    private val exactlyKafkaTemplate: KafkaTemplate<String, String>
) {

    companion object {
        val log = logger()
    }

    // 단일 전송
    fun send(topic: String, message: String?) {
        kafkaTemplate.send(topic, message)
    }

    // 정확히 한번 전송을 위한 Send
    fun sendTransactional(topic: String, key: String, message: String) {
        exactlyKafkaTemplate.executeInTransaction {
            it.send(topic, key, message)

            log.info("[TX] Sent message: key=$key, message=$message to topic=$topic")
        }
    }

}
