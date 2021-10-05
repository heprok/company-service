package com.briolink.companyservice.api.graphql.query

import com.briolink.companyservice.api.graphql.fromEntity
import com.briolink.companyservice.api.service.CompanyService
import com.briolink.companyservice.api.types.Company
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsQuery
import com.netflix.graphql.dgs.InputArgument

@DgsComponent
class CompanyQuery(private val companyService: CompanyService) {
    @DgsQuery
//    @PreAuthorize("isAuthenticated()")
    fun getCompany(@InputArgument("slug") slug: String): Company {
        val company = companyService.getCompanyBySlug(slug)
        return Company.fromEntity(company)
    }
}
