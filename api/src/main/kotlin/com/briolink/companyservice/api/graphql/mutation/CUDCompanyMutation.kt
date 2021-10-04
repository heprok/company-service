package com.briolink.companyservice.api.graphql.mutation

import com.briolink.companyservice.api.service.CompanyService
import com.briolink.companyservice.api.service.IndustryService
import com.briolink.companyservice.api.service.KeywordService
import com.briolink.companyservice.api.service.OccupationService
import com.briolink.companyservice.api.types.CompanyResult
import com.briolink.companyservice.api.types.CreateCompanyInput
import com.briolink.companyservice.api.types.UpdateCompanyInput
import com.briolink.companyservice.common.jpa.write.entity.CompanyWriteEntity
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsMutation
import com.netflix.graphql.dgs.InputArgument
import graphql.schema.DataFetchingEnvironment
import org.springframework.web.multipart.MultipartFile
import java.net.URL
import java.util.*
import javax.persistence.EntityNotFoundException

@DgsComponent
class CUDCompanyMutation(
    val companyService: CompanyService,
    val occupationService: OccupationService,
    val industryService: IndustryService,
    val keywordService: KeywordService,
) {
    @DgsMutation(field = "createCompany")
    fun create(@InputArgument("company") company: CreateCompanyInput): CompanyResult {
        companyService.createCompany(
                CompanyWriteEntity(
                        name = company.name,
                        website = company.website.toString(),
                ),
        )
        return CompanyResult(
                userErrors = listOf(),
        )
    }

    @DgsMutation
    fun uploadCompanyImage(@InputArgument("id") id: String, @InputArgument("image") image: MultipartFile?): URL? {
        return companyService.uploadCompanyProfileImage(UUID.fromString(id), image)
    }

    @DgsMutation(field = "updateCompany")
    fun update(
        @InputArgument("id") id: String,
        @InputArgument("company") updatedCompany: UpdateCompanyInput,
        dfe: DataFetchingEnvironment
    ): CompanyResult {
        val company = companyService.findById(UUID.fromString(id)).orElseThrow { throw EntityNotFoundException("$id company not found") }
        println(updatedCompany)
        println(company)
//
//        val updatedCompany: Map<String, Any> = dfe.getArgument("company")
//        val companyWriteEntity: CompanyWriteEntity = jacksonObjectMapper().convertValue(updatedCompany, company::class.java)
//        println(companyWriteEntity)
//        println(company)

        company.slug = updatedCompany.slug ?: company.slug
        company.name = updatedCompany.name ?: company.name
        company.website = updatedCompany.website?.toString() ?: company.website
        company.about = updatedCompany.about ?: company.about
        company.isTypePublic = updatedCompany.isTypePublic ?: company.isTypePublic
        company.country = updatedCompany.country ?: company.country
        company.state = updatedCompany.state ?: company.state
        company.city = updatedCompany.city ?: company.city
        company.facebook = updatedCompany.facebook ?: company.facebook
        company.twitter = updatedCompany.twitter ?: company.twitter
        company.occupation = updatedCompany.occupationId?.let {
            occupationService.findById(UUID.fromString(it))
                    .orElseThrow { throw EntityNotFoundException("$it occupation not found") }
        } ?: company.occupation
        company.industry = updatedCompany.industryId?.let {
            industryService.findById(UUID.fromString(it))
                    .orElseThrow { throw EntityNotFoundException("$it industry not found") }
        } ?: company.industry
        company.keywords = updatedCompany.keyWordIds?.let { list ->
            list.map {
                keywordService.findById(UUID.fromString(it!!)).orElseThrow { throw EntityNotFoundException("$id keyword not found") }
            }
        }?.toMutableList() ?: company.keywords

        companyService.updateCompany(company)
        return CompanyResult(
                userErrors = listOf(),
        )
    }
}

