package com.study.chapter10

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class SendController(
    private val service: SendService
) {

    @PostMapping("/send")
    fun send(@RequestParam topic: String, @RequestParam message: String): ResponseEntity<String> {
        service.send(topic, message)
        return ResponseEntity.ok("Sent to $topic")
    }
}