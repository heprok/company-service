package com.briolink.companyservice.common.event.v1

data class KeyWordCreatedEvent(override val data: com.briolink.companyservice.common.domain.v1.KeyWord) : com.briolink.companyservice.common.event.v1.Event<com.briolink.companyservice.common.domain.v1.KeyWord>()
data class KeyWordUpdatedEvent(override val data: com.briolink.companyservice.common.domain.v1.KeyWord) : com.briolink.companyservice.common.event.v1.Event<com.briolink.companyservice.common.domain.v1.KeyWord>()
data class KeyWordDeletedEvent(override val data: com.briolink.companyservice.common.domain.v1.KeyWord) : com.briolink.companyservice.common.event.v1.Event<com.briolink.companyservice.common.domain.v1.KeyWord>()
