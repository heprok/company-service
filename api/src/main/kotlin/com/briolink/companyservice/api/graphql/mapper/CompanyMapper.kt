package com.briolink.companyservice.api.graphql.mapper

import com.briolink.companyservice.api.types.Company
import com.briolink.companyservice.common.jpa.read.entity.CompanyReadEntity
import com.briolink.companyservice.common.jpa.write.entity.CompanyWriteEntity

fun Company.Companion.fromEntity(entity: CompanyWriteEntity): Company {
    TODO("Not implemented")
}

fun Company.Companion.fromEntity(entity: CompanyReadEntity): Company {
    TODO("Not implemented")
}
