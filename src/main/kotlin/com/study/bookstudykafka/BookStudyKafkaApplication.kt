package com.study.bookstudykafka

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class BookStudyKafkaApplication

fun main(args: Array<String>) {
    runApplication<BookStudyKafkaApplication>(*args)
}
