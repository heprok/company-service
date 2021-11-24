// package com.briolink.companyservice.common.mapper
//
// import com.briolink.companyservice.common.domain.v1_0.Company
// import com.briolink.companyservice.common.jpa.read.entity.CompanyReadEntity
// import com.briolink.companyservice.common.jpa.write.entity.CompanyWriteEntity
// import org.mapstruct.Mapper
// import org.mapstruct.Mapping
// import org.mapstruct.Mappings
// import org.mapstruct.NullValuePropertyMappingStrategy
// import org.mapstruct.ReportingPolicy
// import org.mapstruct.factory.Mappers
//
// @Mapper(
//        unmappedTargetPolicy = ReportingPolicy.IGNORE,
//        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
// )
// interface CompanyMapper {
//    companion object {
//        val INSTANCE: CompanyMapper = Mappers.getMapper(CompanyMapper::class.java)
//    }
//
//    @Mappings(
//            Mapping(source = "industry_id", target = "")
//    )
//    fun toDomain(input: CompanyWriteEntity): Company
//    @Mapping(source = ".", target = "data")
//    fun toReadEntity(entity: Company): CompanyReadEntity
// }
