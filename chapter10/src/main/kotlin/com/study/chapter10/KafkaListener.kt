package com.study.chapter10

import com.example.avro.schema.StockTrade
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class KafkaListener {

    companion object {
        val log = logger()
    }

    @KafkaListener(topics = ["stock-trades"], groupId = "test-group")
    fun consume(message: StockTrade) {
        log.info("Consumed message -> ${message.toString()}")
    }
}
