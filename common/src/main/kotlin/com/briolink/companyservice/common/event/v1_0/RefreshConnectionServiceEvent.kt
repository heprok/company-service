package com.briolink.companyservice.common.event.v1_0

import com.briolink.companyservice.common.domain.v1_0.CompanyConnectionDeletedEventData
import com.briolink.companyservice.common.domain.v1_0.CompanyConnectionHideEventData
import com.briolink.companyservice.common.domain.v1_0.CompanyServiceRefreshVerifyUses
import com.briolink.companyservice.common.domain.v1_0.Domain
import com.briolink.event.Event

data class RefreshConnectionServiceEvent(override val data: CompanyServiceRefreshVerifyUses) : Event<Domain>("1.0")
data class CompanyConnectionChangeVisibilityEvent(override val data: CompanyConnectionHideEventData) : Event<Domain>("1.0")
data class CompanyConnectionDeletedEvent(override val data: CompanyConnectionDeletedEventData) : Event<Domain>("1.0")
