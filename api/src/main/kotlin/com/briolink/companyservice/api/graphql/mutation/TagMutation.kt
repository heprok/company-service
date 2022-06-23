package com.briolink.companyservice.api.graphql.mutation

import com.briolink.companyservice.api.graphql.mapper.toEnum
import com.briolink.companyservice.api.graphql.mapper.toTag
import com.briolink.companyservice.api.types.CreateTagInput
import com.briolink.companyservice.api.types.Tag
import com.briolink.lib.dictionary.dto.TagCreateRequest
import com.briolink.lib.dictionary.service.DictionaryService
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsMutation
import com.netflix.graphql.dgs.InputArgument
import javax.validation.ValidationException

@DgsComponent
class TagMutation(
    private val dictionaryService: DictionaryService,
) {

    @DgsMutation
    fun createTag(@InputArgument input: CreateTagInput): Tag {
        val dto = input.toTag()
        val tag = dictionaryService.getTagIfNotExistsCreate(
            request = TagCreateRequest(
                id = dto.id,
                name = dto.name,
                type = dto.type,
                path = dto.path
            ),
        )
        return Tag(tag.id, tag.name)
    }

    @DgsMutation
    fun createTagBatch(@InputArgument listInput: List<CreateTagInput>): List<Tag> {
        dictionaryService.createTags(
            listInput.map { input ->
                input.let {
                    if (it.name == null && it.id == null) throw ValidationException("id and name must be not null ")
                    TagCreateRequest(
                        id = it.id,
                        name = it.name ?: "",
                        type = it.type.toEnum(),
                        path = it.path
                    )
                }
            }
        ).let { list ->
            return list.map { Tag(it.id, it.name) }
        }
    }
}
