package com.study.chapter06

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/kafka")
class KafkaSendController(
    private val sampleProducer: SampleProducer,
) {

    @PostMapping("/send")
    fun send(@RequestParam topic: String, @RequestParam message: String): ResponseEntity<String> {
        sampleProducer.send(topic, message)
        return ResponseEntity.ok("Sent to $topic")
    }
}
