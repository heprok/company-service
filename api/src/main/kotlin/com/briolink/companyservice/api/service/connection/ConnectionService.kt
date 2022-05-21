package com.briolink.companyservice.api.service.connection

import com.briolink.companyservice.api.service.connection.dto.ConnectionRequestActionEnum
import com.briolink.companyservice.common.config.AppEndpointsProperties
import com.briolink.lib.common.exception.UnavailableException
import com.netflix.graphql.dgs.client.MonoGraphQLClient
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.reactive.function.client.WebClient
import java.util.UUID

@Service
@Transactional
class ConnectionService(
    private val appEndpointsProperties: AppEndpointsProperties,
) {
    private val getConnectionStateQuery =
        """
        query getConnectionState(${'$'}objectKey: ObjectKey!) {
          getConnectionState(objectKey: ${'$'}objectKey) {
            status
          }
        }
        """

    private val connectionRequestMutation =
        """
        mutation connectionRequest(${'$'}input: ConnectionRequestInput!) {
          connectionRequest(input: ${'$'}input) {
            success
          }
        }
        """

    fun getConnectionStatus(jwtAuthToken: String, companyId: UUID): String {
        val client = createGQLClient(jwtAuthToken)

        val vars = mutableMapOf(
            "objectKey" to mutableMapOf(
                "id" to companyId.toString(),
                "type" to "Company"
            )
        )

        val result = client.reactiveExecuteQuery(getConnectionStateQuery, vars).block()
            ?: throw UnavailableException(serviceName = SERVICE_NAME)

        return try {
            val status = result.extractValue<String>("getConnectionState.status")
            status
        } catch (e: Exception) {
            throw UnavailableException(serviceName = SERVICE_NAME)
        }
    }

    fun connectionRequest(
        jwtAuthToken: String,
        companyId: UUID,
        action: ConnectionRequestActionEnum,
        message: String? = null
    ): Boolean {
        val client = createGQLClient(jwtAuthToken)

        val vars = mutableMapOf(
            "input" to mutableMapOf(
                "objectKey" to mutableMapOf(
                    "id" to companyId.toString(),
                    "type" to "Company"
                ),
                "action" to action.name
            )
        )
        if (message != null) vars["input"]!!["message"] = message

        val result = client.reactiveExecuteQuery(connectionRequestMutation, vars).block()
            ?: throw UnavailableException(serviceName = SERVICE_NAME)

        return try {
            val success = result.extractValue<Boolean>("connectionRequest.success")
            success
        } catch (e: Exception) {
            throw UnavailableException(serviceName = SERVICE_NAME)
        }
    }

    private fun createGQLClient(token: String) = MonoGraphQLClient.createWithWebClient(
        WebClient.create(appEndpointsProperties.connection),
        headersConsumer = {
            it.add("Authorization", token)
        },
    )

    companion object {
        const val SERVICE_NAME = "CONNECTION"
    }
}
