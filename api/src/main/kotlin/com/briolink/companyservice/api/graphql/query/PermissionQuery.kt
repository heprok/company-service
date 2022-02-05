package com.briolink.companyservice.api.graphql.query

import com.briolink.companyservice.api.graphql.fromEntity
import com.briolink.companyservice.api.service.ServiceCompanyService
import com.briolink.companyservice.api.types.CompanyInfoByServiceItem
import com.briolink.companyservice.api.types.CompanyInfoByServiceList
import com.briolink.companyservice.api.types.Service
import com.briolink.companyservice.api.types.ServiceFilter
import com.briolink.companyservice.api.types.ServiceList
import com.briolink.companyservice.api.types.ServiceSort
import com.briolink.companyservice.api.util.SecurityUtil
import com.briolink.lib.permission.enumeration.AccessObjectTypeEnum
import com.briolink.lib.permission.enumeration.PermissionRightEnum
import com.briolink.lib.permission.service.PermissionService
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsQuery
import com.netflix.graphql.dgs.InputArgument
import org.springframework.security.access.prepost.PreAuthorize
import java.util.UUID

@DgsComponent
class PermissionQuery(
    private val serviceCompanyService: ServiceCompanyService,
    private val permissionService: PermissionService,
) {
    @DgsQuery
    @PreAuthorize("isAuthenticated()")
    fun getServices(
        @InputArgument("companyId") companyId: String,
        @InputArgument("sort") sort: ServiceSort,
        @InputArgument("limit") limit: Int,
        @InputArgument("filter") filter: ServiceFilter?,
        @InputArgument("offset") offset: Int,

    ): ServiceList {
        return if (serviceCompanyService.countServiceByCompany(companyId = UUID.fromString(companyId))) {
            val filterSecurity =
                if (permissionService.isHavePermission(
                        accessObjectType = AccessObjectTypeEnum.Company,
                        userId = SecurityUtil.currentUserAccountId,
                        accessObjectId = UUID.fromString(companyId),
                        permissionRight = PermissionRightEnum.IsCanEditCompanyService
                    )
                ) {
                    filter?.copy(isHide = false) ?: ServiceFilter(isHide = false)
                } else filter
            val page =
                serviceCompanyService.findAll(
                    companyId = UUID.fromString(companyId),
                    sort = sort,
                    limit = limit,
                    offset = offset,
                    filter = filterSecurity,
                )
            ServiceList(
                items = page.content.map {
                    Service.fromEntity(it)
                },
                totalItems = page.totalElements.toInt(),
            )
        } else {
            ServiceList(items = listOf(), totalItems = -1)
        }
    }

    @DgsQuery
    @PreAuthorize("isAuthenticated()")
    fun getServicesCount(
        @InputArgument("companyId") companyId: String,
        @InputArgument("filter") filter: ServiceFilter?
    ): Int = serviceCompanyService.count(companyId = UUID.fromString(companyId), filter = filter).toInt()

    @DgsQuery
    @PreAuthorize("isAuthenticated()")
    fun getVerifyUsesByService(
        @InputArgument("serviceId") serviceId: String,
        @InputArgument("limit") limit: Int,
        @InputArgument("offset") offset: Int,
    ): CompanyInfoByServiceList {
        val page = serviceCompanyService.getVerifyUsesByServiceId(
            serviceId = UUID.fromString(serviceId),
            limit = limit,
            offset = offset
        )
        return CompanyInfoByServiceList(
            items = page.content.map {
                CompanyInfoByServiceItem.fromEntity(it)
            },
            totalItems = page.totalElements.toInt()
        )
    }
}
