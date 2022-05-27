package com.briolink.companyservice.updater.handler.expverification

import com.briolink.companyservice.common.jpa.enumeration.ExpVerificationStatusEnum
import com.briolink.companyservice.updater.handler.userjobposition.UserJobPositionHandlerService
import com.briolink.companyservice.updater.service.SyncService
import com.briolink.lib.event.IEventHandler
import com.briolink.lib.event.annotation.EventHandler
import com.briolink.lib.event.annotation.EventHandlers
import com.briolink.lib.sync.SyncEventHandler
import com.briolink.lib.sync.enumeration.ObjectSyncEnum
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

@EventHandler("ExpVerificationSyncEvent", "1.0")
@Transactional
class ExpVerificationSyncEventHandler(
    private val userJobPositionHandlerService: UserJobPositionHandlerService,
    syncService: SyncService,
) : SyncEventHandler<ExpVerificationSyncEvent>(ObjectSyncEnum.ExpVerification, syncService) {
    override fun handle(event: ExpVerificationSyncEvent) {
        val syncData = event.data
        if (!objectSyncStarted(syncData)) return
        try {
            val objectSync = syncData.objectSync!!
            when (objectSync.objectConfirmType) {
                ObjectConfirmType.WorkExperience -> {
                    userJobPositionHandlerService.updateStatus(
                        id = objectSync.objectConfirmId,
                        status = ExpVerificationStatusEnum.ofValue(objectSync.status.value),
                        verifiedBy = objectSync.actionBy
                    )
                    userJobPositionHandlerService.updateVerification(
                        id = objectSync.objectConfirmId,
                        verificationId = objectSync.id,
                        verifyByCompany = objectSync.userToConfirmIds.isEmpty()
                    )
                }
                else -> null
            }
        } catch (ex: Exception) {
            sendError(syncData, ex)
        }
        objectSyncCompleted(syncData)
    }
}
