package com.briolink.companyservice.api.graphql.mutation

import com.briolink.companyservice.api.types.ConfirmEmployeeResult
import com.briolink.companyservice.api.types.DelOrHideResult
import com.briolink.companyservice.api.types.EditEmployeeRightResult
import com.briolink.companyservice.api.types.PermissionRight
import com.briolink.lib.permission.AllowedRights
import com.briolink.lib.permission.enumeration.AccessObjectTypeEnum
import com.briolink.lib.permission.enumeration.PermissionRightEnum
import com.briolink.lib.permission.service.PermissionService
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsMutation
import com.netflix.graphql.dgs.InputArgument

@DgsComponent
class PermissionMutation(
    private val permissionService: PermissionService
) {
    @DgsMutation
    @AllowedRights(AccessObjectTypeEnum.Company, [PermissionRightEnum.IsCanEditEmployees])
    fun deleteEmployee(
        @InputArgument("companyId") accessObjectId: String,
        @InputArgument userId: String,
        @InputArgument isFull: Boolean
    ): DelOrHideResult {
        return DelOrHideResult(success = false, userErrors = listOf())
    }

    @DgsMutation
    @AllowedRights(AccessObjectTypeEnum.Company, [PermissionRightEnum.IsCanEditEmployees])
    fun editEmployeeRight(
        @InputArgument("companyId") accessObjectId: String,
        @InputArgument userId: String,
        @InputArgument rights: List<PermissionRight>
    ): EditEmployeeRightResult {
        return EditEmployeeRightResult(success = false, userErrors = listOf())
    }

    @DgsMutation
    @AllowedRights(AccessObjectTypeEnum.Company, [PermissionRightEnum.IsCanEditEmployees])
    fun confirmEmployee(
        @InputArgument("companyId") accessObjectId: String,
        @InputArgument userId: String,
    ): ConfirmEmployeeResult {
        return ConfirmEmployeeResult(success = false, userErrors = listOf())
    }
}
