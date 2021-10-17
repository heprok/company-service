package com.briolink.companyservice.api.graphql.mutation

import com.briolink.companyservice.api.service.CompanyService
import com.briolink.companyservice.api.service.IndustryService
import com.briolink.companyservice.api.service.KeywordService
import com.briolink.companyservice.api.service.OccupationService
import com.briolink.companyservice.api.types.CompanyResultData
import com.briolink.companyservice.api.types.CreateCompanyInput
import com.briolink.companyservice.api.types.CreateCompanyResult
import com.briolink.companyservice.api.types.UpdateCompanyInput
import com.briolink.companyservice.api.types.UpdateCompanyResult
import com.briolink.companyservice.common.jpa.write.entity.CompanyWriteEntity
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsMutation
import com.netflix.graphql.dgs.InputArgument
import graphql.schema.DataFetchingEnvironment
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.multipart.MultipartFile
import java.net.URL
import java.util.*
import javax.persistence.EntityNotFoundException

@DgsComponent
class CompanyMutation(
    val companyService: CompanyService,
    val occupationService: OccupationService,
    val industryService: IndustryService,
    val keywordService: KeywordService,
) {
    @PreAuthorize("isAuthenticated()")
    @DgsMutation
    fun uploadCompanyImage(@InputArgument("id") id: String, @InputArgument("image") image: MultipartFile?): URL? {
        return companyService.uploadCompanyProfileImage(UUID.fromString(id), image)
    }

//    @PreAuthorize("isAuthenticated()")
    @DgsMutation
    fun createCompany(@InputArgument("input") createInputCompany: CreateCompanyInput): CreateCompanyResult {
        val company = companyService.createCompany(CompanyWriteEntity(name = createInputCompany.name, website = createInputCompany.website))
        return CreateCompanyResult(
                userErrors = listOf(),
                data = CompanyResultData(id = company.id.toString(), name = company.name),
        )
    }

    @PreAuthorize("isAuthenticated()")
    @DgsMutation(field = "updateCompany")
    fun update(
        @InputArgument("id") id: String,
        @InputArgument("input") inputCompany: UpdateCompanyInput,
        dfe: DataFetchingEnvironment
    ): UpdateCompanyResult {
        val company = companyService.findById(UUID.fromString(id)).orElseThrow { throw EntityNotFoundException("$id company not found") }

        company.slug = inputCompany.slug ?: company.slug
        company.name = inputCompany.name ?: company.name
        company.website = inputCompany.website ?: company.website
        company.description = inputCompany.description ?: company.description
        company.isTypePublic = inputCompany.isTypePublic ?: company.isTypePublic
//        company.country = inputCompany.country ?: company.country
//        company.state = inputCompany.state ?: company.state
//        company.city = inputCompany.city ?: company.city
        company.location = inputCompany.location ?: company.location
        company.facebook = inputCompany.facebook ?: company.facebook
        company.twitter = inputCompany.twitter ?: company.twitter

        company.occupation = if (inputCompany.occupationId != null || inputCompany.occupationName != null) {
            if (inputCompany.occupationId != null) {
                inputCompany.occupationId.let {
                    occupationService.findById(UUID.fromString(it))
                            .orElseThrow { throw EntityNotFoundException("$it occupation not found") }
                }
            } else {
                occupationService.create(name = inputCompany.occupationName!!)
            }
        } else {
            company.occupation
        }

        company.industry = if (inputCompany.industryId != null || inputCompany.industryName != null) {
            if (inputCompany.industryId != null) {
                inputCompany.industryId.let {
                    industryService.findById(UUID.fromString(it))
                            .orElseThrow { throw EntityNotFoundException("$it industry not found") }
                }
            } else {
                industryService.create(name = inputCompany.industryName!!)
            }
        } else {
            company.industry
        }

        company.keywords = inputCompany.keyWordIds?.let { list ->
            list.map {
                keywordService.findById(UUID.fromString(it!!)).orElseThrow { throw EntityNotFoundException("$id keyword not found") }
            }
        }?.toMutableList() ?: company.keywords
        companyService.updateCompany(company)
        return UpdateCompanyResult(
                success = true,
                userErrors = listOf(),
        )
    }
}

