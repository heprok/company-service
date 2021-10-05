package com.briolink.companyservice.api.graphql.query

import com.briolink.companyservice.api.graphql.fromEntity
import com.briolink.companyservice.api.types.Industry
import com.briolink.companyservice.common.jpa.read.repository.IndustryReadRepository
import com.briolink.companyservice.common.util.StringUtil
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsQuery
import com.netflix.graphql.dgs.InputArgument

@DgsComponent
class IndustryQuery(private val industryReadRepository: IndustryReadRepository ) {
    @DgsQuery
//    @PreAuthorize("isAuthenticated()")
    fun getIndustries(@InputArgument("query") query: String): List<Industry> {
        val escapeQuery = StringUtil.replaceNonWord(query)
        val industries = if (query.isNotEmpty()) industryReadRepository.findByName("($escapeQuery*) (\"$escapeQuery\")") else listOf()

        return industries.map {
            Industry.fromEntity(it)
        }
    }
}
