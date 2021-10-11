package com.briolink.companyservice.api.graphql.query

import com.briolink.companyservice.api.graphql.SecurityUtil
import com.briolink.companyservice.api.graphql.fromEntity
import com.briolink.companyservice.api.service.CompanyService
import com.briolink.companyservice.api.types.Company
import com.briolink.companyservice.api.types.CompanyAndUserRole
import com.briolink.companyservice.api.types.Role
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
        return CompanyAndUserRole(
                company = Company.fromEntity(company),
                role = when (companyService.isUserEditCompany(
                        SecurityUtil.currentUserAccountId,
                        UUID.fromString(company.id.toString()),
                )
                ) {
                    true -> Role.ADMIN
                    false -> Role.EMPLOYEE
                },
        )

    }
}
