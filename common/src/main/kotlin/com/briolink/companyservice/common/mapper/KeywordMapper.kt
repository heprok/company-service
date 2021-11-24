package com.briolink.companyservice.common.mapper

import com.briolink.companyservice.common.domain.v1_0.Keyword
import com.briolink.companyservice.common.jpa.read.entity.KeywordReadEntity
import com.briolink.companyservice.common.jpa.write.entity.KeywordWriteEntity
import org.mapstruct.Mapper
import org.mapstruct.NullValuePropertyMappingStrategy
import org.mapstruct.ReportingPolicy
import org.mapstruct.factory.Mappers

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
)
interface KeywordMapper {
    companion object {
        val INSTANCE: KeywordMapper = Mappers.getMapper(KeywordMapper::class.java)
    }

    fun toDomain(input: KeywordWriteEntity): Keyword
    fun toReadEntity(entity: Keyword): KeywordReadEntity
}
