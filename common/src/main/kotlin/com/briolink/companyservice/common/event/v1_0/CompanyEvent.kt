package com.briolink.companyservice.common.event.v1_0

import com.briolink.companyservice.common.domain.v1_0.Company
import com.briolink.companyservice.common.domain.v1_0.CompanyStatistic
import com.briolink.companyservice.common.domain.v1_0.CompanyStatisticConnection
import com.briolink.companyservice.common.domain.v1_0.Domain
import com.briolink.lib.event.Event
import com.briolink.lib.sync.SyncData
import com.briolink.lib.sync.SyncEvent

data class CompanyCreatedEvent(override val data: Company) : Event<Domain>("1.0")
data class CompanyUpdatedEvent(override val data: Company) : Event<Domain>("1.0")
data class CompanyStatisticEvent(override val data: CompanyStatistic) : Event<Domain>("1.0")
data class CompanyStatisticConnectionEvent(override val data: CompanyStatisticConnection) : Event<Domain>("1.0")
data class CompanySyncEvent(override val data: SyncData<Company>) : SyncEvent<Company>("1.0")
