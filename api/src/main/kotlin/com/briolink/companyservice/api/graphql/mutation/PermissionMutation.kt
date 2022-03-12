package com.briolink.companyservice.api.graphql.mutation

import com.briolink.companyservice.api.service.employee.EmployeeService
import com.briolink.companyservice.api.types.ConfirmEmployeeResult
import com.briolink.companyservice.api.types.DelOrHideResult
import com.briolink.companyservice.api.types.EditEmployeeRightResult
import com.briolink.companyservice.api.types.PermissionRight
import com.briolink.companyservice.api.types.PermissionRole
import com.briolink.companyservice.api.util.SecurityUtil
import com.briolink.lib.permission.AllowedRights
import com.briolink.lib.permission.enumeration.AccessObjectTypeEnum
import com.briolink.lib.permission.enumeration.PermissionRightEnum
import com.briolink.lib.permission.exception.AccessDeniedException
import com.briolink.lib.permission.service.PermissionService
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsMutation
import com.netflix.graphql.dgs.InputArgument
import java.util.UUID

@DgsComponent
class PermissionMutation(
    private val permissionService: PermissionService,
    private val employeeService: EmployeeService,
) {
    @DgsMutation
    @AllowedRights(AccessObjectTypeEnum.Company, [PermissionRightEnum.IsCanEditEmployees])
    fun deleteEmployee(
        @InputArgument("companyId") accessObjectId: String,
        @InputArgument userId: String,
        @InputArgument isFull: Boolean
    ): DelOrHideResult {
        return try {
            val success = if (isFull)
                employeeService.deleteEmployee(UUID.fromString(accessObjectId), SecurityUtil.currentUserAccountId, UUID.fromString(userId))
            else employeeService.setFormerEmployee(
                UUID.fromString(accessObjectId),
                SecurityUtil.currentUserAccountId,
                UUID.fromString(userId),
            )

            DelOrHideResult(success = success, userErrors = listOf())
        } catch (e: AccessDeniedException) {
            DelOrHideResult(
                success = false,
                userErrors = listOf(com.briolink.companyservice.api.types.Error("403 Permission denied")),
            )
        }
    }

    @DgsMutation
    @AllowedRights(AccessObjectTypeEnum.Company, [PermissionRightEnum.IsCanEditEmployees])
    fun editEmployeeRight(
        @InputArgument("companyId") accessObjectId: String,
        @InputArgument userId: String,
        @InputArgument role: PermissionRole,
        @InputArgument rights: List<PermissionRight>?
    ): EditEmployeeRightResult {
        return EditEmployeeRightResult(success = false, userErrors = listOf())
    }

    @DgsMutation
    @AllowedRights(AccessObjectTypeEnum.Company, [PermissionRightEnum.IsCanEditEmployees])
    fun confirmEmployee(
        @InputArgument("companyId") accessObjectId: String,
        @InputArgument userId: String,
        @InputArgument accept: Boolean
    ): ConfirmEmployeeResult {
        return ConfirmEmployeeResult(success = false, userErrors = listOf())
    }
}
