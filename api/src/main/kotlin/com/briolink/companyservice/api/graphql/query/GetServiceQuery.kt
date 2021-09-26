package com.briolink.companyservice.api.graphql.query

import com.briolink.companyservice.api.service.service.ServiceQueryService
import com.briolink.companyservice.api.graphql.fromEntity
import com.briolink.companyservice.api.types.Service
import com.briolink.companyservice.api.types.ServiceList
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsQuery
import com.netflix.graphql.dgs.InputArgument
import org.springframework.security.access.prepost.PreAuthorize
import java.util.UUID

@DgsComponent
class GetServiceQuery(private val serviceQueryService: ServiceQueryService ) {
    @DgsQuery
//    @PreAuthorize("isAuthenticated()")
    fun getServices(
        @InputArgument("limit") limit: Int,
        @InputArgument("offset") offset: Int,
        @InputArgument("companyId") companyId: String
    ): ServiceList {
        val page = serviceQueryService.getByCompanyId(UUID.fromString(companyId), limit, offset)
        return ServiceList(
                items = page.content.map {
                    Service.fromEntity(it)
                },
                totalItems = page.totalElements.toInt()
        )
    }
}
