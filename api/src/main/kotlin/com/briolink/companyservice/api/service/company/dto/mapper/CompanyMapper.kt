package com.briolink.companyservice.api.service.company.dto.mapper

import com.briolink.companyservice.api.types.CompanyType
import com.briolink.companyservice.common.enumeration.CompanyTypeEnum

fun CompanyType.toEnum() = CompanyTypeEnum.valueOf(name)
