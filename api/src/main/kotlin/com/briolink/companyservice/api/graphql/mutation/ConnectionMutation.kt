package com.briolink.companyservice.api.graphql.mutation

import com.briolink.companyservice.api.graphql.SecurityUtil
import com.briolink.companyservice.api.service.connection.ConnectionService
import com.briolink.companyservice.api.types.DelOrHideResult
import com.briolink.companyservice.api.types.Error
import com.briolink.companyservice.common.jpa.enumration.AccessObjectTypeEnum
import com.briolink.companyservice.common.jpa.enumration.PermissionRightEnum
import com.briolink.companyservice.common.service.PermissionService
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsMutation
import com.netflix.graphql.dgs.InputArgument
import java.util.UUID
import org.springframework.security.access.prepost.PreAuthorize

@DgsComponent
class ConnectionMutation(
    private val connectionService: ConnectionService,
    private val permissionService: PermissionService,
) {
    @DgsMutation(field = "hideCompanyConnection")
    @PreAuthorize("isAuthenticated()")
    fun hide(
        @InputArgument("companyId") companyId: String,
        @InputArgument("connectionId") connectionId: String,
        @InputArgument("isHide") isHide: Boolean
    ): DelOrHideResult {
        return if (permissionService.isHavePermission(
                    companyId = UUID.fromString(companyId),
                    userId = SecurityUtil.currentUserAccountId,
                    permissionRight = PermissionRightEnum.ConnectionCrud,
                    accessObjectType = AccessObjectTypeEnum.Connection,
            )) {
            connectionService.changeVisibilityByIdAndCompanyId(
                    companyId = UUID.fromString(companyId),
                    connectionId = UUID.fromString(connectionId),
                    isHide = isHide,
            )
            DelOrHideResult(
                    success = true,
                    userErrors = listOf(),
            )
        } else {
            DelOrHideResult(
                    success = false,
                    userErrors = listOf(Error("403 Permission denied")),
            )
        }
    }

    @DgsMutation(field = "deleteCompanyConnection")
    @PreAuthorize("isAuthenticated()")
    fun delete(
        @InputArgument("companyId") companyId: String,
        @InputArgument("connectionId") connectionId: String
    ): DelOrHideResult {
        return if (permissionService.isHavePermission(
                    companyId = UUID.fromString(companyId),
                    permissionRight = PermissionRightEnum.ConnectionCrud,
                    userId = SecurityUtil.currentUserAccountId,
                    accessObjectType = AccessObjectTypeEnum.Connection,
            )) {
            connectionService.deleteConnectionInCompany(
                    companyId = UUID.fromString(companyId),
                    connectionId = UUID.fromString(connectionId),
            )
            DelOrHideResult(
                    success = true,
                    userErrors = listOf(),
            )
        } else {
            DelOrHideResult(
                    success = false,
                    userErrors = listOf(Error("403 Permission denied")),
            )
        }
    }
}
