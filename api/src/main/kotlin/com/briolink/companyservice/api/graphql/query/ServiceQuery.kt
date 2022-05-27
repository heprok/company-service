package com.briolink.companyservice.api.graphql.query

import com.briolink.companyservice.api.graphql.fromEntity
import com.briolink.companyservice.api.service.ServiceCompanyService
import com.briolink.companyservice.api.types.Service
import com.briolink.companyservice.api.types.ServiceFilter
import com.briolink.companyservice.api.types.ServiceList
import com.briolink.companyservice.api.types.ServiceSort
import com.briolink.companyservice.api.types.ServiceSortBy
import com.briolink.companyservice.api.types.SortDirection
import com.briolink.lib.common.utils.BlSecurityUtils
import com.briolink.lib.permission.service.PermissionService
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsQuery
import com.netflix.graphql.dgs.InputArgument
import java.util.UUID

@DgsComponent
class ServiceQuery(
    private val serviceCompanyService: ServiceCompanyService,
    private val permissionService: PermissionService,
) {
    @DgsQuery
    fun getServices(
        @InputArgument companyId: String,
        @InputArgument sort: ServiceSort?,
        @InputArgument limit: Int?,
        @InputArgument filter: ServiceFilter?,
        @InputArgument offset: Int?,

    ): ServiceList {
        return if (serviceCompanyService.countServiceByCompany(companyId = UUID.fromString(companyId))) {
            val filterSecurity =
                if (!BlSecurityUtils.isGuest && permissionService.checkPermission(
                        userId = BlSecurityUtils.currentUserId,
                        accessObjectId = UUID.fromString(companyId),
                        right = "EditCompanyService@Company"
                    )
                ) {
                    filter?.copy(isHide = false) ?: ServiceFilter(isHide = false)
                } else filter

            val page =
                serviceCompanyService.findAll(
                    companyId = UUID.fromString(companyId),
                    sort = sort ?: ServiceSort(ServiceSortBy.Name, SortDirection.ASC),
                    limit = limit ?: 10,
                    offset = offset ?: 0,
                    filter = filterSecurity,
                )

            ServiceList(
                items = page.content.map { Service.fromEntity(it) },
                totalItems = page.totalElements.toInt(),
            )
        } else {
            ServiceList(items = listOf(), totalItems = -1)
        }
    }

    @DgsQuery
    fun getServicesCount(
        @InputArgument companyId: String,
        @InputArgument filter: ServiceFilter?
    ): Int = serviceCompanyService.count(companyId = UUID.fromString(companyId), filter = filter).toInt()
}
