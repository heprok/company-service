package com.briolink.companyservice.api.service

import com.briolink.companyservice.api.exception.UnavailableException
import com.briolink.companyservice.api.service.employee.EmployeeService
import com.briolink.companyservice.api.service.expverification.ExpVerificationService
import com.briolink.companyservice.api.service.expverification.dto.VerificationConfirmAction
import com.briolink.companyservice.api.util.SecurityUtil
import com.briolink.companyservice.common.config.AppEndpointsProperties
import com.briolink.companyservice.common.jpa.enumeration.ExpVerificationStatusEnum
import com.briolink.companyservice.common.jpa.read.entity.UserJobPositionReadEntity
import com.briolink.companyservice.common.jpa.read.repository.EmployeeReadRepository
import com.briolink.companyservice.common.jpa.read.repository.UserJobPositionReadRepository
import com.netflix.graphql.dgs.client.MonoGraphQLClient
import mu.KLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.reactive.function.client.WebClient
import java.time.LocalDate
import java.util.UUID
import javax.persistence.EntityNotFoundException

@Service
@Transactional
class UserJobPositionService(
    private val userJobPositionReadRepository: UserJobPositionReadRepository,
    private val expVerificationService: ExpVerificationService,
    private val appEndpointsProperties: AppEndpointsProperties,
    private val employeeReadRepository: EmployeeReadRepository,
    private val employeeService: EmployeeService
) {

    private fun getById(id: UUID): UserJobPositionReadEntity =
        userJobPositionReadRepository.findById(id)
            .orElseThrow { throw EntityNotFoundException("UserJobPosition with id $id not found") }

    private fun existCurrentUserJobPosition(
        companyId: UUID,
        userId: UUID
    ): Boolean = userJobPositionReadRepository.existsByCompanyIdAndUserIdAndStatusAndEndDateIsNull(
        companyId = companyId,
        userId = userId,
        status = ExpVerificationStatusEnum.Confirmed.value,
    )

    fun confirmWorkExpVerification(id: UUID, action: VerificationConfirmAction) {
        val userJobPosition = getById(id)

        if (userJobPosition.verificationId == null) throw RuntimeException("UserJobPosition with id $id has no verificationId")

        if (expVerificationService.confirmWorkExpVerification(userJobPosition.verificationId!!, action)) {

            userJobPosition.status = when (action) {
                VerificationConfirmAction.Confirm -> {
                    userJobPosition.data.verifiedBy = SecurityUtil.currentUserAccountId
                    ExpVerificationStatusEnum.Confirmed
                }
                VerificationConfirmAction.Reject -> ExpVerificationStatusEnum.Rejected
            }
        }

        userJobPositionReadRepository.save(userJobPosition)
    }

    fun deleteById(id: UUID) {
        return deleteById(listOf(id))
    }

    fun deleteById(ids: List<UUID>) {
        val userJobPosition = getById(ids.first())
        userJobPositionReadRepository.deleteById(ids)
        if (!existCurrentUserJobPosition(userJobPosition.companyId, userJobPosition.companyId)) {
            employeeService.deleteEmployee(userJobPosition.companyId, userJobPosition.userId)
        }
    }

    fun setFormerJobPosition(id: UUID) {
        return setFormerJobPosition(listOf(id))
    }

    fun setFormerJobPosition(ids: List<UUID>) {
        val userJobPosition = getById(ids.first())

        ids.forEach { sendToUserSetEndDate(it, LocalDate.now()) }

        userJobPositionReadRepository.setFormerEmployee(ids)
        if (!existCurrentUserJobPosition(userJobPosition.companyId, userJobPosition.userId)) {
            employeeService.setFormerEmployee(userJobPosition.companyId, userJobPosition.userId)
        }
    }

    fun sendToUserSetEndDate(userJobPositionId: UUID, endDate: LocalDate? = LocalDate.now()): Boolean {
        val client = createGQLClient()

        val variables = mapOf(
            "id" to userJobPositionId.toString(),
            "endDate" to endDate.toString()
        )

        val result = client.reactiveExecuteQuery(setUserJobPositionEndDateMutation, variables).block()
            ?: throw UnavailableException(serviceName = SERVICE_NAME)

        val userErrors = result.extractValue<List<HashMap<String, String>>>("setUserJobPositionEndDate.userErrors")

        if (userErrors.isNotEmpty()) {

            val ex =
                UnavailableException(userErrors.first()["message"] ?: "Unknown error", serviceName = ExpVerificationService.SERVICE_NAME)
            logger.error("Error while set end date userJobPosition: $userErrors", ex)

            throw ex
        }

        return true
    }

    private val setUserJobPositionEndDateMutation = """
        mutation setUserJobPositionEndDate(${'$'}id: ID!, ${'$'}endDate: Date ) {
          setUserJobPositionEndDate(id: ${'$'}id, endDate: ${'$'}endDate) {
            userErrors{
                message
            }
          }
        }
    """

    private fun createGQLClient(token: String? = null) = MonoGraphQLClient.createWithWebClient(
        WebClient.create(appEndpointsProperties.user),
        headersConsumer = { headers ->
            token?.also { headers.add("Authorization", token) }
        },
    )

    companion object {
        const val SERVICE_NAME = "USER"
        private val logger = KLogging().logger
    }
}
