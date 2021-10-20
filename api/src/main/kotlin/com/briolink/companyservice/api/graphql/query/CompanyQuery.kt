package com.briolink.companyservice.api.graphql.query

import com.briolink.companyservice.api.graphql.SecurityUtil
import com.briolink.companyservice.api.graphql.fromEntity
import com.briolink.companyservice.api.service.CompanyService
import com.briolink.companyservice.api.types.Company
import com.briolink.companyservice.api.types.CompanyAndUserRole
import com.briolink.companyservice.api.types.PermissionRole
import com.briolink.companyservice.common.jpa.read.entity.UserPermissionRoleReadEntity
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsQuery
import com.netflix.graphql.dgs.InputArgument
import org.springframework.security.access.prepost.PreAuthorize
import java.util.*

@DgsComponent
class CompanyQuery(private val companyService: CompanyService) {
    @DgsQuery
    @PreAuthorize("isAuthenticated()")
    fun getCompany(@InputArgument("slug") slug: String): CompanyAndUserRole {
        val company = companyService.getCompanyBySlug(slug)
        val role = companyService.getPermission(company.id, SecurityUtil.currentUserAccountId)
        return CompanyAndUserRole(
                company = Company.fromEntity(company),
                role = when (role) {
                    UserPermissionRoleReadEntity.RoleType.Employee -> PermissionRole.Employee
                    UserPermissionRoleReadEntity.RoleType.Owner -> PermissionRole.Owner
                    else -> null
                },
        )

    }
}
