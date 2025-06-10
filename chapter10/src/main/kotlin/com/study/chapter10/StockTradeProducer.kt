package com.study.chapter10

import com.example.avro.schema.StockTrade
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class StockTradeProducer(
    private val kafkaTemplate: KafkaTemplate<String, Any>, // StockTrade 와 같이 데이터 맞춰주는게 더 좋긴함
) {
    private val topicName = "stock-trades"

    companion object {
        val log = logger()

    }

    fun sendMessage(trade: StockTrade) {
        log.info("Sending message to Kafka topic: $topicName, message: $trade")
        kafkaTemplate.send(topicName, trade.ticker.toString(), trade)
    }

    // 잘못된 schema 를 넘기는 경우 차단.
    fun sendInvalidMessage(trade: StockTrade) {
        log.info("Sending message to Kafka topic: $topicName, message: $trade")
        kafkaTemplate.send(topicName, trade.ticker.toString(), trade)
    }
}
