package com.briolink.companyservice.api.graphql.query

import com.briolink.companyservice.api.graphql.fromEntity
import com.briolink.companyservice.api.graphql.fromModel
import com.briolink.companyservice.api.service.CompanyService
import com.briolink.companyservice.api.types.Company
import com.briolink.companyservice.api.types.CompanyAndUserPermission
import com.briolink.companyservice.api.types.UserPermission
import com.briolink.companyservice.api.util.SecurityUtil
import com.briolink.permission.enumeration.AccessObjectTypeEnum
import com.briolink.permission.service.PermissionService
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsQuery
import com.netflix.graphql.dgs.InputArgument
import com.netflix.graphql.dgs.exceptions.DgsEntityNotFoundException
import org.springframework.security.access.prepost.PreAuthorize
import java.util.UUID

@DgsComponent
class CompanyQuery(
    private val companyService: CompanyService,
    private val permissionService: PermissionService
) {
    @DgsQuery
    @PreAuthorize("isAuthenticated()")
    fun getCompany(@InputArgument("slug") slug: String): CompanyAndUserPermission {
        val company = companyService.getCompanyBySlug(slug)
        val role = company?.let {
            permissionService.getUserPermissionRights(
                userId = SecurityUtil.currentUserAccountId,
                accessObjectId = company.id,
                accessObjectType = AccessObjectTypeEnum.Company,
            )
        }
        return CompanyAndUserPermission(
            company = company?.let { Company.fromEntity(it) },
            userPermission = role?.let { UserPermission.fromModel(role) }
        )
    }

    @DgsQuery
    @PreAuthorize("@servletUtil.isIntranet()")
    fun getCompanyById(@InputArgument("id") id: String): Company =
        companyService.findById(UUID.fromString(id))
            .orElseThrow { throw DgsEntityNotFoundException("$id company not found") }.let { Company.fromEntity(it) }
}
