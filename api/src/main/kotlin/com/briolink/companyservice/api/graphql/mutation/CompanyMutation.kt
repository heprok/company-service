package com.briolink.companyservice.api.graphql.mutation

import com.briolink.companyservice.api.graphql.SecurityUtil.currentUserAccountId
import com.briolink.companyservice.api.service.CompanyService
import com.briolink.companyservice.api.service.IndustryService
import com.briolink.companyservice.api.service.KeywordService
import com.briolink.companyservice.api.service.OccupationService
import com.briolink.companyservice.api.types.CompanyResultData
import com.briolink.companyservice.api.types.CreateCompanyInput
import com.briolink.companyservice.api.types.CreateCompanyResult
import com.briolink.companyservice.api.types.Error
import com.briolink.companyservice.api.types.UpdateCompanyInput
import com.briolink.companyservice.api.types.UpdateCompanyResult
import com.briolink.companyservice.common.jpa.read.entity.UserPermissionRoleReadEntity
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

    @DgsMutation
    fun createCompany(@InputArgument("input") createInputCompany: CreateCompanyInput): CreateCompanyResult {
        return if (createInputCompany.website == null || !companyService.isExistWebsite(createInputCompany.website.host.toString())) {
            val company = companyService.createCompany(
                    CompanyWriteEntity(
                            name = createInputCompany.name,
                            createdBy = UUID.fromString(createInputCompany.createBy),
                    ).apply {
                        websiteUrl = createInputCompany.website
                    },
            )
            CreateCompanyResult(
                    userErrors = listOf(),
                    data = CompanyResultData(id = company.id.toString(), name = company.name, website = company.website),
            )
        } else {
            CreateCompanyResult(
                    userErrors = listOf(),
                    data = companyService.getByWebsite(createInputCompany.website).let {
                        CompanyResultData(
                                id = it.id.toString(),
                                name = it.name,
                                website = it.websiteUrl,
                        )
                    },
//                    userErrors = listOf(Error("WebsiteCompanyIsExist", listOf("website"))),
            )
        }
    }

    @PreAuthorize("isAuthenticated()")
    @DgsMutation(field = "updateCompany")
    fun update(
        @InputArgument("id") id: String,
        @InputArgument("input") inputCompany: UpdateCompanyInput,
        dfe: DataFetchingEnvironment
    ): UpdateCompanyResult {
        val userErrors = mutableListOf<Error>()

        if (companyService.getPermission(
                    companyId = UUID.fromString(id),
                    userId = currentUserAccountId,
            ) != UserPermissionRoleReadEntity.RoleType.Owner)
            userErrors.add(Error("403 Permission denied"))
        else {
            companyService.findById(UUID.fromString(id)).orElseThrow { throw EntityNotFoundException("$id company not found") }
                    .apply {
                        val definitionFiled: Map<String, Any> = dfe.getArgument("input")
                        definitionFiled.forEach { (name, _) ->
                            when (name) {
                                "slug" -> this.slug = inputCompany.slug!!
                                "name" -> {
                                    if (inputCompany.name.isNullOrEmpty()) userErrors.add(Error("Name must be not empty or null"))
                                    else this.name = inputCompany.name
                                }
                                "website" -> {
                                    if (inputCompany.website != null && companyService.isExistWebsite(inputCompany.website.host.toString()))
                                        userErrors.add(Error("Website exist"))
                                    else this.websiteUrl = inputCompany.website
                                }
                                "description" -> this.description = inputCompany.description
                                "isTypePublic" -> this.isTypePublic = inputCompany.isTypePublic!!
                                "location" -> this.location = inputCompany.location
                                "facebook" -> this.facebook = inputCompany.facebook
                                "twitter" -> this.twitter = inputCompany.twitter
                                "occupationId" -> {
                                    this.occupation = inputCompany.occupationId?.let {
                                        occupationService.findById(UUID.fromString(it))
                                                .orElseThrow { throw EntityNotFoundException("$it occupation not found") }
                                    }
                                }
                                "occupationName" -> {
                                    if (inputCompany.occupationName.isNullOrEmpty()) userErrors.add(Error("Occupation must be not null or empty"))
                                    else this.occupation = occupationService.create(name = inputCompany.occupationName)
                                }
                                "industryId" -> {
                                    this.industry = inputCompany.industryId?.let {
                                        industryService.findById(UUID.fromString(it))
                                                .orElseThrow { throw EntityNotFoundException("$it industry not found") }
                                    }
                                }
                                "industryName" -> {
                                    if (inputCompany.industryName.isNullOrEmpty()) userErrors.add(Error("Industry must be not null or empty"))
                                    else this.industry = industryService.create(name = inputCompany.industryName)
                                }
                                "keyWordIds" -> {
                                    this.keywords = if (inputCompany.keyWordIds.isNullOrEmpty())
                                        mutableListOf()
                                    else
                                        inputCompany.keyWordIds.let { list ->
                                            list.map {
                                                keywordService.findById(UUID.fromString(it))
                                                        .orElseThrow { throw EntityNotFoundException("$id keyword not found") }
                                            }
                                        }.toMutableList()
                                }
                            }

                        }
                        if (userErrors.isEmpty()) companyService.updateCompany(this)

//            this.occupation = if (inputCompany.occupationId != null || inputCompany.occupationName != null) {
//                if (inputCompany.occupationId != null) {
//                    inputCompany.occupationId.let {
//                        occupationService.findById(UUID.fromString(it))
//                                .orElseThrow { throw EntityNotFoundException("$it occupation not found") }
//                    }
//                } else {
//                    occupationService.create(name = inputCompany.occupationName!!)
//                }
//            } else {
//                this.occupation
//            }

//            this.industry = if (inputCompany.industryId != null || inputCompany.industryName != null) {
//                if (inputCompany.industryId != null) {
//                    inputCompany.industryId.let {
//                        industryService.findById(UUID.fromString(it))
//                                .orElseThrow { throw EntityNotFoundException("$it industry not found") }
//                    }
//                } else {
//                    industryService.create(name = inputCompany.industryName!!)
//                }
//            } else {
//                this.industry
//            }

//                ?: this.keywords
                    }
        }

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

