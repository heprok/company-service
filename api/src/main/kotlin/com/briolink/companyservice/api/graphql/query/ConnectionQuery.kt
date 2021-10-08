package com.briolink.companyservice.api.graphql.query

import com.briolink.companyservice.api.graphql.fromEntity
import com.briolink.companyservice.api.service.ConnectionService
import com.briolink.companyservice.api.types.Connection
import com.briolink.companyservice.api.types.ConnectionList
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsQuery
import com.netflix.graphql.dgs.InputArgument
import org.springframework.security.access.prepost.PreAuthorize
import java.util.UUID

@DgsComponent
class ConnectionQuery(private val connectionService: ConnectionService) {
    @DgsQuery
    @PreAuthorize("isAuthenticated()")
    fun getConnections(
        @InputArgument("limit") limit: Int,
        @InputArgument("offset") offset: Int,
        @InputArgument("companyId") companyId: String
    ): ConnectionList {
        val page = connectionService.getByCompanyId(UUID.fromString(companyId), limit, offset)
        return ConnectionList(
                items = page.content.map {
                    Connection.fromEntity(it)
                },
                totalItems = page.totalElements.toInt()
        )
    }
}
