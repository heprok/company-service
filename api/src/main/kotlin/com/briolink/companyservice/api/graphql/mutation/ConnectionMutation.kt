package com.briolink.companyservice.api.graphql.mutation

import com.briolink.companyservice.api.service.connection.ConnectionService
import com.briolink.companyservice.api.service.connection.dto.ConnectionRequestActionEnum
import com.briolink.companyservice.api.types.BaseResult
import com.briolink.companyservice.api.types.ConnectionRequestInput
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsMutation
import com.netflix.graphql.dgs.InputArgument
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.RequestHeader
import java.util.UUID

@DgsComponent
class ConnectionMutation(
    private val connectionService: ConnectionService,
) {

    @DgsMutation
    @PreAuthorize("isAuthenticated()")
    fun connectionRequest(@InputArgument input: ConnectionRequestInput, @RequestHeader("Authorization") authorization: String): BaseResult {
        return BaseResult(
            success = connectionService.connectionRequest(
                authorization,
                UUID.fromString(input.companyId),
                ConnectionRequestActionEnum.valueOf(input.action.name),
                input.message
            ),
            userErrors = listOf()
        )
    }
}
