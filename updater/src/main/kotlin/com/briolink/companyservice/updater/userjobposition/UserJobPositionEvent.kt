package com.briolink.companyservice.updater.userjobposition

import com.briolink.event.Event

data class UserJobPositionCreatedEvent(override val data: UserJobPosition) : Event<UserJobPosition>("1.0")
data class UserJobPositionUpdatedEvent(override val data: UserJobPosition) : Event<UserJobPosition>("1.0")
data class UserJobPositionDeletedEvent(override val data: UserJobPositionDeletedData) : Event<UserJobPositionDeletedData>("1.0")
