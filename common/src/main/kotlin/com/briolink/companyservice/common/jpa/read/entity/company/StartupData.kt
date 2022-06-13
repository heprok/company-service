package com.briolink.companyservice.common.jpa.read.entity.company

import com.briolink.lib.common.type.basic.Money
import com.briolink.lib.common.type.basic.ValueWithCount
import com.briolink.lib.dictionary.enumeration.TagType
import com.briolink.lib.dictionary.model.Tag
import com.briolink.lib.dictionary.validation.TagTypeMatch
import com.briolink.lib.dictionary.validation.TagTypeMatchInCollection
import com.fasterxml.jackson.annotation.JsonProperty

data class StartupData(
    @JsonProperty
    var totalPatentDocs: Int? = null,
    @JsonProperty
    var revenue: Money? = null,
    @JsonProperty
    var totalRaised: Money? = null,
    @JsonProperty
    var lastFinancing: Money? = null,
    @JsonProperty
    @TagTypeMatch([TagType.FinancingStatus])
    var lastFinancingStatus: Tag? = null,
    @JsonProperty
    @TagTypeMatchInCollection([TagType.Universe])
    var universes: MutableSet<Tag> = mutableSetOf(),
    @JsonProperty
    @TagTypeMatchInCollection([TagType.CPCCode])
    var cpcCodes: MutableSet<Tag> = mutableSetOf(),
    @JsonProperty
    var investors: MutableSet<CompanyReadEntity.BaseCompany> = mutableSetOf(),
    @JsonProperty
    var potentialInvestors: MutableSet<ValueWithCount<CompanyReadEntity.BaseCompany, Double>> = mutableSetOf(),
    @JsonProperty
    var servicesProviders: MutableSet<CompanyReadEntity.BaseCompany> = mutableSetOf(),
    @JsonProperty
    var otherServicesProviders: MutableSet<String> = mutableSetOf(),
    @JsonProperty
    var comparables: MutableSet<ValueWithCount<CompanyReadEntity.BaseCompany, Double>> = mutableSetOf(),
    @JsonProperty
    var otherComparables: MutableSet<ValueWithCount<CompanyReadEntity.BaseCompany, Double>> = mutableSetOf(),
)
