package com.briolink.companyservice.api.exception

class UnavailableException(override val message: String = "Service unavailable", serviceName: String?) :
    RuntimeException(if (serviceName != null) "$serviceName: $message" else message)
