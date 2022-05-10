package com.briolink.companyservice.updater.handler.project

import com.briolink.companyservice.common.jpa.read.repository.CompanyReadRepository
import com.briolink.companyservice.updater.service.SyncService
import com.briolink.lib.event.IEventHandler
import com.briolink.lib.event.annotation.EventHandler
import com.briolink.lib.event.annotation.EventHandlers
import com.briolink.lib.sync.SyncEventHandler
import com.briolink.lib.sync.enumeration.ObjectSyncEnum

@EventHandlers(
    EventHandler("ProjectCreatedEvent", "1.0"),
    EventHandler("ProjectUpdatedEvent", "1.0"),
)
class ProjectEventHandler(
    private val projectHandlerService: ProjectHandlerService,
) : IEventHandler<ProjectCreatedEvent> {
    override fun handle(event: ProjectCreatedEvent) {
        val project = event.data

        val updateStatistic = if (project.status == ProjectStatus.Verified) {
            projectHandlerService.createOrUpdate(project)
            true
        } else projectHandlerService.delete(project.id) > 0

        if (updateStatistic) {
            project.participantFrom.companyId?.let { projectHandlerService.refreshStatistic(it) }
            project.participantTo.companyId?.let { projectHandlerService.refreshStatistic(it) }
        }
    }
}

@EventHandler("ProjectVisibilityUpdatedEvent", "1.0")
class ProjectVisibilityUpdatedEventHandler(
    private val projectHandlerService: ProjectHandlerService,
) : IEventHandler<ProjectVisibilityUpdatedEvent> {
    override fun handle(event: ProjectVisibilityUpdatedEvent) {
        if (event.data.objectType == ProjectObjectType.Company) {
            projectHandlerService.setHidden(event.data.id, event.data.participantObjectId, event.data.hidden)
        }
    }
}

@EventHandler("ProjectDeletedEvent", "1.0")
class ProjectDeleteEventHandler(
    private val projectHandlerService: ProjectHandlerService,
) : IEventHandler<ProjectDeletedEvent> {
    override fun handle(event: ProjectDeletedEvent) {
        if (event.data.objectType == ProjectObjectType.Company) {
            projectHandlerService.softDeleteCompanyById(event.data.id, event.data.participantObjectId)
        }
    }
}

@EventHandler("ProjectSyncEvent", "1.0")
class ProjectSyncEventHandler(
    private val projectHandlerService: ProjectHandlerService,
    private val companyReadRepository: CompanyReadRepository,
    syncService: SyncService,
) : SyncEventHandler<ProjectSyncEvent>(ObjectSyncEnum.Project, syncService) {
    override fun handle(event: ProjectSyncEvent) {
        val syncData = event.data
        if (!objectSyncStarted(syncData)) return
        try {
            val project = syncData.objectSync!!

            if (project.status == ProjectStatus.Verified) {
                projectHandlerService.createOrUpdate(project)
            } else projectHandlerService.delete(project.id)
        } catch (ex: Exception) {
            sendError(syncData, ex)
        }
        if (objectSyncCompleted(syncData)) {
            projectHandlerService.refreshStatistic(companyReadRepository.getAllCompanyUUID())
        }
    }
}
