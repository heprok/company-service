package com.briolink.companyservice.api.exception

class BadRequestException(override val message: String = "Bad request") : RuntimeException(message)
