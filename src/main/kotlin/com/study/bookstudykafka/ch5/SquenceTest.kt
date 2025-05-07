package com.study.bookstudykafka.ch5

import org.apache.kafka.clients.producer.*
import org.apache.kafka.common.serialization.StringSerializer
import java.util.*
import java.util.concurrent.atomic.AtomicInteger


fun main() {
    println("==== 순서 테스트 시작 ====")

    // --- 실험 A (순서 깨짐 가능성)
    println("\n---- [실험 A] enable.idempotence = false, max.in.flight = 5 ----")
    runTest(idempotent = false, maxInFlight = 5, "test-order-topic", ackValue = "0")

    // --- 실험 B (순서 보장)
    println("\n---- [실험 B] enable.idempotence = true, max.in.flight = 1 ----")
    runTest(idempotent = true, maxInFlight = 1, "test-order-topic", ackValue = "all")
}

fun runTest(idempotent: Boolean, maxInFlight: Int, topic: String, ackValue: String) {
    val props = Properties().apply {
        put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:19092")
        put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer::class.java.name)
        put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer::class.java.name)
        put(ProducerConfig.LINGER_MS_CONFIG, "1")
        put(ProducerConfig.BATCH_SIZE_CONFIG, "16384")
        put(ProducerConfig.ACKS_CONFIG, ackValue)
        put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, idempotent)
        //  props[ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG] = false // 일단 false로 테스트
        put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, maxInFlight)
        put(ProducerConfig.RETRIES_CONFIG, Int.MAX_VALUE.toString())
        put(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG, FailingInterceptor::class.java.name)
    }

    val producer = KafkaProducer<String, String>(props)

    println("==== Kafka Producer 초기화 완료 ====")

    Thread.sleep(2000)

    for (i in 1..10) {
        val record = ProducerRecord<String, String>(topic, "key", "message-$i")
        producer.send(record) { metadata, exception ->
            if (exception == null) {
                println("[SENT] ${record.value()} → partition=${metadata.partition()}, offset=${metadata.offset()}")
            } else {
                println("[FAILED] ${record.value()} → ${exception.message}")
            }
        }

        // 의도적으로 3번째 메시지에서 딜레이 (재시도 유도)
        if (i % 3 == 0) {
            Thread.sleep(5000)
        } else {
            Thread.sleep(5)
        }
    }

    producer.flush()
    producer.close()
}


class FailingInterceptor : ProducerInterceptor<String, String> {

    private val counter = AtomicInteger()

    override fun configure(configs: MutableMap<String, *>?) {}

    override fun onSend(record: ProducerRecord<String, String>): ProducerRecord<String, String> {
        val count = counter.incrementAndGet()

        // 3번째 메시지마다 일부러 예외 발생
        if (count % 3 == 0) {
            println("[Interceptor] 인위적 오류 발생 -> ${record.value()}")
            throw RuntimeException("인위적인 예외 발생 (for test)")
        }

        return record
    }

    override fun onAcknowledgement(metadata: RecordMetadata?, exception: Exception?) {}

    override fun close() {}
}