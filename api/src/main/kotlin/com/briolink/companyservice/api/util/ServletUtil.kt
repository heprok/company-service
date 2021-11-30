package com.briolink.companyservice.api.util

import org.springframework.stereotype.Component
import javax.servlet.http.HttpServletRequest

@Component
class ServletUtil(private val request: HttpServletRequest) {
    fun isIntranet(): Boolean = intranetServerNamePattern.matches(request.serverName)

    companion object {
        val intranetServerNamePattern = "[\\w-]+\\.[\\w-]+\\.svc\\.cluster\\.local$".toRegex()
    }
}