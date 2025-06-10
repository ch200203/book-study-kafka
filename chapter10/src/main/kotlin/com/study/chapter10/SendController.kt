package com.study.chapter10

import com.example.avro.schema.StockTrade
import com.example.avro.schema.TradeType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Instant

@RestController
@RequestMapping("/trades")
class SendController(
    private val producer: StockTradeProducer
) {

    companion object {
        private val log = logger()
    }

    @PostMapping
    fun createTrade(@RequestBody request: TradeRequest) {
        log.info("requestData: {}", request.toString())
        // DTO를 Avro 생성 클래스로 변환
        val stockTrade = StockTrade().apply {
            ticker = request.ticker
            quantity = request.quantity
            price = request.price
            tradeType = TradeType.valueOf(request.tradeType.uppercase())
            tradeTimestamp = Instant.now()
        }
        producer.sendMessage(stockTrade)
    }

    @PostMapping("/invalid")
    fun invalidTrade(@RequestBody request: TradeRequest) {
        // DTO를 Avro 생성 클래스로 변환
        val stockTrade = StockTrade().apply {
            ticker = request.ticker
            quantity = request.quantity
            price = request.price
            tradeType = TradeType.valueOf(request.tradeType.uppercase())
            tradeTimestamp = Instant.now()
            // traderId = "test-user-id"
        }
        producer.sendMessage(stockTrade)
    }


    data class TradeRequest(val ticker: String, val quantity: Int, val price: Double, val tradeType: String)
}