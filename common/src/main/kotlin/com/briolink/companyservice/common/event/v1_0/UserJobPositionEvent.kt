package com.briolink.companyservice.common.event.v1_0

import com.briolink.companyservice.common.domain.v1_0.Domain
import com.briolink.companyservice.common.domain.v1_0.UserJobPosition
import com.briolink.companyservice.common.domain.v1_0.UserJobPositionDeletedData
import com.briolink.event.Event

data class UserJobPositionCreatedEvent(override val data: UserJobPosition) : Event<Domain>("1.0")
data class UserJobPositionUpdatedEvent(override val data: UserJobPosition) : Event<Domain>("1.0")
data class UserJobPositionDeletedEvent(override val data: UserJobPositionDeletedData) : Event<Domain>("1.0")
