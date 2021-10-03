package com.briolink.companyservice.common.event.v1_0

import com.briolink.companyservice.common.domain.v1_0.User
import com.briolink.event.Event

data class UserCreatedEvent(override val data: User) : Event<User>("1.0")
data class UserUpdatedEvent(override val data: User) : Event<User>("1.0")
