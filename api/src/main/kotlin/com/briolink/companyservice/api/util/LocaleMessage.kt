package com.briolink.companyservice.api.util

import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.stereotype.Component

@Component
class LocaleMessage(private val messageSource: MessageSource) {
    fun getMessage(key: String): String = try {
        messageSource.getMessage(key, null, LocaleContextHolder.getLocale())
    } catch (e: Exception) {
        key
    }
}
