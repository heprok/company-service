package com.briolink.companyservice.api.service.employee.dto

import com.briolink.companyservice.api.types.EmployeesEditorListOptions
import com.briolink.lib.permission.model.PermissionRight
import java.util.UUID

data class EmployeeListRequest(
    val companyId: UUID,
    val filters: EmployeeListFilter? = null,
    val limit: Int = 10,
    val offset: Int = 0,
    val isOnlyUserWithPermission: Boolean = false,
    val tab: EmployeeTab? = null,
    var forConfirmation: Boolean = false
) {
    companion object {
        fun fromType(companyId: String, options: EmployeesEditorListOptions) = EmployeeListRequest(
            companyId = UUID.fromString(companyId),
            filters = EmployeeListFilter(
                workDateRange = options.filter?.workDateRange?.let { DateRange(it.start, it.end) },
                jobPositionTitles = options.filter?.jobPositionTitles,
                rights = options.filter?.rights?.map { PermissionRight.fromString(it) },
                search = options.filter?.search,
            ),
            limit = options.limit,
            offset = options.offset,
            isOnlyUserWithPermission = options.isOnlyUserWithPermission ?: false,
            tab = options.tab?.let { EmployeeTab.valueOf(it.name) },
        )
    }
}

enum class EmployeeTab {
    Current,
    Former
}
