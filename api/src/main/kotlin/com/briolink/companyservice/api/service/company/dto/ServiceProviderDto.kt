package com.briolink.companyservice.api.service.company.dto

import com.briolink.lib.common.type.basic.Money
import com.briolink.lib.dictionary.enumeration.TagType
import com.briolink.lib.dictionary.model.Tag
import com.briolink.lib.dictionary.validation.TagTypeMatch
import com.briolink.lib.dictionary.validation.TagTypeMatchInCollection
import java.time.LocalDate
import java.util.UUID

data class CreateServiceProviderInfoDto(
    @TagTypeMatch([TagType.ServiceProviderType])
    val primaryType: Tag?,
    @TagTypeMatchInCollection([TagType.ServiceProviderType])
    val otherTypes: MutableSet<Tag> = mutableSetOf(),
    val servicedCompanies: Int?,
    val serviced: ServiceProviderServicedDto?,
    val lastDeal: ServiceProviderDealDto?,
    val primaryContact: ServiceProviderPrimaryContactDto?,
    val lastClosedFund: ServiceProviderFundDto?,
) {
    fun getAllTags(): Set<Tag> =
        mutableSetOf<Tag>().apply {
            primaryType?.also { add(it) }
            otherTypes.also { addAll(it) }
            lastDeal?.getAllTags()?.also { addAll(it) }
        }
}

data class ServiceProviderServicedDto(
    val companies: Int?,
    val deals: Int?,
    val investors: Int?,
    val funds: Int?,
    val total: Int?,
)

data class ServiceProviderDealDto(
    val date: LocalDate?,
    val size: Money?,
    val valuation: Money?,
    @TagTypeMatch([TagType.ValuationStatus])
    val valuationStatus: Tag?,
    @TagTypeMatch([TagType.DealType])
    val type: Tag?,
    @TagTypeMatch([TagType.DealClass])
    val `class`: Tag?,
    val leadPartners: MutableSet<ServiceProviderLeadPartnerDto> = mutableSetOf(),
) {
    fun getAllTags(): Set<Tag> =
        mutableSetOf<Tag>().apply {
            valuationStatus?.also { add(it) }
            type?.also { add(it) }
            `class`?.also { add(it) }
        }
}

data class ServiceProviderPrimaryContactDto(
    val name: String,
    val pbId: String?,
    val title: String?,
    val email: String?,
    val phone: String?,
    val personId: UUID?,
    val personSlug: String?,
)

data class ServiceProviderLeadPartnerDto(
    val pbId: String?,
    val name: String,
)

data class ServiceProviderFundDto(
    val name: String,
    val leadPartners: MutableSet<ServiceProviderLeadPartnerDto> = mutableSetOf(),
)
