package com.briolink.companyservice.updater.handler.project

import com.briolink.companyservice.common.jpa.enumeration.CompanyRoleTypeEnum
import com.briolink.companyservice.common.jpa.enumeration.ConnectionStatusEnum
import com.briolink.companyservice.common.jpa.read.entity.CompanyReadEntity
import com.briolink.companyservice.common.jpa.read.entity.ConnectionReadEntity
import com.briolink.companyservice.common.jpa.read.entity.ConnectionServiceReadEntity
import com.briolink.companyservice.common.jpa.read.entity.UserReadEntity
import com.briolink.companyservice.common.jpa.read.repository.CompanyReadRepository
import com.briolink.companyservice.common.jpa.read.repository.ConnectionReadRepository
import com.briolink.companyservice.common.jpa.read.repository.ConnectionServiceReadRepository
import com.briolink.companyservice.common.jpa.read.repository.UserReadRepository
import com.briolink.companyservice.common.jpa.read.repository.service.ServiceReadRepository
import com.briolink.lib.permission.enumeration.AccessObjectTypeEnum
import com.briolink.lib.permission.enumeration.PermissionRightEnum
import com.briolink.lib.permission.service.PermissionService
import com.vladmihalcea.hibernate.type.range.Range
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID
import java.util.stream.Collectors

@Transactional
@Service
class ProjectHandlerService(
    private val connectionReadRepository: ConnectionReadRepository,
    private val companyReadRepository: CompanyReadRepository,
    private val connectionServiceReadRepository: ConnectionServiceReadRepository,
    private val serviceReadRepository: ServiceReadRepository,
    private val userReadRepository: UserReadRepository,
    private val permissionService: PermissionService
) {
    fun createOrUpdate(project: ProjectEventData): ConnectionReadEntity {
        val participantUsers = userReadRepository.findByIdIsIn(
            mutableListOf(project.participantFrom.userId, project.participantTo.userId),
        ).stream().collect(Collectors.toMap(UserReadEntity::id) { v -> v })

        val participantCompanies = companyReadRepository.findByIdIsIn(
            mutableListOf(project.participantFrom.companyId, project.participantTo.companyId),
        ).stream().collect(Collectors.toMap(CompanyReadEntity::id) { v -> v })

        val buyerCompany: CompanyReadEntity
        val sellerCompany: CompanyReadEntity
        val participantBuyer: ProjectParticipant
        if (project.participantFrom.companyRole.type == ProjectCompanyRoleType.Buyer) {
            buyerCompany = participantCompanies[project.participantFrom.companyId]!!
            sellerCompany = participantCompanies[project.participantTo.companyId]!!
            participantBuyer = project.participantFrom
        } else {
            buyerCompany = participantCompanies[project.participantTo.companyId]!!
            sellerCompany = participantCompanies[project.participantFrom.companyId]!!
            participantBuyer = project.participantTo
        }
        val industry = buyerCompany.data.industry

        val serviceMinDate = project.services.minOf { it.startDate.value }
        var serviceMaxDate = project.services.maxOfOrNull { it.endDate?.value ?: -1 }

        if (serviceMaxDate == -1) {
            serviceMaxDate = null
        }

        connectionReadRepository.findById(project.id).orElse(ConnectionReadEntity(project.id)).apply {
            participantFromCompanyId = project.participantFrom.companyId
            participantFromUserId = project.participantFrom.userId
            participantFromRoleId = project.participantFrom.companyRole.id
            participantFromRoleName = project.participantFrom.companyRole.name
            participantFromRoleType = CompanyRoleTypeEnum.fromInt(project.participantFrom.companyRole.type.value)
            participantToCompanyId = project.participantTo.companyId
            participantToUserId = project.participantTo.userId
            participantToRoleId = project.participantTo.companyRole.id
            participantToRoleName = project.participantTo.companyRole.name
            participantToRoleType = CompanyRoleTypeEnum.fromInt(project.participantTo.companyRole.type.value)
            serviceIds = project.services.map { service -> service.id }.toList()
            dates = if (serviceMaxDate == null) Range.closedInfinite(serviceMinDate) else Range.closed(
                serviceMinDate,
                serviceMaxDate
            )
            status = ConnectionStatusEnum.fromInt(project.status.value)
            countryId = buyerCompany.data.location?.country?.id
            stateId = buyerCompany.data.location?.state?.id
            cityId = buyerCompany.data.location?.city?.id
            deletedCompanyIds = listOf()
            hiddenCompanyIds = mutableListOf<UUID>().apply {
                if (!permissionService.isHavePermission(
                        userId = project.participantFrom.userId,
                        accessObjectId = project.participantFrom.companyId,
                        accessObjectType = AccessObjectTypeEnum.Company,
                        permissionRight = PermissionRightEnum.IsCanCreateProject,
                    )
                )
                    add(project.participantFrom.companyId)

                if (!permissionService.isHavePermission(
                        userId = project.participantTo.userId,
                        accessObjectId = project.participantTo.companyId,
                        accessObjectType = AccessObjectTypeEnum.Company,
                        permissionRight = PermissionRightEnum.IsCanCreateProject,
                    )
                )
                    add(project.participantTo.companyId)
            }
            created = project.created
            companyIndustryId = industry?.id
            this.data = ConnectionReadEntity.Data(
                participantFrom = ConnectionReadEntity.Participant(
                    user = ConnectionReadEntity.User(
                        id = participantUsers[project.participantFrom.userId]!!.id,
                        slug = participantUsers[project.participantFrom.userId]!!.data.slug,
                        image = participantUsers[project.participantFrom.userId]!!.data.image,
                        firstName = participantUsers[project.participantFrom.userId]!!.data.firstName,
                        lastName = participantUsers[project.participantFrom.userId]!!.data.lastName,
                    ),
                    userJobPositionTitle = project.participantFrom.userJobPositionTitle,
                    company = ConnectionReadEntity.Company(
                        id = project.participantFrom.companyId,
                        slug = participantCompanies[project.participantFrom.companyId]!!.slug,
                        name = participantCompanies[project.participantFrom.companyId]!!.name,
                        logo = participantCompanies[project.participantFrom.companyId]!!.data.logo,
                        occupation = participantCompanies[project.participantFrom.companyId]!!.data.occupation?.name,
                    ),
                    companyRole = ConnectionReadEntity.CompanyRole(
                        id = project.participantFrom.companyRole.id,
                        name = project.participantFrom.companyRole.name,
                        type = CompanyRoleTypeEnum.valueOf(project.participantFrom.companyRole.type.name),
                    ),
                ),
                participantTo = project.participantTo.let {
                    ConnectionReadEntity.Participant(
                        user = ConnectionReadEntity.User(
                            id = participantUsers[project.participantTo.userId]!!.id,
                            slug = participantUsers[project.participantTo.userId]!!.data.slug,
                            image = participantUsers[project.participantTo.userId]!!.data.image,
                            firstName = participantUsers[project.participantTo.userId]!!.data.firstName,
                            lastName = participantUsers[project.participantTo.userId]!!.data.lastName,
                        ),
                        userJobPositionTitle = it.userJobPositionTitle,
                        company = ConnectionReadEntity.Company(
                            id = project.participantTo.companyId,
                            slug = participantCompanies[project.participantTo.companyId]!!.slug,
                            name = participantCompanies[project.participantTo.companyId]!!.name,
                            logo = participantCompanies[project.participantTo.companyId]!!.data.logo,
                            occupation = participantCompanies[project.participantTo.companyId]!!.data.occupation?.name,
                        ),
                        companyRole = it.companyRole.let { role ->
                            ConnectionReadEntity.CompanyRole(
                                id = role.id,
                                name = role.name,
                                type = CompanyRoleTypeEnum.valueOf(role.type.name),
                            )
                        },
                    )
                },
                services = ArrayList(
                    project.services.map {
                        ConnectionReadEntity.Service(
                            id = it.id,
                            serviceId = it.serviceId,
                            serviceName = it.serviceName,
                            startDate = it.startDate,
                            endDate = it.endDate,
                            slug = if (it.serviceId != null) serviceReadRepository.getSlugOrNullByServiceIdAndNotHiddenAndNotDeleted(
                                it.serviceId
                            )
                                ?: "-1" else "-1"
                        )
                    },
                ),
                industry = industry?.name,
            )

            project.services.forEach {
                val s = connectionServiceReadRepository.findById(it.id).orElse(ConnectionServiceReadEntity())

                if (s.id == null) s.id = it.id
                s.companyId = sellerCompany.id
                s.collaboratingCompanyId = buyerCompany.id
                s.serviceId = it.serviceId
                s.connectionId = project.id
                s.name = it.serviceName
                s.status = status
                s.hidden = hiddenCompanyIds.contains(sellerCompany.id)
                s.data = ConnectionServiceReadEntity.Data(
                    company = ConnectionServiceReadEntity.Company(
                        id = buyerCompany.id,
                        name = buyerCompany.name,
                        logo = buyerCompany.data.logo,
                        location = buyerCompany.data.location?.toString(),
                        industryName = buyerCompany.data.industry?.name,
                        slug = buyerCompany.slug
                    ),
                    roleName = participantBuyer.companyRole.name,
                    periodUsedStart = it.startDate,
                    periodUsedEnd = it.endDate
                )
                connectionServiceReadRepository.save(s)
            }

            return connectionReadRepository.save(this)
        }
    }

    fun delete(connectionId: UUID) {
        connectionReadRepository.deleteById(connectionId)
    }

    fun updateCompany(company: CompanyReadEntity) {
        connectionReadRepository.updateCompany(
            companyId = company.id,
            slug = company.slug,
            name = company.name,
            logo = company.data.logo?.toString(),
            occupation = company.data.occupation?.name
        )
    }

    fun updateUser(user: UserReadEntity) {
        connectionReadRepository.updateUser(
            userId = user.id,
            slug = user.data.slug,
            firstName = user.data.firstName,
            lastName = user.data.lastName,
            image = user.data.image?.toString()
        )
    }
}
