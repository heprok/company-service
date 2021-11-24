package com.briolink.companyservice.api.graphql.query

import com.briolink.companyservice.api.graphql.fromEntity
import com.briolink.companyservice.api.service.connection.ConnectionService
import com.briolink.companyservice.api.types.Industry
import com.briolink.companyservice.common.jpa.read.repository.IndustryReadRepository
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsQuery
import com.netflix.graphql.dgs.InputArgument
import org.springframework.security.access.prepost.PreAuthorize

@DgsComponent
class IndustryQuery(
    private val industryReadRepository: IndustryReadRepository,
    private val connectionService: ConnectionService
) {
    @DgsQuery
    @PreAuthorize("isAuthenticated()")
    fun getIndustries(
        @InputArgument("query") query: String,
        @InputArgument("companyId") companyId: String?
    ): List<Industry> {
        val industries = if (companyId == null && query.isNotEmpty()) {
            industryReadRepository.findByName(query.ifBlank { null })
        } else if (companyId != null) {
            connectionService.getIndustriesInConnectionFromCompany(companyId, query)
        } else {
            listOf()
        }

        return industries.map {
            Industry.fromEntity(it)
        }
    }
}
