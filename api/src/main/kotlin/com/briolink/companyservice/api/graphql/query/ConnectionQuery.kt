package com.briolink.companyservice.api.graphql.query

import com.briolink.companyservice.api.graphql.fromEntity
import com.briolink.companyservice.api.service.ConnectionService
import com.briolink.companyservice.api.types.Connection
import com.briolink.companyservice.api.types.ConnectionFilter
import com.briolink.companyservice.api.types.ConnectionList
import com.briolink.companyservice.api.types.ConnectionSort
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
        @InputArgument("companyId") companyId: String,
        @InputArgument("filter") filter: ConnectionFilter?,
        @InputArgument("sort") sort: ConnectionSort,
        @InputArgument("limit") limit: Int,
        @InputArgument("offset") offset: Int,
    ): ConnectionList {
        return if (connectionService.existsConnectionByCompany(companyId = UUID.fromString(companyId))) {
            val page = connectionService.findAll(
                    companyId = UUID.fromString(companyId),
                    sort = sort,
                    filter = filter,
                    limit = limit,
                    offset = offset,
            )
            ConnectionList(
                    items = page.content.map {
                        Connection.fromEntity(it)
                    },
                    totalItems = page.totalElements.toInt(),
            )
        } else {
            ConnectionList(items = listOf(), totalItems = -1)
        }
    }
}
