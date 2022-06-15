package com.briolink.companyservice.updater.mapper

import com.briolink.companyservice.common.domain.v2_0.CompanyType
import com.briolink.companyservice.common.enumeration.CompanyTypeEnum

fun CompanyType.toEnum() = CompanyTypeEnum.valueOf(this.name)
