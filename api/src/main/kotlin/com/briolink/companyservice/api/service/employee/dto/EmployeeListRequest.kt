package com.briolink.companyservice.api.service.employee.dto

import java.util.UUID

data class EmployeeListRequest(
    val companyId: UUID,
    val filters: EmployeeListFilter? = null,
    val limit: Int = 10,
    val offset: Int = 0,
    val isUserWithPermission: Boolean = false,
    val isCurrentEmployees: Boolean = true,
)
