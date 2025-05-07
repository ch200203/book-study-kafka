package com.study.bookstudykafka.service

import com.study.bookstudykafka.config.logger
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class KafkaProducerService(
    private val kafkaTemplate: KafkaTemplate<String, String>,

    @Qualifier("fastKafkaTemplate")
    private val fastKafkaTemplate: KafkaTemplate<String, String>,

    @Qualifier("safeKafkaTemplate")
    private val safeKafkaTemplate: KafkaTemplate<String, String>,
) {
    companion object {
        private val log = logger()
    }
    /**
     * 단일 메시지 전송
     */
    fun send(topic: String, message: String?) {
        kafkaTemplate.send(topic, message)
    }

    /**
     * 배치 메시지 전송
     */
    fun sendBatchMessages(topic: String, messages: List<String>) {
        messages.forEach { message ->
            kafkaTemplate.send(topic, message)
            log.info("Sent batch message: $message")
        }
    }

    /**
     * 트랜잭션 전송 (Exactly Once)
     */
    fun sendTransactionalMessages(topic: String, messages: List<String>) {
        kafkaTemplate.executeInTransaction { operations ->
            messages.forEach { message ->
                operations.send(topic, message)

            }
        }
    }

    /**
     * 빠른 전송 (유실 가능성 있음, 지연 적음)
     */
    fun sendFast(topic: String, message: String) {
        log.info("[FAST] Sending -> $message")
        fastKafkaTemplate.send(topic, message)
    }

    /**
     * 정확한 전송 (트랜잭션으로 Exactly Once)
     */
    fun sendSafeTransactional(topic: String, messages: List<String>) {
        println("[SAFE] Transactional sending -> $messages")
        safeKafkaTemplate.executeInTransaction { ops ->
            messages.forEach { ops.send(topic, it) }
        }
    }

}
