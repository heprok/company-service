package com.briolink.companyservice.api.graphql.query

import com.briolink.companyservice.api.service.connection.ConnectionService
import com.briolink.companyservice.api.types.ConnectionState
import com.briolink.companyservice.api.types.ConnectionStatus
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsQuery
import com.netflix.graphql.dgs.InputArgument
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.RequestHeader
import java.util.UUID

@DgsComponent
class ConnectionQuery(
    private val connectionService: ConnectionService,
) {
    @DgsQuery
    @PreAuthorize("isAuthenticated()")
    fun getConnectionState(@InputArgument companyId: String, @RequestHeader("Authorization") authorization: String): ConnectionState {
        val status = connectionService.getConnectionStatus(
            authorization,
            UUID.fromString(companyId)
        )

        return ConnectionState(status = ConnectionStatus.valueOf(status))
    }
}
