package com.briolink.companyservice.api.graphql.mutation

import com.briolink.companyservice.api.service.ServiceCompanyService
import com.briolink.companyservice.api.types.DelOrHideResult
import com.briolink.companyservice.api.types.Error
import com.briolink.companyservice.api.util.SecurityUtil.currentUserAccountId
import com.briolink.permission.enumeration.AccessObjectTypeEnum
import com.briolink.permission.enumeration.PermissionRightEnum
import com.briolink.permission.service.PermissionService
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsMutation
import com.netflix.graphql.dgs.InputArgument
import org.springframework.security.access.prepost.PreAuthorize
import java.util.UUID

@DgsComponent
class ServiceMutation(
    private val serviceCompanyService: ServiceCompanyService,
    private val permissionService: PermissionService,
) {
    @DgsMutation(field = "hideCompanyService")
    @PreAuthorize("isAuthenticated()")
    fun hide(
        @InputArgument("companyId") companyId: String,
        @InputArgument("serviceId") serviceId: String
    ): DelOrHideResult {
        return if (permissionService.isHavaPermission(
                accessObjectId = UUID.fromString(companyId),
                permissionRight = PermissionRightEnum.ServiceCrud,
                userId = currentUserAccountId,
                accessObjectType = AccessObjectTypeEnum.Company
            )
        ) {
            serviceCompanyService.toggleVisibilityByIdAndCompanyId(
                companyId = UUID.fromString(companyId),
                serviceId = UUID.fromString(serviceId),
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

    @DgsMutation
    @PreAuthorize("isAuthenticated()")
    fun deleteCompanyService(
        @InputArgument("serviceId") serviceId: String,
        @InputArgument("companyId") companyId: String
    ): DelOrHideResult {
        return if (permissionService.isHavaPermission(
                accessObjectId = UUID.fromString(companyId),
                permissionRight = PermissionRightEnum.ServiceCrud,
                userId = currentUserAccountId,
                accessObjectType = AccessObjectTypeEnum.Company
            )
        ) {
            serviceCompanyService.deleteServiceInCompany(
                serviceId = UUID.fromString(serviceId),
                userId = currentUserAccountId
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
