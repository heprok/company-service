package com.briolink.companyservice.common.jpa.read.entity.company

import com.briolink.companyservice.common.jpa.read.entity.UserReadEntity
import com.briolink.lib.common.type.basic.Money
import com.briolink.lib.dictionary.enumeration.TagType
import com.briolink.lib.dictionary.model.Tag
import com.briolink.lib.dictionary.validation.TagTypeMatch
import com.briolink.lib.dictionary.validation.TagTypeMatchInCollection
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate
import java.util.UUID

data class SPData(
    @JsonProperty
    @TagTypeMatch([TagType.ServiceProviderType])
    var primaryType: Tag? = null,
    @JsonProperty
    @TagTypeMatchInCollection([TagType.ServiceProviderType])
    var otherTypes: MutableSet<Tag> = mutableSetOf(),
    @JsonProperty
    val serviced: SPServiced? = null,
    @JsonProperty
    var primaryContact: SPContact? = null,
    @JsonProperty
    val lastDeal: SPDeal? = null,
    @JsonProperty
    var lastClosedFund: SPFund? = null
)

data class SPFund(
    @JsonProperty
    val name: String,
    @JsonProperty
    var leadPartners: MutableSet<SPDealLeadPartner> = mutableSetOf(),
)

data class SPDeal(
    @JsonProperty
    var date: LocalDate? = null,
    @JsonProperty
    var size: Money? = null,
    @JsonProperty
    var valuation: Money? = null,
    @JsonProperty
    @TagTypeMatch([TagType.ValuationStatus])
    var valuationStatus: Tag? = null,
    @JsonProperty
    @TagTypeMatch([TagType.DealType])
    var type: Tag? = null,
    @JsonProperty
    @TagTypeMatch([TagType.DealClass])
    var `class`: Tag? = null,
    @JsonProperty
    var leadPartners: MutableSet<SPDealLeadPartner> = mutableSetOf(),
)

data class SPServiced(
    @JsonProperty
    val companies: Int? = null,
    @JsonProperty
    val deals: Int? = null,
    @JsonProperty
    val investors: Int? = null,
    @JsonProperty
    val funds: Int? = null,
) {
    val total: Int
        get() = (companies ?: 0) + (deals ?: 0) + (investors ?: 0) + (funds ?: 0)
}

data class SPContact(
    @JsonProperty
    var name: String,
    @JsonProperty
    var title: String? = null,
    @JsonProperty
    var email: String? = null,
    @JsonProperty
    var phone: String? = null,
    @JsonProperty
    var pbId: String? = null,
    @JsonProperty
    var personId: UUID? = null,
    @JsonProperty
    var user: UserReadEntity.BaseUser? = null
)

data class SPDealLeadPartner(
    @JsonProperty
    var pbId: String? = null,
    @JsonProperty
    var name: String? = null,
)
