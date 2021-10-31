package com.briolink.companyservice.updater.event

import com.briolink.companyservice.updater.dto.CompanyService
import com.briolink.event.Event

data class CompanyServiceCreatedEvent(override val data: CompanyService) : Event<CompanyService>("1.0")
data class CompanyServiceUpdatedEvent(override val data: CompanyService) : Event<CompanyService>("1.0")
