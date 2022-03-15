package com.briolink.companyservice.api.graphql.mutation

import com.briolink.companyservice.api.service.connection.ConnectionService
import com.briolink.companyservice.api.types.DelOrHideResult
import com.briolink.lib.permission.AllowedRights
import com.briolink.lib.permission.enumeration.AccessObjectTypeEnum
import com.briolink.lib.permission.enumeration.PermissionRightEnum
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsMutation
import com.netflix.graphql.dgs.InputArgument
import java.util.UUID

@DgsComponent
class ConnectionMutation(
    private val connectionService: ConnectionService,
) {
    @DgsMutation
    @AllowedRights(accessObjectType = AccessObjectTypeEnum.Company, value = [PermissionRightEnum.IsCanEditProject])
    fun hideCompanyConnection(
        @InputArgument("companyId") accessObjectId: String,
        @InputArgument("connectionId") connectionId: String,
        @InputArgument("isHide") hidden: Boolean
    ): DelOrHideResult {
        connectionService.changeVisibilityByIdAndCompanyId(
            companyId = UUID.fromString(accessObjectId),
            connectionId = UUID.fromString(connectionId),
            hidden = hidden,
        )
        return DelOrHideResult(
            success = true,
            userErrors = listOf(),
        )
    }

    @DgsMutation
    @AllowedRights(accessObjectType = AccessObjectTypeEnum.Company, value = [PermissionRightEnum.IsCanEditProject])
    fun deleteCompanyConnection(
        @InputArgument("companyId") accessObjectId: String,
        @InputArgument("connectionId") connectionId: String
    ): DelOrHideResult {
        connectionService.deleteConnectionInCompany(
            companyId = UUID.fromString(accessObjectId),
            connectionId = UUID.fromString(connectionId),
        )
        return DelOrHideResult(
            success = true,
            userErrors = listOf(),
        )
    }
}
