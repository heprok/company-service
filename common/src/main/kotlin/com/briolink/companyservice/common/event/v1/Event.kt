package com.briolink.companyservice.common.event.v1

import com.briolink.companyservice.common.domain.v1.Domain

abstract class Event<T : com.briolink.companyservice.common.domain.v1.Domain> {
    abstract val data: T

    val event: String = this.javaClass.simpleName
    val timestamp: Long = System.currentTimeMillis()
    val version: String = "1.0"
}
