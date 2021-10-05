package com.briolink.companyservice.api.graphql.query

import com.briolink.companyservice.api.graphql.fromEntity
import com.briolink.companyservice.api.types.Keyword
import com.briolink.companyservice.common.jpa.read.repository.KeywordReadRepository
import com.briolink.companyservice.common.util.StringUtil
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsQuery
import com.netflix.graphql.dgs.InputArgument

@DgsComponent
class OccupationQuery(private val keywordReadRepository: KeywordReadRepository ) {
    @DgsQuery
//    @PreAuthorize("isAuthenticated()")
    fun getOccupations(@InputArgument("query") query: String): List<Keyword> {
        val escapeQuery = StringUtil.replaceNonWord(query)
        val keywords = if (query.isNotEmpty()) keywordReadRepository.findByName("($escapeQuery*) (\"$escapeQuery\")") else listOf()

        return keywords.map {
            Keyword.fromEntity(it)
        }
    }
}
