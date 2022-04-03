package com.briolink.companyservice.api.service.employee.dto

import com.briolink.lib.permission.model.PermissionRight

data class EmployeeListFilter(
    val workDateRange: DateRange? = null,
    val jobPositionTitles: List<String>? = null,
    val rights: List<PermissionRight>? = null,
    val search: String? = null,
)
