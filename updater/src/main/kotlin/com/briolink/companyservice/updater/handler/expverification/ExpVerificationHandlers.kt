package com.briolink.companyservice.updater.handler.expverification

import com.briolink.companyservice.common.jpa.enumeration.ExpVerificationStatusEnum
import com.briolink.companyservice.updater.handler.userjobposition.UserJobPositionHandlerService
import com.briolink.lib.event.IEventHandler
import com.briolink.lib.event.annotation.EventHandler
import com.briolink.lib.event.annotation.EventHandlers
import org.springframework.transaction.annotation.Transactional

@EventHandlers(
    EventHandler("ExpVerificationCreatedEvent", "1.0"),
    EventHandler("ExpVerificationUpdatedEvent", "1.0")
)
@Transactional
class ExpVerificationEventHandler(
    private val userJobPositionHandlerService: UserJobPositionHandlerService
) : IEventHandler<ExpVerificationCreatedEvent> {
    override fun handle(event: ExpVerificationCreatedEvent) {
        val verification = event.data
        when (verification.objectConfirmType) {
            ObjectConfirmType.WorkExperience -> {
                userJobPositionHandlerService.updateStatus(
                    id = verification.objectConfirmId,
                    status = ExpVerificationStatusEnum.ofValue(verification.status.value),
                    verifiedBy = verification.actionBy
                )
                userJobPositionHandlerService.updateVerification(
                    id = verification.objectConfirmId,
                    verificationId = verification.id,
                    verifyByCompany = verification.userToConfirmIds.isEmpty()
                )
            }
            else -> null
        }
    }
}

@EventHandler("ExpVerificationChangedStatusEvent", "1.0")
@Transactional
class ExpVerificationChangedStatusEventHandler(
    private val userJobPositionHandlerService: UserJobPositionHandlerService
) : IEventHandler<ExpVerificationChangedStatusEvent> {
    override fun handle(event: ExpVerificationChangedStatusEvent) {
        when (event.data.objectConfirmType) {
            ObjectConfirmType.WorkExperience -> {
                userJobPositionHandlerService.updateStatus(
                    id = event.data.objectConfirmId,
                    status = ExpVerificationStatusEnum.ofValue(event.data.status.value),
                    verifiedBy = null
                )
                userJobPositionHandlerService.updateVerification(
                    id = event.data.objectConfirmId,
                    verificationId = null,
                    verifyByCompany = false
                )
            }
            else -> null
        }
    }
}

// TODO: Add sync events
