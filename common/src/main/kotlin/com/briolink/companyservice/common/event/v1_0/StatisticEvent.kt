package com.briolink.companyservice.common.event.v1_0

import com.briolink.companyservice.common.domain.v1_0.Domain
import com.briolink.companyservice.common.domain.v1_0.StatisticRefresh
import com.briolink.event.Event

data class StatisticRefreshEvent(override val data: StatisticRefresh) : Event<Domain>("1.0")
