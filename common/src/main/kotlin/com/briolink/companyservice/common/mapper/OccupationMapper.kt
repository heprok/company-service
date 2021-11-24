package com.briolink.companyservice.common.mapper

import com.briolink.companyservice.common.domain.v1_0.Occupation
import com.briolink.companyservice.common.jpa.read.entity.OccupationReadEntity
import com.briolink.companyservice.common.jpa.write.entity.OccupationWriteEntity
import org.mapstruct.Mapper
import org.mapstruct.NullValuePropertyMappingStrategy
import org.mapstruct.ReportingPolicy
import org.mapstruct.factory.Mappers

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
)
interface OccupationMapper {
    companion object {
        val INSTANCE: OccupationMapper = Mappers.getMapper(OccupationMapper::class.java)
    }

    fun toDomain(input: OccupationWriteEntity): Occupation
    fun toReadEntity(entity: Occupation): OccupationReadEntity
}
