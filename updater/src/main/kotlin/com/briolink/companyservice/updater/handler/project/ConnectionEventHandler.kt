package com.briolink.companyservice.updater.handler.project

import com.briolink.companyservice.updater.RefreshStatisticByCompanyId
import com.briolink.companyservice.updater.service.SyncService
import com.briolink.lib.event.IEventHandler
import com.briolink.lib.event.annotation.EventHandler
import com.briolink.lib.event.annotation.EventHandlers
import com.briolink.lib.sync.SyncEventHandler
import com.briolink.lib.sync.enumeration.ObjectSyncEnum
import org.springframework.context.ApplicationEventPublisher

@EventHandlers(
    EventHandler("ProjectCreatedEvent", "1.0"),
    EventHandler("ProjectUpdatedEvent", "1.0"),
)
class ProjectEventHandler(
    private val projectHandlerService: ProjectHandlerService,
    private val applicationEventPublisher: ApplicationEventPublisher
) : IEventHandler<ProjectCreatedEvent> {
    override fun handle(event: ProjectCreatedEvent) {
        val connection = event.data
        if (connection.status != ProjectStatus.Rejected) {
            projectHandlerService.createOrUpdate(connection).also {
                if (connection.status == ProjectStatus.Verified) {
                    applicationEventPublisher.publishEvent(
                        RefreshStatisticByCompanyId(
                            it.participantToCompanyId,
                            false
                        )
                    )
                    applicationEventPublisher.publishEvent(
                        RefreshStatisticByCompanyId(
                            it.participantFromCompanyId,
                            false
                        )
                    )
                }
            }
        } else if (connection.status == ProjectStatus.Rejected) {
            projectHandlerService.delete(connection.id)
        }
    }
}

@EventHandler("ProjectSyncEvent", "1.0")
class ProjectSyncEventHandler(
    private val projectHandlerService: ProjectHandlerService,
    private val applicationEventPublisher: ApplicationEventPublisher,
    syncService: SyncService,
) : SyncEventHandler<ProjectSyncEvent>(ObjectSyncEnum.Connection, syncService) {
    override fun handle(event: ProjectSyncEvent) {
        val syncData = event.data
        if (!objectSyncStarted(syncData)) return
        try {
            val project = syncData.objectSync!!
            if (project.status != ProjectStatus.Rejected) {
                projectHandlerService.createOrUpdate(project).also {
                    if (project.status == ProjectStatus.Verified) {
                        applicationEventPublisher.publishEvent(
                            RefreshStatisticByCompanyId(
                                it.participantToCompanyId,
                                false
                            )
                        )
                        applicationEventPublisher.publishEvent(
                            RefreshStatisticByCompanyId(
                                it.participantFromCompanyId,
                                false
                            )
                        )
                    }
                }
            } else if (project.status == ProjectStatus.Rejected) {
                projectHandlerService.delete(project.id)
            }
        } catch (ex: Exception) {
            sendError(syncData, ex)
        }
        objectSyncCompleted(syncData)
    }
}
