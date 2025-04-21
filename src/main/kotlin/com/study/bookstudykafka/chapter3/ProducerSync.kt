package com.study.bookstudykafka.chapter3

import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.clients.producer.RecordMetadata
import java.util.*


class ProducerSync {

}

fun main(args: Array<String>) {
    val props: Properties = Properties() //Properties 오브젝트를 시작합니다.
    props.put(
        "bootstrap.servers",
        "peter-kafka01.foo.bar:9092,peter-kafka02.foo.bar:9092,peter-kafka03.foo.bar:9092"
    ) //브로커 리스트를 정의합니다.
    props.put(
        "key.serializer",
        "org.apache.kafka.common.serialization.StringSerializer"
    ) //메시지 키와 벨류에 문자열을 지정하므로 내장된 StringSerializer를 지정합니다.
    props.put(
        "value.serializer",
        "org.apache.kafka.common.serialization.StringSerializer"
    )

    val producer: Producer<Any?, Any?> = KafkaProducer<Any?, Any?>(props) //Properties 오브젝트를 전달해 새 프로듀서를 생성합니다.

    try {
        for (i in 0..2) {
            val record = ProducerRecord<Any?, Any?>(
                "peter-basic01",
                "Apache Kafka is a distributed streaming platform - $i"
            ) //ProducerRecord 오브젝트를 생성합니다.
            val metadata: RecordMetadata = producer.send(record)
                .get() //get() 메소드를 이용해 카프카의 응답을 기다립니다. 메시지가 성공적으로 전송되지 않으면 예외가 발생하고, 에러가 없다면 RecordMetadata를 얻게 됩니다.
            System.out.printf(
                "Topic: %s, Partition: %d, Offset: %d, Key: %s, Received Message: %s\n",
                metadata.topic(),
                metadata.partition(),
                metadata.offset(),
                record.key(),
                record.value()
            )
        }
    } catch (e: Exception) {
        e.printStackTrace() //카프카로 메시지를 보내기 전과 보내는 동안 에러가 발생하면 예외가 발생합니다.
    } finally {
        producer.close() // 프로듀서 종료
    }
}
