package com.study.chapter10

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class Chapter10Application

fun main(args: Array<String>) {
    runApplication<Chapter10Application>(*args)
}

inline fun <reified T : Any> T.logger(): Logger = LoggerFactory.getLogger(T::class.java)