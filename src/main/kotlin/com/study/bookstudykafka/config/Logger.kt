package com.study.bookstudykafka.config

import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * 로그 전역 설정
 */
inline fun <reified T: Any> T.logger(): Logger = LoggerFactory.getLogger(T::class.java)