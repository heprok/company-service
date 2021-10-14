package com.briolink.companyservice.api.graphql.query

import com.briolink.companyservice.api.graphql.fromEntity
import com.briolink.companyservice.api.types.Occupation
import com.briolink.companyservice.common.jpa.read.repository.OccupationReadRepository
import com.briolink.companyservice.common.util.StringUtil
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsQuery
import com.netflix.graphql.dgs.InputArgument
import org.springframework.security.access.prepost.PreAuthorize

@DgsComponent
class OccupationQuery(private val occupationReadRepository: OccupationReadRepository) {
    @DgsQuery
    @PreAuthorize("isAuthenticated()")
    fun getOccupations(@InputArgument("query") query: String): List<Occupation> {
        val escapeQuery = StringUtil.replaceNonWord(query)
        val occupations = if (query.isNotEmpty()) occupationReadRepository.findByName("($escapeQuery*) (\"$escapeQuery\")") else listOf()

        return occupations.map {
            Occupation.fromEntity(it)
        }
    }
}