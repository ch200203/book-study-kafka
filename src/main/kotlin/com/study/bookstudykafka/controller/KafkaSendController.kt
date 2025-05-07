package com.study.bookstudykafka.controller

import com.study.bookstudykafka.service.KafkaProducerService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/kafka")
class KafkaSendController(
    private val service: KafkaProducerService,
) {

    @PostMapping("/send")
    fun send(@RequestParam topic: String, @RequestParam message: String): ResponseEntity<String> {
        service.send(topic, message)
        return ResponseEntity.ok("Sent to $topic")
    }

    @PostMapping("/send-batch")
    fun sendBatch(@RequestParam topic: String, @RequestBody messages: List<String>): ResponseEntity<String> {
        service.sendBatchMessages(topic, messages)
        return ResponseEntity.ok("Sent batch messages to $topic")
    }

    @PostMapping("/send-tx")
    fun sendTransactional(@RequestParam topic: String, @RequestBody messages: List<String>): ResponseEntity<String> {
        service.sendTransactionalMessages(topic, messages)
        return ResponseEntity.ok("Sent transactional messages to $topic")
    }

    @PostMapping("/fast")
    fun sendFast(@RequestParam topic: String, @RequestParam message: String): ResponseEntity<String> {
        service.sendFast(topic, message)
        return ResponseEntity.ok("Fast sent: $message")
    }

    @PostMapping("/safe")
    fun sendSafe(@RequestParam topic: String, @RequestBody messages: List<String>): ResponseEntity<String> {
        service.sendSafeTransactional(topic, messages)
        return ResponseEntity.ok("Safe sent: $messages")
    }
}
