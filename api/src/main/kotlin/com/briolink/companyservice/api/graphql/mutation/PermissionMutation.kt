package com.briolink.companyservice.api.graphql.mutation

import com.briolink.companyservice.api.service.UserJobPositionService
import com.briolink.companyservice.api.service.employee.EmployeeService
import com.briolink.companyservice.api.types.ConfirmEmployeeResult
import com.briolink.companyservice.api.types.DelOrHideResult
import com.briolink.companyservice.api.types.EditEmployeeRightResult
import com.briolink.companyservice.api.types.Error
import com.briolink.companyservice.api.types.PermissionRole
import com.briolink.companyservice.api.util.SecurityUtil.currentUserAccountId
import com.briolink.lib.permission.AllowedRights
import com.briolink.lib.permission.enumeration.PermissionRoleEnum
import com.briolink.lib.permission.exception.AccessDeniedException
import com.briolink.lib.permission.exception.notfound.UserPermissionRoleNotFoundException
import com.briolink.lib.permission.model.PermissionRight
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsMutation
import com.netflix.graphql.dgs.InputArgument
import java.util.UUID

@DgsComponent
class PermissionMutation(
    private val employeeService: EmployeeService,
    private val userJobPositionService: UserJobPositionService,
) {
    @DgsMutation
    @AllowedRights(["EditEmployees@Company"])
    fun deleteEmployee(
        @InputArgument("companyId") accessObjectId: String,
        @InputArgument userJobPositionIds: List<String>,
        @InputArgument userId: String,
        @InputArgument toFormer: Boolean
    ): DelOrHideResult {
        return try {
            if (employeeService.checkPermissionLevel(
                    UUID.fromString(accessObjectId),
                    byUserId = currentUserAccountId,
                    userId = UUID.fromString(userId),
                )
            ) throw AccessDeniedException()

            if (toFormer) {
                userJobPositionService.setFormerJobPosition(userJobPositionIds.map { UUID.fromString(it) })
            } else {
                userJobPositionService.deleteById(userJobPositionIds.map { UUID.fromString(it) })
            }

            DelOrHideResult(success = true, userErrors = listOf())
        } catch (e: AccessDeniedException) {
            DelOrHideResult(
                success = false,
                userErrors = listOf(Error("403 Permission denied")),
            )
        }
    }

    @DgsMutation
    @AllowedRights(["EditEmployees@Company"])
    fun editEmployeeRight(
        @InputArgument("companyId") accessObjectId: String,
        @InputArgument userId: String,
        @InputArgument role: PermissionRole,
        @InputArgument rights: List<String?>?
    ): EditEmployeeRightResult {

        return try {
            val success = employeeService.editPermissionRight(
                UUID.fromString(accessObjectId),
                UUID.fromString(userId),
                PermissionRoleEnum.valueOf(role.name),
                rights?.map { PermissionRight.fromString(it.toString()) },
            )
            EditEmployeeRightResult(success = success, userErrors = listOf())
        } catch (ex: UserPermissionRoleNotFoundException) {
            EditEmployeeRightResult(
                success = false,
                userErrors = listOf(Error("User permission with userId $userId and company $accessObjectId not found"))
            )
        }
    }

    @DgsMutation
    @AllowedRights(["EditEmployees@Company"])
    fun confirmEmployee(
        @InputArgument("companyId") accessObjectId: String,
        @InputArgument userId: String,
        @InputArgument accept: Boolean
    ): ConfirmEmployeeResult {
        return ConfirmEmployeeResult(success = false, userErrors = listOf())
    }
}
