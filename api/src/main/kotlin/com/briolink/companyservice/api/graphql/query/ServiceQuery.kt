package com.briolink.companyservice.api.graphql.query

import com.briolink.companyservice.api.graphql.SecurityUtil
import com.briolink.companyservice.api.graphql.fromEntity
import com.briolink.companyservice.api.service.CompanyService
import com.briolink.companyservice.api.service.ServiceCompanyService
import com.briolink.companyservice.api.types.Service
import com.briolink.companyservice.api.types.ServiceFilter
import com.briolink.companyservice.api.types.ServiceSort
import com.briolink.companyservice.api.types.ServiceList
import com.briolink.companyservice.common.jpa.read.entity.UserPermissionRoleReadEntity
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsQuery
import com.netflix.graphql.dgs.InputArgument
import org.springframework.security.access.prepost.PreAuthorize
import java.util.UUID

@DgsComponent
class ServiceQuery(
    private val serviceCompanyService: ServiceCompanyService,
    private val companyService: CompanyService,
) {
    @Deprecated("Not working filter")
    @DgsQuery
    @PreAuthorize("isAuthenticated()")
    fun getServices(
        @InputArgument("limit") limit: Int,
        @InputArgument("offset") offset: Int,
        @InputArgument("companyId") companyId: String
    ): ServiceList {
        val page = serviceCompanyService.getByCompanyId(UUID.fromString(companyId), limit, offset)
        return ServiceList(
                items = page.content.map {
                    Service.fromEntity(it)
                },
                totalItems = page.totalElements.toInt(),
        )
    }

    @DgsQuery
    @PreAuthorize("isAuthenticated()")
    fun getServicesFilter(
        @InputArgument("companyId") companyId: String,
        @InputArgument("sort") sort: ServiceSort,
        @InputArgument("limit") limit: Int,
        @InputArgument("filter") filter: ServiceFilter?,
        @InputArgument("offset") offset: Int,

        ): ServiceList {
        return if (serviceCompanyService.countServiceByCompany(companyId = UUID.fromString(companyId))) {
            val filterSecurity =
                    if (companyService.getPermission(UUID.fromString(companyId), SecurityUtil.currentUserAccountId)
                        != UserPermissionRoleReadEntity.RoleType.Owner) {
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
}
