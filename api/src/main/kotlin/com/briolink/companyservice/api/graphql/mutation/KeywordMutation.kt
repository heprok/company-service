package com.briolink.companyservice.api.graphql.mutation

import com.briolink.companyservice.api.service.KeywordService
import com.briolink.companyservice.api.types.CreateKeywordInput
import com.briolink.companyservice.api.types.CreateKeywordResult
import com.briolink.companyservice.api.types.KeywordResultData
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsMutation
import com.netflix.graphql.dgs.InputArgument

@DgsComponent
class KeywordMutation(
    val keywordService: KeywordService,
) {
    @DgsMutation(field = "createKeyword")
    fun create(@InputArgument("input") keywordInput: CreateKeywordInput): CreateKeywordResult {
        val keyword = keywordService.create(keywordInput.name)
        return CreateKeywordResult(
                data = KeywordResultData(keyword.id.toString()),
                userErrors = listOf(),
        )
    }
}

