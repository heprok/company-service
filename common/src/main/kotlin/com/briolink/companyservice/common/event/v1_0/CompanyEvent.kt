package com.briolink.companyservice.common.event.v1_0

import com.briolink.companyservice.common.domain.v1_0.Company
import com.briolink.companyservice.common.domain.v1_0.CompanyStatistic
import com.briolink.companyservice.common.domain.v1_0.Domain
import com.briolink.event.Event
import com.briolink.lib.sync.ISyncEvent
import com.briolink.lib.sync.enumeration.ServiceEnum

data class CompanyCreatedEvent(override val data: Company) : Event<Domain>("1.0")
data class CompanyUpdatedEvent(override val data: Company) : Event<Domain>("1.0")
data class CompanyStatisticEvent(override val data: CompanyStatistic) : Event<Domain>("1.0")
data class CompanySyncEvent(
    override val service: ServiceEnum,
    override val indexRow: Long,
    override val totalElements: Long,
    override val syncId: Int,
    override val isLastData: Boolean = true,
    override val data: Company
) : Event<Domain>("1.0"), ISyncEvent
