package com.briolink.companyservice.api.graphql.mapper

import com.briolink.companyservice.api.types.CreateTagInput
import com.briolink.companyservice.api.types.CreateTagWithCountInput
import com.briolink.companyservice.api.types.TagType
import com.briolink.lib.dictionary.model.Tag
import com.briolink.lib.dictionary.model.TagWithCount

fun CreateTagInput.toTag(): Tag {
    if (id != null && name != null) throw IllegalArgumentException("Tag must be contains name ")

    return Tag(
        id = id ?: "",
        type = type.toEnum(),
        name = name ?: ""
    ).also {
        it.path = path
    }
}

fun CreateTagWithCountInput.toTagWithCount(): TagWithCount {
    if (id != null && name != null) throw IllegalArgumentException("Tag must be id or name")

    return TagWithCount(
        id = id ?: "",
        type = type.toEnum(),
        name = name ?: "",
        count = count
    ).also {
        it.path = path
    }
}

fun TagType.toEnum() = com.briolink.lib.dictionary.enumeration.TagType.valueOf(name)

fun List<CreateTagInput>.toMutableSetTags(): MutableSet<Tag> = this.map { it.toTag() }.toMutableSet()
