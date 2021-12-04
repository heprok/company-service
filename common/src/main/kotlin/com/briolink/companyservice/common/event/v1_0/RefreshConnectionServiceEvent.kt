package com.briolink.companyservice.common.event.v1_0

import com.briolink.companyservice.common.domain.v1_0.CompanyServiceRefreshVerifyUses
import com.briolink.companyservice.common.domain.v1_0.Domain
import com.briolink.event.Event

data class RefreshConnectionServiceEvent(override val data: CompanyServiceRefreshVerifyUses) : Event<Domain>("1.0")
