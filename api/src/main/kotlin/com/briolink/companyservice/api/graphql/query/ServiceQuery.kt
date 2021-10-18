package com.briolink.companyservice.api.graphql.query

import com.briolink.companyservice.api.graphql.fromEntity
import com.briolink.companyservice.api.service.ServiceCompanyService
import com.briolink.companyservice.api.types.Service
import com.briolink.companyservice.api.types.ServiceFilter
import com.briolink.companyservice.api.types.ServiceList
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsQuery
import com.netflix.graphql.dgs.InputArgument
import org.springframework.security.access.prepost.PreAuthorize
import java.util.UUID

@DgsComponent
class ServiceQuery(private val serviceCompanyService: ServiceCompanyService) {
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
                totalItems = page.totalElements.toInt()
        )

    }

    @DgsQuery
    @PreAuthorize("isAuthenticated()")
    fun getServicesFilter(
        @InputArgument("companyId") companyId: String,
        @InputArgument("limit") limit: Int,
        @InputArgument("filter") filter: ServiceFilter = ServiceFilter(),
        @InputArgument("offset") offset: Int,
    ): ServiceList {
        val page = serviceCompanyService.findAll(companyId = UUID.fromString(companyId), limit = limit, offset = offset, filter = filter)
        return ServiceList(
                items = page.content.map {
                    Service.fromEntity(it)
                },
                totalItems = page.totalElements.toInt()
        )

    }


}
