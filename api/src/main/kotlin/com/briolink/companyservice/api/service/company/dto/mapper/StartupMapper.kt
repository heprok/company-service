package com.briolink.companyservice.api.service.company.dto.mapper

import com.briolink.companyservice.api.graphql.mapper.toFilterMutableSetStr
import com.briolink.companyservice.api.graphql.mapper.toFilterMutableSetWithUUID
import com.briolink.companyservice.api.graphql.mapper.toMoney
import com.briolink.companyservice.api.graphql.mapper.toMutableSetTags
import com.briolink.companyservice.api.graphql.mapper.toTag
import com.briolink.companyservice.api.service.company.dto.CreateStartupInfoDto
import com.briolink.companyservice.api.types.CreateStartupInfoInput

fun CreateStartupInfoInput.toDto() = CreateStartupInfoDto(
    totalPatentDocs = totalPatentDocs,
    revenue = revenue?.toMoney(),
    totalRaised = totalRaised?.toMoney(),
    lastFinancing = lastFinancing?.toMoney(),
    lastFinancingStatus = lastFinancingStatus?.toTag(),
    universe = universe?.toMutableSetTags() ?: mutableSetOf(),
    cpcCodes = cpcCodes?.toMutableSetTags() ?: mutableSetOf(),
    investors = investors?.toFilterMutableSetWithUUID() ?: mutableSetOf(),
    otherInvestors = investors?.toFilterMutableSetStr() ?: mutableSetOf(),
    potentialInvestors = potentialInvestors?.toFilterMutableSetWithUUID() ?: mutableSetOf(),
    servicesProviders = servicesProviders?.toFilterMutableSetWithUUID() ?: mutableSetOf(),
    otherServicesProviders = servicesProviders?.toFilterMutableSetStr() ?: mutableSetOf(),
    comparables = comparables?.toFilterMutableSetWithUUID() ?: mutableSetOf(),
    otherComparables = comparables?.toFilterMutableSetStr() ?: mutableSetOf(),
)
