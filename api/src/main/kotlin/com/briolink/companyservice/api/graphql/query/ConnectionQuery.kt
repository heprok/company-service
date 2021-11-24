package com.briolink.companyservice.api.graphql.query

import com.briolink.companyservice.api.graphql.fromEntity
import com.briolink.companyservice.api.service.connection.ConnectionService
import com.briolink.companyservice.api.service.connection.dto.FiltersDto
import com.briolink.companyservice.api.service.connection.dto.SortDto
import com.briolink.companyservice.api.types.Connection
import com.briolink.companyservice.api.types.ConnectionList
import com.briolink.companyservice.api.types.ConnectionListOptions
import com.briolink.companyservice.api.types.ConnectionTab
import com.briolink.companyservice.api.types.ConnectionTabItemsCount
import com.briolink.companyservice.api.types.IdNameItem
import com.briolink.companyservice.common.jpa.enumration.ConnectionStatusEnum
import com.briolink.companyservice.common.jpa.read.entity.ConnectionReadEntity
import com.briolink.companyservice.common.jpa.read.repository.ConnectionReadRepository
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsQuery
import com.netflix.graphql.dgs.InputArgument
import graphql.schema.DataFetchingEnvironment
import org.springframework.security.access.prepost.PreAuthorize
import java.util.UUID

@DgsComponent
class ConnectionQuery(
    private val connectionService: ConnectionService,
    private val connectionReadRepository: ConnectionReadRepository
) {
    @DgsQuery
    @PreAuthorize("isAuthenticated()")
    fun getCompanyConnections(
        @InputArgument("companyId") companyId: String,
        @InputArgument("options") options: ConnectionListOptions,
        dfe: DataFetchingEnvironment
    ): ConnectionList {
        return if (connectionService.existsConnectionByCompany(companyId = UUID.fromString(companyId))) {
            val filterDto = FiltersDto(
                collaboratorIds = options.filter?.collaboratorIds?.map { UUID.fromString(it) },
                collaborationStartDate = options.filter?.collaborationDates?.start,
                collaborationEndDate = options.filter?.collaborationDates?.end,
                industryIds = options.filter?.industryIds?.map { UUID.fromString(it) },
                serviceIds = options.filter?.serviceIds?.map { UUID.fromString(it) },
                location = options.filter?.location,
                status = options.filter?.status?.map { ConnectionStatusEnum.valueOf(it.name) },
            )
            val sortDto = options.sort?.let {
                SortDto(
                    key = SortDto.ConnectionSortKeys.valueOf(it.key.name),
                    direction = SortDto.SortDirection.valueOf(it.direction.name),
                )
            } ?: SortDto(
                key = SortDto.ConnectionSortKeys.id,
                direction = SortDto.SortDirection.ASC,
            )
            val tabsCount = connectionService
                .tabs(UUID.fromString(companyId), true, filterDto)

            val result: List<ConnectionReadEntity>
            val tabId = options.tabId

            result = connectionService.getList(
                companyId = UUID.fromString(companyId),
                tabId = tabId,
                filters = filterDto,
                sort = sortDto,
                offset = options.offset,
                limit = options.limit,
            )

            ConnectionList(
                items = result.map { Connection.fromEntity(it) },
                itemsCountByTab = tabsCount.map { ConnectionTabItemsCount(id = it.id, value = it.count ?: 0) },
                totalItems = tabsCount.sumOf { it.count ?: 0 },
            )
        } else {
            ConnectionList(items = listOf(), itemsCountByTab = listOf(), totalItems = -1)
        }
    }

    @DgsQuery
    @PreAuthorize("isAuthenticated()")
    fun getCompanyConnectionTabs(
        @InputArgument("companyId") companyId: String
    ): List<ConnectionTab> {
        return connectionService
            .tabs(UUID.fromString(companyId), false)
            .map { ConnectionTab(id = it.id, name = it.name) }
    }

    @DgsQuery
    @PreAuthorize("isAuthenticated()")
    fun getCompanyConnectionCollaborators(
        @InputArgument("companyId") companyId: String,
        @InputArgument("query") query: String?,
    ): List<IdNameItem> =
        connectionReadRepository.getCollaboratorsByCompanyId(companyId, query = query?.ifBlank { null })
            .map { IdNameItem(id = it.id.toString(), name = it.name) }

    @DgsQuery
    @PreAuthorize("isAuthenticated()")
    fun getCompanyConnectionServices(
        @InputArgument("companyId") companyId: String,
        @InputArgument("query") query: String?,
    ): List<IdNameItem> =
        connectionReadRepository.getConnectionServicesByCompanyId(companyId, query = query?.ifBlank { null })
            .map { IdNameItem(id = it.id.toString(), name = it.name) }
}
