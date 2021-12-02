package com.briolink.companyservice.api.graphql.query

import com.briolink.companyservice.api.graphql.fromEntity
import com.briolink.companyservice.api.service.CompanyService
import com.briolink.companyservice.api.types.Company
import com.briolink.companyservice.api.types.CompanyAndUserRole
import com.briolink.companyservice.api.types.PermissionRole
import com.briolink.companyservice.api.util.SecurityUtil
import com.briolink.companyservice.common.jpa.enumeration.UserPermissionRoleTypeEnum
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsQuery
import com.netflix.graphql.dgs.InputArgument
import com.netflix.graphql.dgs.exceptions.DgsEntityNotFoundException
import org.springframework.security.access.prepost.PreAuthorize
import java.util.UUID

@DgsComponent
class CompanyQuery(private val companyService: CompanyService) {
    @DgsQuery
    @PreAuthorize("isAuthenticated()")
    fun getCompany(@InputArgument("slug") slug: String): CompanyAndUserRole {
        val company = companyService.getCompanyBySlug(slug)
        val role = company?.let { companyService.getPermission(it.id, SecurityUtil.currentUserAccountId) }
        return CompanyAndUserRole(
            company = company?.let { Company.fromEntity(it) },
            role = when (role) {
                UserPermissionRoleTypeEnum.Employee -> PermissionRole.Employee
                UserPermissionRoleTypeEnum.Owner -> PermissionRole.Owner
                else -> null
            },
        )
    }

    @DgsQuery
    @PreAuthorize("@servletUtil.isIntranet()")
    fun getCompanyById(@InputArgument("id") id: String): Company =
        companyService.findById(UUID.fromString(id))
            .orElseThrow { throw DgsEntityNotFoundException("$id company not found") }.let { Company.fromEntity(it) }
}
