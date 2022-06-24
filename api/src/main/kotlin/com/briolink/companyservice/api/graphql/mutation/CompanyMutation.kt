package com.briolink.companyservice.api.graphql.mutation

import com.briolink.companyservice.api.graphql.mapper.fromEntity
import com.briolink.companyservice.api.service.company.CompanyService
import com.briolink.companyservice.api.service.company.dto.mapper.toDto
import com.briolink.companyservice.api.types.Company
import com.briolink.companyservice.api.types.CreateCompanyInput
import com.briolink.companyservice.api.types.Error
import com.briolink.companyservice.api.types.UpdateCompanyInput
import com.briolink.companyservice.api.types.UpdateCompanyResult
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
    fun createCompanies(@InputArgument listInput: List<CreateCompanyInput>, env: DataFetchingEnvironment): List<Company> {
        return companyService.createCompanies(listInput.map { it.toDto() }).map { Company.fromEntity(it) }
    }

    @DgsMutation
    // @PreAuthorize("@blServletUtils.isIntranet()")
    fun createCompany(@InputArgument input: CreateCompanyInput): Company {

        return companyService.createCompany(input.toDto()).let {
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
