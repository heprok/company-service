package com.briolink.companyservice.api.graphql.mutation

import com.briolink.companyservice.api.service.CompanyService
import com.briolink.companyservice.api.service.PermissionService
import com.briolink.companyservice.api.service.ServiceCompanyService
import com.briolink.companyservice.api.types.DelOrHideResult
import com.briolink.companyservice.api.types.Error
import com.briolink.companyservice.common.jpa.enumration.AccessObjectTypeEnum
import com.briolink.companyservice.common.jpa.enumration.PermissionRightEnum
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsMutation
import com.netflix.graphql.dgs.InputArgument
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.RequestHeader
import java.util.*

@DgsComponent
class ServiceMutation(
    private val serviceCompanyService: ServiceCompanyService,
    private val permissionService: PermissionService,
    private val companyService: CompanyService
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
                    accessObjectType = AccessObjectTypeEnum.CompanyService,
            )) {
            serviceCompanyService.toggleVisibilityByIdAndCompanyId(
                    companyId = UUID.fromString(companyId),
                    serviceId = UUID.fromString(serviceId))
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
        @InputArgument("companyId") companyId: String,
        @RequestHeader("Authorization") authorization: String
    ): DelOrHideResult {
        return if (permissionService.isHavePermission(
                    companyId = UUID.fromString(companyId),
                    permissionRight = PermissionRightEnum.ServiceCrud,
                    accessObjectType = AccessObjectTypeEnum.CompanyService,
            )) {
            serviceCompanyService.deleteServiceInCompany(
                    serviceId = UUID.fromString(serviceId),
                    authorization = authorization
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

