package com.briolink.companyservice.updater.handler.project

import com.briolink.companyservice.common.jpa.enumeration.CompanyRoleTypeEnum
import com.briolink.companyservice.common.jpa.read.entity.ProjectReadEntity
import com.briolink.companyservice.common.jpa.read.repository.ProjectReadRepository
import com.briolink.companyservice.common.jpa.read.repository.service.ServiceReadRepository
import com.briolink.companyservice.updater.RefreshStatisticByCompanyId
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID
import javax.persistence.EntityNotFoundException

@Transactional
@Service
class ProjectHandlerService(
    private val projectReadRepository: ProjectReadRepository,
    private val serviceReadRepository: ServiceReadRepository,
    private val applicationEventPublisher: ApplicationEventPublisher
) {

    fun createOrUpdate(domain: ProjectEventData): ProjectReadEntity {
        val projectService = domain.service

        val serviceRead = serviceReadRepository.findById(projectService.serviceId!!)
            .orElseThrow { throw EntityNotFoundException("Service not found") }

        projectReadRepository.findById(domain.id).orElse(ProjectReadEntity(domain.id)).apply {
            participantFromCompanyId = domain.participantFrom.companyId!!
            participantFromUserId = domain.participantFrom.userId!!
            participantFromRoleType = CompanyRoleTypeEnum.ofValue(domain.participantFrom.companyRole?.type?.value!!)
            participantFromRoleName = domain.participantFrom.companyRole.name

            participantToCompanyId = domain.participantTo.companyId!!
            participantToUserId = domain.participantTo.userId!!
            participantToRoleType = CompanyRoleTypeEnum.ofValue(domain.participantTo.companyRole?.type?.value!!)
            participantToRoleName = domain.participantTo.companyRole.name

            hiddenCompanyIds = mutableListOf<UUID>().apply {
                if (domain.participantFrom.companyHidden == true) add(domain.participantFrom.companyId)
                if (domain.participantTo.companyHidden == true) add(domain.participantTo.companyId)
            }.toTypedArray()

            deletedCompanyIds = mutableListOf<UUID>().apply {
                if (domain.participantFrom.companyDeleted == true) add(domain.participantFrom.companyId)
                if (domain.participantTo.companyDeleted == true) add(domain.participantTo.companyId)
            }.toTypedArray()

            acceptedCompanyIds = mutableListOf<UUID>().apply {
                if (domain.participantFrom.companyAccepted == true) add(domain.participantFrom.companyId)
                if (domain.participantTo.companyAccepted == true) add(domain.participantTo.companyId)
            }.toTypedArray()

            serviceId = serviceRead.id

            data = ProjectReadEntity.Data(
                participantTo = ProjectReadEntity.Participant(
                    companyId = domain.participantTo.companyId,
                    userId = domain.participantTo.userId,
                    role = ProjectReadEntity.ProjectRole(
                        id = domain.participantTo.companyRole.id,
                        name = domain.participantTo.companyRole.name,
                        type = CompanyRoleTypeEnum.ofValue(domain.participantTo.companyRole.type.value)
                    )
                ),
                participantFrom = ProjectReadEntity.Participant(
                    companyId = domain.participantFrom.companyId,
                    userId = domain.participantFrom.userId,
                    role = ProjectReadEntity.ProjectRole(
                        id = domain.participantFrom.companyRole.id,
                        name = domain.participantFrom.companyRole.name,
                        type = CompanyRoleTypeEnum.ofValue(domain.participantFrom.companyRole.type.value)
                    )
                ),
                projectService = ProjectReadEntity.ProjectService(
                    service = ProjectReadEntity.Service(
                        id = serviceRead.id,
                        name = projectService.serviceName,
                        slug = if (serviceRead.deleted || serviceRead.hidden) "-1" else serviceRead.data.slug,
                    ),
                    startDate = projectService.startDate!!,
                    endDate = projectService.endDate
                )

            )

            return projectReadRepository.save(this)
        }
    }

    fun delete(projectId: UUID) =
        projectReadRepository.deleteByIds(projectId)

    fun setHidden(projectId: UUID, companyId: UUID, hidden: Boolean) =
        projectReadRepository.changeVisibilityById(projectId, companyId, hidden).also {
            if (it > 0) refreshStatistic(companyId, false)
        }

    fun softDeleteCompanyById(projectId: UUID, companyId: UUID) =
        projectReadRepository.softDeleteCompanyIdById(projectId, companyId).also {
            if (it > 0) refreshStatistic(companyId, true)
        }

    fun refreshStatistic(listCompanyId: List<UUID>, refreshCollaborating: Boolean = false) {
        listCompanyId.forEach {
            refreshStatistic(it, refreshCollaborating)
        }
    }

    fun refreshStatistic(companyId: UUID, refreshCollaborating: Boolean = false) {
        applicationEventPublisher.publishEvent(RefreshStatisticByCompanyId(companyId, refreshCollaborating))
    }
}
