package com.briolink.companyservice.api.service.company.dto

import com.briolink.lib.common.type.basic.Money
import com.briolink.lib.common.type.basic.ValueWithCount
import com.briolink.lib.dictionary.enumeration.TagType
import com.briolink.lib.dictionary.model.Tag
import com.briolink.lib.dictionary.validation.TagTypeMatch
import com.briolink.lib.dictionary.validation.TagTypeMatchInCollection
import java.util.UUID

data class CreateStartupInfoDto(
    val totalPatentDocs: Int?,
    val revenue: Money?,
    val totalRaised: Money?,
    val lastFinancing: Money?,
    @TagTypeMatch([TagType.FinancingStatus])
    val lastFinancingStatus: Tag?,
    @TagTypeMatchInCollection([TagType.Universe])
    val universe: MutableSet<Tag> = mutableSetOf(),
    @TagTypeMatchInCollection([TagType.CPCCode])
    val cpcCodes: MutableSet<Tag> = mutableSetOf(),
    val investors: MutableSet<UUID> = mutableSetOf(),
    val otherInvestors: MutableSet<String> = mutableSetOf(),
    val potentialInvestors: MutableSet<ValueWithCount<UUID, Double>> = mutableSetOf(),
    val servicesProviders: MutableSet<UUID> = mutableSetOf(),
    val otherServicesProviders: MutableSet<String> = mutableSetOf(),
    val comparables: MutableSet<ValueWithCount<UUID, Double>> = mutableSetOf(),
    val otherComparables: MutableSet<ValueWithCount<String, Double>> = mutableSetOf(),
) {
    fun getAllTags(): Set<Tag> =
        mutableSetOf<Tag>().apply {
            lastFinancingStatus?.also { add(it) }
            universe.also { addAll(it) }
            cpcCodes.also { addAll(it) }
        }
}
