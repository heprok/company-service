package com.briolink.companyservice.api.graphql.query

import com.briolink.companyservice.api.graphql.fromEntity
import com.briolink.companyservice.api.types.Keyword
import com.briolink.companyservice.common.jpa.read.repository.KeywordReadRepository
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsQuery
import com.netflix.graphql.dgs.InputArgument
import org.springframework.security.access.prepost.PreAuthorize

@DgsComponent
class KeywordQuery(private val keywordReadRepository: KeywordReadRepository) {
    @DgsQuery
    @PreAuthorize("isAuthenticated()")
    fun getKeywords(@InputArgument query: String): List<Keyword> {
        val keywords = keywordReadRepository.findByName(query.ifBlank { null })

        return keywords.map {
            Keyword.fromEntity(it)
        }
    }
}
