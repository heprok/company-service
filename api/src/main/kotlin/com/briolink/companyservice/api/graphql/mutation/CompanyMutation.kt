package com.briolink.companyservice.api.graphql.mutation

import com.briolink.companyservice.api.graphql.fromEntity
import com.briolink.companyservice.api.service.CompanyService
import com.briolink.companyservice.api.service.IndustryService
import com.briolink.companyservice.api.service.KeywordService
import com.briolink.companyservice.api.service.OccupationService
import com.briolink.companyservice.api.types.Company
import com.briolink.companyservice.api.types.CreateCompanyInput
import com.briolink.companyservice.api.types.Error
import com.briolink.companyservice.api.types.UpdateCompanyInput
import com.briolink.companyservice.api.types.UpdateCompanyResult
import com.briolink.companyservice.api.util.SecurityUtil.currentUserAccountId
import com.briolink.companyservice.common.jpa.enumeration.AccessObjectTypeEnum
import com.briolink.companyservice.common.jpa.enumeration.PermissionRightEnum
import com.briolink.companyservice.common.service.PermissionService
import com.briolink.companyservice.common.util.StringUtil
import com.briolink.location.model.LocationId
import com.briolink.location.service.LocationService
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsMutation
import com.netflix.graphql.dgs.InputArgument
import graphql.schema.DataFetchingEnvironment
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.multipart.MultipartFile
import java.net.URL
import java.util.UUID
import javax.persistence.EntityNotFoundException

@DgsComponent
class CompanyMutation(
    val companyService: CompanyService,
    val occupationService: OccupationService,
    val industryService: IndustryService,
    val keywordService: KeywordService,
    val permissionService: PermissionService,
    val locationService: LocationService,
) {
    @PreAuthorize("isAuthenticated()")
    @DgsMutation
    fun uploadCompanyImage(@InputArgument("id") id: String, @InputArgument("image") image: MultipartFile?): URL? {
        return companyService.uploadCompanyProfileImage(UUID.fromString(id), image)
    }

    @DgsMutation
    @PreAuthorize("@servletUtil.isIntranet()")
    fun createCompany(@InputArgument("input") createInputCompany: CreateCompanyInput): Company =
        companyService.createCompany(
            name = StringUtil.trimAllSpaces(createInputCompany.name),
            imageUrl = createInputCompany.logo,
            industryName = createInputCompany.industryName,
            description = createInputCompany.description,
            createdBy = UUID.fromString(createInputCompany.createBy),
            website = StringUtil.prepareUrl(createInputCompany.website),
        ).let { Company.fromEntity(it) }

    @PreAuthorize("isAuthenticated()")
    @DgsMutation(field = "updateCompany")
    fun update(
        @InputArgument("id") id: String,
        @InputArgument("input") inputCompany: UpdateCompanyInput,
        dfe: DataFetchingEnvironment
    ): UpdateCompanyResult {
        val userErrors = mutableListOf<Error>()

        if (!permissionService.isHavePermission(
                accessObjectType = AccessObjectTypeEnum.Company,
                userId = currentUserAccountId,
                companyId = UUID.fromString(id),
                permissionRight = PermissionRightEnum.EditCompanyProfile,
            )
        )
            userErrors.add(Error("403 Permission denied"))
        else {
            companyService.findById(UUID.fromString(id))
                .orElseThrow { throw EntityNotFoundException("$id company not found") }
                .apply {
                    val definitionFiled: Map<String, Any> = dfe.getArgument("input")
                    definitionFiled.forEach { (name, _) ->
                        when (name) {
                            "slug" -> this.slug = inputCompany.slug!!
                            "name" -> {
                                if (inputCompany.name.isNullOrBlank()) userErrors.add(Error("Name must be not empty or null"))
                                else this.name = StringUtil.trimAllSpaces(inputCompany.name)
                            }
                            "website" -> {
                                if (inputCompany.website != null && companyService.isExistWebsite(inputCompany.website))
                                    userErrors.add(Error("Website exist"))
                                else this.websiteUrl = inputCompany.website
                            }
                            "description" -> this.description = inputCompany.description
                            "isTypePublic" -> this.isTypePublic = inputCompany.isTypePublic!!
                            "locationId" -> {
                                if (inputCompany.locationId != null) {
                                    locationService.getLocationInfo(LocationId.fromString(inputCompany.locationId))
                                        .also {
                                            if (it != null) {
                                                countryId = it.country.id
                                                stateId = it.state?.id
                                                cityId = it.city?.id
                                            } else userErrors.add(Error("Not find location for ${inputCompany.locationId}"))
                                        }
                                    return@forEach
                                } else {
                                    countryId = null
                                    stateId = null
                                    cityId = null
                                }
                            }
                            "facebook" -> this.facebook = inputCompany.facebook
                            "twitter" -> this.twitter = inputCompany.twitter
                            "occupationId" -> {
                                this.occupation = inputCompany.occupationId?.let {
                                    if (it.isBlank()) null
                                    else occupationService.findById(UUID.fromString(it))
                                        .orElseThrow { throw EntityNotFoundException("$it occupation not found") }
                                }
                            }
                            "occupationName" -> {
                                if (inputCompany.occupationName.isNullOrBlank()) userErrors.add(Error("Occupation must be not null or empty")) // ktlint-disable max-line-length
                                else this.occupation = occupationService.create(name = inputCompany.occupationName)
                            }
                            "industryId" -> {
                                this.industry = inputCompany.industryId?.let {
                                    industryService.findById(UUID.fromString(it))
                                        .orElseThrow { throw EntityNotFoundException("$it industry not found") }
                                }
                            }
                            "industryName" -> {
                                if (inputCompany.industryName.isNullOrBlank()) userErrors.add(Error("Industry must be not null or empty"))
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
