package com.briolink.companyservice.api.service.company.dto.mapper

import com.briolink.companyservice.api.graphql.mapper.toMoney
import com.briolink.companyservice.api.graphql.mapper.toMutableSetTags
import com.briolink.companyservice.api.graphql.mapper.toTag
import com.briolink.companyservice.api.service.company.dto.CreateServiceProviderInfoDto
import com.briolink.companyservice.api.service.company.dto.ServiceProviderDealDto
import com.briolink.companyservice.api.service.company.dto.ServiceProviderFundDto
import com.briolink.companyservice.api.service.company.dto.ServiceProviderLeadPartnerDto
import com.briolink.companyservice.api.service.company.dto.ServiceProviderPrimaryContactDto
import com.briolink.companyservice.api.service.company.dto.ServiceProviderServicedDto
import com.briolink.companyservice.api.types.CreateLeadPartnerInput
import com.briolink.companyservice.api.types.CreateServiceProviderDealInput
import com.briolink.companyservice.api.types.CreateServiceProviderFundInput
import com.briolink.companyservice.api.types.CreateServiceProviderInfoInput
import com.briolink.companyservice.api.types.CreateServiceProviderPrimaryContactInput
import com.briolink.companyservice.api.types.CreateServiceProviderServicedInput

fun CreateServiceProviderInfoInput.toDto() = CreateServiceProviderInfoDto(
    primaryType = primaryType?.toTag(),
    otherTypes = otherTypes?.toMutableSetTags() ?: mutableSetOf(),
    servicedCompanies = servicedCompanies,
    serviced = serviced?.toDto(),
    lastDeal = lastDeal?.toDto(),
    primaryContact = primaryContact?.toDto(),
    lastClosedFund = lastClosedFund?.toDto()
)

fun CreateServiceProviderServicedInput.toDto() = ServiceProviderServicedDto(
    companies = companies,
    deals = deals,
    investors = investors,
    funds = funds,
    total = total

)

fun CreateServiceProviderFundInput.toDto() = ServiceProviderFundDto(
    name = name,
    leadPartners = leadPartners?.map { it.toDto() }?.toMutableSet() ?: mutableSetOf(),
)

fun CreateLeadPartnerInput.toDto() = ServiceProviderLeadPartnerDto(
    pbId = pbId,
    name = name,
)

fun CreateServiceProviderPrimaryContactInput.toDto() = ServiceProviderPrimaryContactDto(
    name = name,
    title = title,
    email = email,
    phone = phone,
    pbId = pbId,
    personId = personId,
    personSlug = personSlug

)

fun CreateServiceProviderDealInput.toDto() = ServiceProviderDealDto(
    date = date,
    size = size?.toMoney(),
    valuation = valuation?.toMoney(),
    valuationStatus = valuationStatus?.toTag(),
    type = type1?.let {
        val tag1 = it.toTag()
        val tag2 = type2?.toTag()
        val tag3 = type3?.toTag()

        if (tag3 != null && tag2 != null) {
            tag2.parent = tag1
            tag3.parent = tag2
            return@let tag3
        }

        if (tag2 != null) {
            tag2.parent = tag1
            return@let tag2
        }

        return@let tag1
    },
    `class` = `class`?.toTag(),
    leadPartners = leadPartners?.map { it.toDto() }?.toMutableSet() ?: mutableSetOf(),
)
