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
import com.briolink.lib.permission.enumeration.AccessObjectTypeEnum
import com.briolink.lib.permission.enumeration.PermissionRightEnum
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsQuery
import com.netflix.graphql.dgs.InputArgument
import org.springframework.security.access.prepost.PreAuthorize
import java.util.UUID

@DgsComponent
class EmployeesQuery(private val employeeService: EmployeeService) {
    @DgsQuery
    @PreAuthorize("isAuthenticated()")
    fun getEmployees(
        @InputArgument("limit") limit: Int,
        @InputArgument("offset") offset: Int,
        @InputArgument("companyId") companyId: String
    ): UserList {
        val page = employeeService.getByCompanyId(UUID.fromString(companyId), limit, offset)
        return UserList(
            items = page.content.map {
                User.fromEntity(it)
            },
            totalItems = page.totalElements.toInt(),
        )
    }

    @DgsQuery
    @AllowedRights(AccessObjectTypeEnum.Company, [PermissionRightEnum.IsCanEditEmployees])
    fun getEmployeesTabs(
        @InputArgument("companyId") accessObjectId: String,
        @InputArgument filter: EmployeesEditorFilterParameters?
    ): List<EmployeesListCountByItem> {
        val listFilter = EmployeeListFilter(
            workDateRange = filter?.workDateRange?.let { DateRange(it.start, it.end) },
            jobPositionTitles = filter?.jobPositionTitles,
            rights = filter?.rights?.map { PermissionRightEnum.valueOf(it.name) },
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
    @AllowedRights(AccessObjectTypeEnum.Company, [PermissionRightEnum.IsCanEditEmployees])
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
    @AllowedRights(AccessObjectTypeEnum.Company, [PermissionRightEnum.IsCanEditEmployees])
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
