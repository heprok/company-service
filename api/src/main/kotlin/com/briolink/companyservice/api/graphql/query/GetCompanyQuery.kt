package com.briolink.companyservice.api.graphql.query

import com.briolink.companyservice.api.graphql.fromEntity
import com.briolink.companyservice.api.service.company.CompanyQueryService
import com.briolink.companyservice.api.types.Company
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsQuery
import com.netflix.graphql.dgs.InputArgument
import org.springframework.security.access.prepost.PreAuthorize
import java.util.UUID

@DgsComponent
class GetCompanyQuery(private val companyQueryService: CompanyQueryService) {

    @DgsQuery
    @PreAuthorize("isAuthenticated()")
    fun getCompany(@InputArgument("id") companyId: String): Company {
        val company = companyQueryService.getCompanyById(UUID.fromString(companyId))
        return Company.fromEntity(company)
    }
}
