// package com.briolink.companyservice.common.mapper
//
// import com.briolink.companyservice.common.domain.v1_0.Connection
// import com.briolink.companyservice.common.jpa.read.entity.ConnectionReadEntity
// import org.mapstruct.Mapper
// import org.mapstruct.Mapping
// import org.mapstruct.NullValuePropertyMappingStrategy
// import org.mapstruct.ReportingPolicy
// import org.mapstruct.factory.Mappers
//
// @Mapper(
//        unmappedTargetPolicy = ReportingPolicy.IGNORE,
//        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
// )
// interface ConnectionMapper {
//    companion object {
//        val INSTANCE: ConnectionMapper = Mappers.getMapper(ConnectionMapper::class.java)
//    }
//
//    fun toDomain(input: ConnectionWriteEntity): Connection
//    @Mapping(source = ".", target = "data")
//    fun toReadEntity(entity: Connection): ConnectionReadEntity
// }
