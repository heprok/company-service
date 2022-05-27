package com.briolink.companyservice.api.graphql.mutation

import com.briolink.companyservice.api.service.ServiceCompanyService
import com.briolink.companyservice.api.types.DelOrHideResult
import com.briolink.lib.common.utils.BlSecurityUtils
import com.briolink.lib.permission.AllowedRights
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsMutation
import com.netflix.graphql.dgs.InputArgument
import java.util.UUID

@DgsComponent
class ServiceMutation(
    private val serviceCompanyService: ServiceCompanyService,
) {
    @DgsMutation(field = "hideCompanyService")
    @AllowedRights(value = ["EditCompanyServiceCompany"])
    fun hide(
        @InputArgument("companyId") accessObjectId: String,
        @InputArgument("serviceId") serviceId: String
    ): DelOrHideResult {
        serviceCompanyService.toggleVisibilityByIdAndCompanyId(
            companyId = UUID.fromString(accessObjectId),
            serviceId = UUID.fromString(serviceId),
        )
        return DelOrHideResult(
            success = true,
            userErrors = listOf(),
        )
    }

    @DgsMutation
    @AllowedRights(value = ["EditCompanyServiceCompany"])
    fun deleteCompanyService(
        @InputArgument("serviceId") serviceId: String,
        @InputArgument("companyId") accessObjectId: String
    ): DelOrHideResult {
        serviceCompanyService.deleteServiceInCompany(
            serviceId = UUID.fromString(serviceId),
            userId = BlSecurityUtils.currentUserId,
        )
        return DelOrHideResult(
            success = true,
            userErrors = listOf(),
        )
    }
}
