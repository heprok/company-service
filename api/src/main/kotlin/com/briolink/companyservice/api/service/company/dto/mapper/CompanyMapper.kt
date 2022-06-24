package com.briolink.companyservice.api.service.company.dto.mapper

import com.briolink.companyservice.api.graphql.mapper.toMutableSetTags
import com.briolink.companyservice.api.graphql.mapper.toTag
import com.briolink.companyservice.api.service.company.dto.CreatedCompanyDto
import com.briolink.companyservice.api.types.CompanyType
import com.briolink.companyservice.api.types.CreateCompanyInput
import com.briolink.companyservice.common.enumeration.CompanyTypeEnum
import com.briolink.lib.location.model.LocationId

fun CompanyType.toEnum() = CompanyTypeEnum.valueOf(name)

fun CreateCompanyInput.toDto() = CreatedCompanyDto(
    id = id,
    pbId = pbId,
    parentCompanyId = parentCompanyId,
    parentCompanyPbId = parentCompanyPbId,
    name = name,
    slug = slug,
    primaryCompanyType = primaryCompanyType.toEnum(),
    otherCompanyTypes = companyTypes?.map { it.toEnum() }?.toMutableSet() ?: mutableSetOf(),
    website = website,
    familiarName = familiarName,
    logo = logo,
    description = description,
    shortDescription = shortDescription,
    locationId = locationId?.let { LocationId.fromString(it) },
    keywords = keywords?.toMutableSetTags() ?: mutableSetOf(),
    verticals = verticals?.toMutableSetTags() ?: mutableSetOf(),
    primaryIndustry = primaryIndustry?.toTag(),
    industries = industries?.toMutableSetTags() ?: mutableSetOf(),
    yearFounded = yearFounded,
    facebook = facebook,
    twitter = twitter,
    employees = employees,
    createBy = createBy,
    startupInfoDto = startupInfo?.toDto(),
    investorInfoDto = investorInfo?.toDto(),
    serviceProviderInfo = serviceProviderInfo?.toDto(),
)
