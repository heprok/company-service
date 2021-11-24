package com.briolink.companyservice.common.mapper

import com.briolink.companyservice.common.domain.v1_0.Industry
import com.briolink.companyservice.common.jpa.read.entity.IndustryReadEntity
import com.briolink.companyservice.common.jpa.write.entity.IndustryWriteEntity
import org.mapstruct.Mapper
import org.mapstruct.NullValuePropertyMappingStrategy
import org.mapstruct.ReportingPolicy
import org.mapstruct.factory.Mappers

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
)
interface IndustryMapper {
    companion object {
        val INSTANCE: IndustryMapper = Mappers.getMapper(IndustryMapper::class.java)
    }

    fun toDomain(input: IndustryWriteEntity): Industry
    fun toReadEntity(entity: Industry): IndustryReadEntity
}
