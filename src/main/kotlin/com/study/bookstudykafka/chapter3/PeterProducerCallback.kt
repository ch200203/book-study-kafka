package com.study.bookstudykafka.chapter3

import org.apache.kafka.clients.producer.Callback
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.clients.producer.RecordMetadata


class PeterProducerCallback : Callback {
    private var record: ProducerRecord<String, String>? = null
    fun PeterProducerCallback(record: ProducerRecord<String, String>?) {
        this.record = record
    }

    override fun onCompletion(metadata: RecordMetadata, e: Exception?) {
        e?.printStackTrace() //카프카가 오류를 리턴하면 onCompletion()은 예외를 갖게 되며, 실제 운영환경에서는 추가적인 예외처리가 필요합니다.
            ?: System.out.printf(
                "Topic: %s, Partition: %d, Offset: %d, Key: %s, Received Message: %s\n",
                metadata.topic(),
                metadata.partition(),
                metadata.offset(),
                record!!.key(),
                record!!.value()
            )
    }
}
