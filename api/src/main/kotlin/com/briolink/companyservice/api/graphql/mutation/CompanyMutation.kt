package com.briolink.companyservice.api.graphql.mutation

import com.briolink.companyservice.api.graphql.mapper.fromEntity
import com.briolink.companyservice.api.graphql.mapper.toMutableSetTags
import com.briolink.companyservice.api.graphql.mapper.toTag
import com.briolink.companyservice.api.service.company.CompanyService
import com.briolink.companyservice.api.service.company.dto.CreatedCompanyDto
import com.briolink.companyservice.api.service.company.dto.mapper.toDto
import com.briolink.companyservice.api.service.company.dto.mapper.toEnum
import com.briolink.companyservice.api.types.Company
import com.briolink.companyservice.api.types.CreateCompanyInput
import com.briolink.companyservice.api.types.Error
import com.briolink.companyservice.api.types.UpdateCompanyInput
import com.briolink.companyservice.api.types.UpdateCompanyResult
import com.briolink.lib.location.model.LocationId
import com.briolink.lib.permission.AllowedRights
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsMutation
import com.netflix.graphql.dgs.InputArgument
import graphql.schema.DataFetchingEnvironment
import org.springframework.web.multipart.MultipartFile
import java.net.URL
import java.util.UUID

@DgsComponent
class CompanyMutation(
    val companyService: CompanyService,
) {
    @AllowedRights(value = ["EditCompanyProfile@Company"], argumentNameId = "id")
    @DgsMutation
    fun uploadCompanyImage(@InputArgument id: String, @InputArgument image: MultipartFile?): URL? {
        return companyService.uploadCompanyProfileImage(UUID.fromString(id), image)
    }

    @DgsMutation
    // @PreAuthorize("@blServletUtils.isIntranet()")
    fun createCompany(@InputArgument input: CreateCompanyInput): Company {

        val dto = CreatedCompanyDto(
            id = input.id,
            pbId = input.pbId,
            parentCompanyId = input.parentCompanyId,
            parentCompanyPbId = input.parentCompanyPbId,
            name = input.name,
            slug = input.slug,
            primaryCompanyType = input.primaryCompanyType.toEnum(),
            otherCompanyTypes = input.companyTypes?.map { it.toEnum() }?.toMutableSet() ?: mutableSetOf(),
            website = input.website,
            familiarName = input.familiarName,
            logo = input.logo,
            description = input.description,
            shortDescription = input.shortDescription,
            locationId = input.locationId?.let { LocationId.fromString(it) },
            keywords = input.keywords?.toMutableSetTags() ?: mutableSetOf(),
            verticals = input.verticals?.toMutableSetTags() ?: mutableSetOf(),
            primaryIndustry = input.primaryIndustry?.toTag(),
            industries = input.industries?.toMutableSetTags() ?: mutableSetOf(),
            yearFounded = input.yearFounded,
            facebook = input.facebook,
            twitter = input.twitter,
            employees = input.employees,
            createBy = input.createBy,
            startupInfoDto = input.startupInfo?.toDto(),
            investorInfoDto = input.investorInfo?.toDto(),
            serviceProviderInfo = input.serviceProviderInfo?.toDto(),

        )

        return companyService.createCompany(dto).let {
            Company.fromEntity(it)
        }
    }

    @AllowedRights(value = ["EditCompanyProfile@Company"], argumentNameId = "id")
    @DgsMutation(field = "updateCompany")
    fun update(
        @InputArgument id: String,
        @InputArgument input: UpdateCompanyInput,
        dfe: DataFetchingEnvironment
    ): UpdateCompanyResult {
        val userErrors = mutableListOf<Error>()

        TODO("Not implemented yet")

        return if (userErrors.isEmpty())
            UpdateCompanyResult(
                success = true,
                userErrors = listOf(),
            )
        else
            UpdateCompanyResult(
                success = false,
                userErrors = userErrors.toList(),
            )
    }
}
