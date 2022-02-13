package com.briolink.companyservice.api.service.employee.dto

import com.briolink.lib.permission.enumeration.PermissionRightEnum

data class EmployeeListFilter(
    val workDateRange: DateRange? = null,
    val jobPositionTitles: List<String>? = null,
    val rights: List<PermissionRightEnum>? = null,
    val search: String? = null,
)
