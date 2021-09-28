package com.briolink.companyservice.api.graphql.mutation

import com.briolink.companyservice.api.service.CompanyService
import com.briolink.companyservice.api.types.CreateCompanyInput
import com.briolink.companyservice.common.jpa.write.entity.CompanyWriteEntity
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsMutation
import com.netflix.graphql.dgs.InputArgument

@DgsComponent
class CUDCompanyMutation (
    val service: CompanyService,
        ) {
    @DgsMutation(field = "createCompany")
    fun create(@InputArgument company: CreateCompanyInput) {
        service.createCompany(
                CompanyWriteEntity(
                        name = company.name,
                        website = company.website.toString()
                )
        )
    }
}
