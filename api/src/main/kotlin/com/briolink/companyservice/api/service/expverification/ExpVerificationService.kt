package com.briolink.companyservice.api.service.expverification

import com.briolink.companyservice.api.service.expverification.dto.ObjectConfirmType
import com.briolink.companyservice.api.service.expverification.dto.VerificationConfirmAction
import com.briolink.companyservice.common.config.AppEndpointsProperties
import com.briolink.companyservice.common.jpa.enumeration.ExpVerificationStatusEnum
import com.briolink.lib.common.exception.UnavailableException
import com.briolink.lib.common.utils.BlSecurityUtils
import com.netflix.graphql.dgs.client.MonoGraphQLClient
import mu.KLogging
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import java.util.UUID

@Service
class ExpVerificationService(
    private val appEndpointsProperties: AppEndpointsProperties
) {

    private val confirmWorkExpVerificationByCompanyMutation = """
        mutation confirmWorkExpVerificationByCompany(${'$'}byUserId: ID!, ${'$'}id: ID!, ${'$'}action: VerificationConfirmAction! ) {
          confirmWorkExpVerificationByCompany(byUserId: ${'$'}byUserId, id: ${'$'}id, action: ${'$'}action) {
            success
            userErrors{
                message
            }
          }
        }
    """

    private val resetVerificationMutation = """
        mutation resetVerification(${'$'}objectKey: ObjectKey!, ${'$'}overrideStatus: VerificationStatus) {
          resetVerification(objectKey: ${'$'}objectKey, overrideStatus: ${'$'}overrideStatus) {
            success
            status
          }
        }
    """

    fun resetVerification(
        objectId: UUID,
        objectConfirmType: ObjectConfirmType,
        overrideStatus: ExpVerificationStatusEnum? = null
    ): ExpVerificationStatusEnum {
        val client = createGQLClient()

        val variables = mapOf(
            "objectKey" to mapOf(
                "type" to objectConfirmType.name,
                "id" to objectId.toString()
            ),
            "overrideStatus" to overrideStatus?.name.toString()
        )

        val result = client.reactiveExecuteQuery(resetVerificationMutation, variables).block()
            ?: throw UnavailableException(serviceName = SERVICE_NAME)

        val success = result.extractValue<Boolean>("resetVerification.success")
        if (!success) throw UnavailableException(serviceName = SERVICE_NAME)

        val status = result.extractValue<String>("resetVerification.status")

        return ExpVerificationStatusEnum.valueOf(status)
    }

    fun confirmWorkExpVerification(verificationId: UUID, action: VerificationConfirmAction): Boolean {
        val client = createGQLClient()

        val variables = mapOf(
            "byUserId" to BlSecurityUtils.currentUserId.toString(),
            "id" to verificationId.toString(),
            "action" to action.name
        )

        val result = client.reactiveExecuteQuery(confirmWorkExpVerificationByCompanyMutation, variables).block()
            ?: throw UnavailableException(serviceName = SERVICE_NAME)

        val userErrors = result.extractValue<List<HashMap<String, String>>>("confirmWorkExpVerificationByCompany.userErrors")

        if (userErrors.isNotEmpty()) {

            val ex = UnavailableException(serviceName = SERVICE_NAME)
            logger.error("Error while confirm verification: $userErrors", ex)

            throw ex
        }

        val success = result.extractValue<Boolean>("confirmWorkExpVerificationByCompany.success")

        return success
    }

    private fun createGQLClient(token: String? = null) = MonoGraphQLClient.createWithWebClient(
        WebClient.create(appEndpointsProperties.expverification),
        headersConsumer = { headers ->
            token?.also { headers.add("Authorization", token) }
        },
    )

    companion object {
        const val SERVICE_NAME = "EXP_VERIFICATION"
        private val logger = KLogging().logger
    }
}
