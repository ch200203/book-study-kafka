package com.study.bookstudykafka.ch5

import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer
import java.util.Properties

fun main() {

    val props = Properties()
    props[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = "localhost:19092"
    props[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java.name
    props[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java.name
    props[ProducerConfig.BATCH_SIZE_CONFIG] = 16384 // 16KB (충분히 큰 값)¬
    props[ProducerConfig.LINGER_MS_CONFIG] = 20 // 20ms
    props[ProducerConfig.ACKS_CONFIG] = "all"

    val producer = KafkaProducer<String, String>(props)

    val topic = "test-topic"

    val startTime = System.currentTimeMillis()

    for (i in 1..4) {
        val message = "linger test message $i"
        println("[${System.currentTimeMillis() - startTime}ms] Send -> $message")

        producer.send(ProducerRecord(topic, message)) { metadata, exception ->
            if (exception == null) {
                println("[SEND COMPLETE] offset=${metadata.offset()}, partition=${metadata.partition()}, timestamp=${metadata.timestamp()}")
            } else {
                println("[SEND FAILED] ${exception.message}")
            }
        }

        Thread.sleep(5) // 200ms 간격으로 천천히 보냄 => 일부러 batch-size 안 채우기
    }

    // 대기
    println("linger.ms 초과할때까지 기다립니다.")
    Thread.sleep(500)

    producer.flush()
    producer.close()
}