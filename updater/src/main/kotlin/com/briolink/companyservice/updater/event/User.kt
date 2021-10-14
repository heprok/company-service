package com.briolink.companyservice.updater.event

import com.briolink.companyservice.updater.dto.User
import com.briolink.event.Event

data class UserCreatedEvent(override val data: User) : Event<User>("1.0")
data class UserUpdatedEvent(override val data: User) : Event<User>("1.0")
