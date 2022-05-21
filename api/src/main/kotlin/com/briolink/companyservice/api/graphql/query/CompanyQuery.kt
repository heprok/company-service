package com.briolink.companyservice.api.graphql.query

import com.briolink.companyservice.api.graphql.fromEntity
import com.briolink.companyservice.api.graphql.fromModel
import com.briolink.companyservice.api.service.CompanyService
import com.briolink.companyservice.api.types.Company
import com.briolink.companyservice.api.types.CompanyAndUserPermission
import com.briolink.companyservice.api.types.UserPermission
import com.briolink.lib.common.utils.BlSecurityUtils
import com.briolink.lib.permission.enumeration.AccessObjectTypeEnum
import com.briolink.lib.permission.service.PermissionService
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
    fun getCompany(@InputArgument slug: String): CompanyAndUserPermission {
        val company = companyService.getCompanyBySlug(slug)
        val role = if (BlSecurityUtils.isGuest) null else company?.let {
            permissionService.getUserPermissionRights(
                userId = BlSecurityUtils.currentUserId,
                accessObjectId = company.id,
                accessObjectType = AccessObjectTypeEnum.Company,
            )
        }
        if (company == null) throw DgsEntityNotFoundException()
        return CompanyAndUserPermission(
            company = Company.fromEntity(company),
            userPermission = role?.let { UserPermission.fromModel(role) } ?: UserPermission(null, listOf()),
        )
    }

    @DgsQuery
    @PreAuthorize("@blServletUtils.isIntranet()")
    fun getCompanyById(@InputArgument id: String): Company =
        companyService.findById(UUID.fromString(id))
            .orElseThrow { throw DgsEntityNotFoundException("$id company not found") }.let { Company.fromEntity(it) }
}
