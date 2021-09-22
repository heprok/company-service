package com.briolink.companyservice.api.graphql

import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import java.util.UUID

object SecurityUtil {
    val currentUserAccountId: UUID?
        get() {
            val authentication: Authentication = SecurityContextHolder.getContext().authentication
            return if (authentication is JwtAuthenticationToken) {
                UUID.fromString(authentication.token.subject)
            } else {
                null
            }
        }
}
