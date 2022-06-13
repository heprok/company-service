package com.briolink.companyservice.api.service.company.dto

import com.briolink.companyservice.common.enumeration.CompanyTypeEnum
import com.briolink.lib.dictionary.enumeration.TagType
import com.briolink.lib.dictionary.model.Tag
import com.briolink.lib.dictionary.validation.TagTypeMatch
import com.briolink.lib.dictionary.validation.TagTypeMatchInCollection
import com.briolink.lib.location.model.LocationId
import java.net.URL
import java.time.Year
import java.util.UUID

data class CreatedCompanyDto(
    val id: UUID?,
    val pbId: String?,
    val parentCompanyId: UUID?,
    val parentCompanyPbId: String?,
    val name: String,
    val slug: String?,
    val primaryCompanyType: CompanyTypeEnum,
    val otherCompanyTypes: MutableSet<CompanyTypeEnum>?,
    val website: URL?,
    val familiarName: String?,
    val logo: URL?,
    val description: String?,
    val shortDescription: String?,
    val locationId: LocationId?,
    @TagTypeMatchInCollection([TagType.Keyword])
    val keywords: MutableSet<Tag> = mutableSetOf(),
    @TagTypeMatchInCollection([TagType.Vertical])
    val verticals: MutableSet<Tag> = mutableSetOf(),
    @TagTypeMatch([TagType.Industry])
    val primaryIndustry: Tag?,
    @TagTypeMatchInCollection([TagType.Industry])
    val industries: MutableSet<Tag> = mutableSetOf(),
    val yearFounded: Year?,
    val facebook: String?,
    val twitter: String?,
    val employees: Int?,
    val createBy: UUID?,
    val startupInfoDto: CreateStartupInfoDto?,
    val investorInfoDto: CreateInvestorInfoDto?,
    val serviceProviderInfo: CreateServiceProviderInfoDto?
) {
    fun getAllTags(): Set<Tag> {
        mutableSetOf<Tag>().apply {
            verticals.also { addAll(it) }
            keywords.also { addAll(it) }
            industries.also { addAll(it) }
            primaryIndustry?.also { add(it) }
            startupInfoDto?.getAllTags()?.also { addAll(it) }
            investorInfoDto?.getAllTags()?.also { addAll(it) }
            serviceProviderInfo?.getAllTags()?.also { addAll(it) }

            return this
        }
    }
}
