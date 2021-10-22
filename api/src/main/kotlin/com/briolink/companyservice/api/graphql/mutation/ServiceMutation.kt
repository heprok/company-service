package com.briolink.companyservice.api.graphql.mutation

import com.briolink.companyservice.api.graphql.SecurityUtil
import com.briolink.companyservice.api.service.CompanyService
import com.briolink.companyservice.api.service.ServiceCompanyService
import com.briolink.companyservice.api.types.HideCompanyServiceResult
import com.briolink.companyservice.common.jpa.read.entity.UserPermissionRoleReadEntity
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsMutation
import com.netflix.graphql.dgs.InputArgument
import org.springframework.security.access.prepost.PreAuthorize
import java.util.*

@DgsComponent
class ServiceMutation(
    val serviceCompanyService: ServiceCompanyService,
    val companyService: CompanyService
) {
    @DgsMutation(field = "hideCompanyService")
    @PreAuthorize("isAuthenticated()")
    fun hide(
        @InputArgument("companyId") companyId: String,
        @InputArgument("serviceId") serviceId: String,
        @InputArgument("isHide") isHide: Boolean
    ): HideCompanyServiceResult {
//            val role = UserPermissionRoleReadEntity.RoleType.Owner
        val role = companyService.getPermission(UUID.fromString(companyId), SecurityUtil.currentUserAccountId)
        return if (role == UserPermissionRoleReadEntity.RoleType.Owner) {
            serviceCompanyService.hideInCompany(
                    companyId = UUID.fromString(companyId),
                    serviceId = UUID.fromString(serviceId),
                    isHide = isHide,
            )
            HideCompanyServiceResult(
                    success = true,
            )
        } else {
            HideCompanyServiceResult(
                    success = false,
                    error = com.briolink.companyservice.api.types.Error("403 Permission denied"),
            )
        }
    }

    @DgsMutation(field = "deleteCompanyService")
    @PreAuthorize("isAuthenticated()")
    fun delete(
        @InputArgument("companyId") companyId: String,
        @InputArgument("serviceId") serviceId: String
    ): Boolean {
        return false
    }
}

