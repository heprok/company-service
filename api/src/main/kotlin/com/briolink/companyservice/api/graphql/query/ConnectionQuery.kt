package com.briolink.companyservice.api.graphql.query

import com.briolink.companyservice.api.graphql.fromEntity
import com.briolink.companyservice.api.service.ConnectionService
import com.briolink.companyservice.api.types.Collaborator
import com.briolink.companyservice.api.types.Connection
import com.briolink.companyservice.api.types.ConnectionFilter
import com.briolink.companyservice.api.types.ConnectionList
import com.briolink.companyservice.api.types.ConnectionRole
import com.briolink.companyservice.api.types.ConnectionRoleType
import com.briolink.companyservice.api.types.ConnectionSort
import com.briolink.companyservice.api.types.ConnectionTabItemsCount
import com.briolink.companyservice.api.types.ServiceProvided
import com.briolink.companyservice.common.util.StringUtil
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsQuery
import com.netflix.graphql.dgs.InputArgument
import org.springframework.security.access.prepost.PreAuthorize
import java.util.UUID

@DgsComponent
class ConnectionQuery(
    private val connectionService: ConnectionService
) {
    @DgsQuery
    @PreAuthorize("isAuthenticated()")
    fun getConnections(
        @InputArgument("companyId") companyId: String,
        @InputArgument("filter") filter: ConnectionFilter?,
        @InputArgument("sort") sort: ConnectionSort?,
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
            val itemsCountByRole = connectionService.getRolesAndCountForCompany(UUID.fromString(companyId), filter = filter).map {
                ConnectionTabItemsCount(
                        id = it.key.toString(),
                        value = it.value,
                )
            }
            ConnectionList(
                    items = page.content.map { entity ->
                        Connection.fromEntity(entity).let {
                            if (it.seller!!.id == companyId) {
                                it.copy(buyer = it.seller, seller = it.buyer)
                            } else {
                                it
                            }
                        }
                    },
                    itemsCountByRole = itemsCountByRole,
                    totalItems = itemsCountByRole.sumOf { connectionTabItemsCount -> connectionTabItemsCount.value },
            )
        } else {
            ConnectionList(items = listOf(), itemsCountByRole = listOf(), totalItems = -1)
        }
    }

    @DgsQuery
    @PreAuthorize("isAuthenticated()")
    fun getConnectionsCount(
        @InputArgument("companyId") companyId: String,
        @InputArgument("filter") filter: ConnectionFilter?
    ): Int = connectionService.count(companyId = UUID.fromString(companyId), filter = filter).toInt()

    @DgsQuery
    @PreAuthorize("isAuthenticated()")
    fun getConnectionTabsRole(
        @InputArgument("companyId") companyId: String
    ): List<ConnectionRole> {
        return connectionService.getRolesForCompany(UUID.fromString(companyId)).map {
            ConnectionRole(
                    id = it.id.toString(),
                    name = it.name,
                    type = ConnectionRoleType.valueOf(it.type.name),
            )
        }
    }

    @DgsQuery
    @PreAuthorize("isAuthenticated()")
    fun getCollaborators(
        @InputArgument("companyId") companyId: String,
        @InputArgument("query") query: String,
    ): List<Collaborator> = connectionService.getCollaboratorsUsedForCompany(UUID.fromString(companyId), StringUtil.replaceNonWord(query))

    @DgsQuery
    @PreAuthorize("isAuthenticated()")
    fun getCollaboratorRoles(
        @InputArgument("companyId") companyId: String,
        @InputArgument("query") query: String,
    ): List<ConnectionRole> =
            connectionService.getConnectionRoleUsedForCompany(UUID.fromString(companyId), StringUtil.replaceNonWord(query))
                    .map { ConnectionRole.fromEntity(it) }


    @DgsQuery
    @PreAuthorize("isAuthenticated()")
    fun getServicesProvided(
        @InputArgument("companyId") companyId: String,
        @InputArgument("query") query: String,
    ): List<ServiceProvided> =
            connectionService.getServicesProvided(UUID.fromString(companyId), StringUtil.replaceNonWord(query))
                    .map { ServiceProvided(id = it.serviceId.toString(), name = it.serviceName) }

}
