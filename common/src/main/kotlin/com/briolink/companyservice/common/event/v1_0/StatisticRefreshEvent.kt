package com.briolink.companyservice.common.event.v1_0

import com.briolink.companyservice.common.domain.v1_0.Domain
import com.briolink.companyservice.common.domain.v1_0.Statistic
import com.briolink.lib.event.Event

data class StatisticRefreshEvent(override val data: Statistic) : Event<Domain>("1.0")
