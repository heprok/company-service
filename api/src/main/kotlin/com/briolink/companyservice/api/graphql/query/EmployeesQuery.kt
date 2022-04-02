package com.briolink.companyservice.api.graphql.query

import com.briolink.companyservice.api.graphql.fromEntity
import com.briolink.companyservice.api.service.employee.EmployeeService
import com.briolink.companyservice.api.service.employee.dto.DateRange
import com.briolink.companyservice.api.service.employee.dto.EmployeeListFilter
import com.briolink.companyservice.api.service.employee.dto.EmployeeListRequest
import com.briolink.companyservice.api.types.Employee
import com.briolink.companyservice.api.types.EmployeeList
import com.briolink.companyservice.api.types.EmployeesEditorFilterParameters
import com.briolink.companyservice.api.types.EmployeesEditorListOptions
import com.briolink.companyservice.api.types.EmployeesListCountByItem
import com.briolink.companyservice.api.types.User
import com.briolink.companyservice.api.types.UserList
import com.briolink.lib.permission.AllowedRights
import com.briolink.lib.permission.model.PermissionRight
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsQuery
import com.netflix.graphql.dgs.InputArgument
import java.util.UUID

@DgsComponent
class EmployeesQuery(private val employeeService: EmployeeService) {
    @DgsQuery
    fun getEmployees(
        @InputArgument limit: Int?,
        @InputArgument offset: Int?,
        @InputArgument companyId: String
    ): UserList {
        val page = employeeService.getByCompanyId(UUID.fromString(companyId), limit ?: 6, offset ?: 0)
        return UserList(
            items = page.content.map {
                User.fromEntity(it)
            },
            totalItems = page.totalElements.toInt(),
        )
    }

    @DgsQuery
    @AllowedRights(value = ["EditEmployees@Company"])
    fun getEmployeesTabs(
        @InputArgument("companyId") accessObjectId: String,
        @InputArgument filter: EmployeesEditorFilterParameters?
    ): List<EmployeesListCountByItem> {
        val listFilter = EmployeeListFilter(
            workDateRange = filter?.workDateRange?.let { DateRange(it.start, it.end) },
            jobPositionTitles = filter?.jobPositionTitles,
            rights = filter?.rights?.map { PermissionRight.fromString(it) },
            search = filter?.search,
        )

        return employeeService.getTabs(UUID.fromString(accessObjectId), listFilter, true).map {
            EmployeesListCountByItem(
                com.briolink.companyservice.api.types.EmployeeTab.valueOf(it.tab.name),
                value = it.value
            )
        }
    }

    @DgsQuery
    @AllowedRights(value = ["EditEmployees@Company"])
    fun getCompanyEmployeesEditor(
        @InputArgument("companyId") accessObjectId: String,
        @InputArgument options: EmployeesEditorListOptions,
    ): EmployeeList {
        val request = EmployeeListRequest.fromType(accessObjectId, options)

        val items = employeeService.getListByCompanyId(request)

        return EmployeeList(
            items = items.map { Employee.fromEntity(it) },
            totalItems = items.totalSize.toInt(),
        )
    }

    @DgsQuery
    @AllowedRights(value = ["EditEmployees@Company"])
    fun getConfirmationEmployees(
        @InputArgument("companyId") accessObjectId: String,
        @InputArgument options: EmployeesEditorListOptions,
    ): EmployeeList {
        val request = EmployeeListRequest.fromType(accessObjectId, options)
        request.forConfirmation = true
        val items = employeeService.getListByCompanyId(request)
        return EmployeeList(
            items = items.map { Employee.fromEntity(it) },
            totalItems = items.totalSize.toInt(),
        )
    }
}
