package com.briolink.companyservice.api.graphql.mutation

import com.briolink.companyservice.api.service.ServiceCompanyService
import com.briolink.companyservice.api.types.DelOrHideResult
import com.briolink.companyservice.api.types.Error
import com.briolink.companyservice.api.util.SecurityUtil.currentUserAccountId
import com.briolink.companyservice.common.jpa.enumeration.AccessObjectTypeEnum
import com.briolink.companyservice.common.jpa.enumeration.PermissionRightEnum
import com.briolink.companyservice.common.service.PermissionService
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
        return if (permissionService.isHavePermission(
                companyId = UUID.fromString(companyId),
                permissionRight = PermissionRightEnum.ServiceCrud,
                userId = currentUserAccountId,
                accessObjectType = AccessObjectTypeEnum.CompanyService,
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

    @DgsMutation(field = "deleteCompanyService")
    @PreAuthorize("isAuthenticated()")
    fun deleteCompanyService(
        @InputArgument("serviceId") serviceId: String,
        @InputArgument("companyId") companyId: String
    ): DelOrHideResult {
        return if (permissionService.isHavePermission(
                companyId = UUID.fromString(companyId),
                permissionRight = PermissionRightEnum.ServiceCrud,
                userId = currentUserAccountId,
                accessObjectType = AccessObjectTypeEnum.CompanyService,
            )
        ) {
            serviceCompanyService.deleteServiceInCompany(
                serviceId = UUID.fromString(serviceId)
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
