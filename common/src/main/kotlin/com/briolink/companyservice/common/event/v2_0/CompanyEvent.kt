package com.briolink.companyservice.common.event.v2_0

import com.briolink.companyservice.common.domain.v2_0.Company
import com.briolink.companyservice.common.domain.v2_0.Domain
import com.briolink.lib.event.Event
import com.briolink.lib.sync.SyncData
import com.briolink.lib.sync.SyncEvent

data class CompanyCreatedEvent(override val data: Company) : Event<Domain>("2.0")
data class CompanyUpdatedEvent(override val data: Company) : Event<Domain>("2.0")
data class CompanySyncEvent(override val data: SyncData<Company>) : SyncEvent<Company>("2.0")
