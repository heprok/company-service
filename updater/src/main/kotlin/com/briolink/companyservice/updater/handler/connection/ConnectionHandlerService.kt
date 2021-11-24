package com.briolink.companyservice.updater.handler.connection

import com.briolink.companyservice.common.jpa.enumration.AccessObjectTypeEnum
import com.briolink.companyservice.common.jpa.enumration.CompanyRoleTypeEnum
import com.briolink.companyservice.common.jpa.enumration.ConnectionStatusEnum
import com.briolink.companyservice.common.jpa.enumration.PermissionRightEnum
import com.briolink.companyservice.common.jpa.read.entity.CompanyReadEntity
import com.briolink.companyservice.common.jpa.read.entity.ConnectionReadEntity
import com.briolink.companyservice.common.jpa.read.entity.ConnectionServiceReadEntity
import com.briolink.companyservice.common.jpa.read.entity.UserReadEntity
import com.briolink.companyservice.common.jpa.read.repository.CompanyReadRepository
import com.briolink.companyservice.common.jpa.read.repository.ConnectionReadRepository
import com.briolink.companyservice.common.jpa.read.repository.ConnectionServiceReadRepository
import com.briolink.companyservice.common.jpa.read.repository.UserReadRepository
import com.briolink.companyservice.common.service.PermissionService
import com.vladmihalcea.hibernate.type.range.Range
import java.util.UUID
import java.util.stream.Collectors
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional
@Service
class ConnectionHandlerService(
    private val connectionReadRepository: ConnectionReadRepository,
    private val companyReadRepository: CompanyReadRepository,
    private val connectionServiceReadRepository: ConnectionServiceReadRepository,
    private val userReadRepository: UserReadRepository,
    private val permissionService: PermissionService,
) {
    fun createOrUpdate(connection: Connection): ConnectionReadEntity {
        val participantUsers = userReadRepository.findByIdIsIn(
                mutableListOf(connection.participantFrom.userId!!, connection.participantTo.userId!!),
        ).stream().collect(Collectors.toMap(UserReadEntity::id) { v -> v })

        val participantCompanies = companyReadRepository.findByIdIsIn(
                mutableListOf(connection.participantFrom.companyId!!, connection.participantTo.companyId!!),
        ).stream().collect(Collectors.toMap(CompanyReadEntity::id) { v -> v })

        val buyerCompany: CompanyReadEntity
        val sellerCompany: CompanyReadEntity
        if (connection.participantFrom.companyRole?.type == ConnectionCompanyRoleType.Buyer) {
            buyerCompany = participantCompanies[connection.participantFrom.companyId]!!
            sellerCompany = participantCompanies[connection.participantTo.companyId]!!
        } else {
            buyerCompany = participantCompanies[connection.participantTo.companyId]!!
            sellerCompany = participantCompanies[connection.participantFrom.companyId]!!
        }
        val industry = buyerCompany.data.industry

        val serviceMinDate = connection.services.minOf { it.startDate!!.value }
        var serviceMaxDate = connection.services.maxOfOrNull { it.endDate?.value ?: -1 }

        if (serviceMaxDate == -1) {
            serviceMaxDate = null
        }

        connection.services.forEach {
            val s = connectionServiceReadRepository.findById(it.id).orElse(ConnectionServiceReadEntity())

            if (s.id == null) s.id = it.id
            s.companyId = sellerCompany.id
            s.serviceId = it.serviceId
            s.name = it.serviceName

            connectionServiceReadRepository.save(s)
        }

        connectionReadRepository.findById(connection.id).orElse(ConnectionReadEntity(connection.id)).apply {
            participantFromCompanyId = connection.participantFrom.companyId!!
            participantFromUserId = connection.participantFrom.userId!!
            participantFromRoleId = connection.participantFrom.companyRole!!.id
            participantFromRoleName = connection.participantFrom.companyRole!!.name
            participantFromRoleType = CompanyRoleTypeEnum.fromInt(connection.participantFrom.companyRole!!.type.value)
            participantToCompanyId = connection.participantTo.companyId!!
            participantToUserId = connection.participantTo.userId!!
            participantToRoleId = connection.participantTo.companyRole!!.id
            participantToRoleName = connection.participantTo.companyRole!!.name
            participantToRoleType = CompanyRoleTypeEnum.fromInt(connection.participantTo.companyRole!!.type.value)
            serviceIds = connection.services.map { service -> service.id }.toList()
            dates = if (serviceMaxDate == null) Range.closedInfinite(serviceMinDate) else Range.closed(serviceMinDate, serviceMaxDate)
            status = ConnectionStatusEnum.fromInt(connection.status.value)
            countryId = buyerCompany.data.location?.country?.id
            stateId = buyerCompany.data.location?.state?.id
            cityId = buyerCompany.data.location?.city?.id
            deletedCompanyIds = listOf()
            hiddenCompanyIds = mutableListOf<UUID>().apply {
                if (!permissionService.isHavePermission(
                            userId = connection.participantFrom.userId!!,
                            companyId = connection.participantFrom.companyId!!,
                            accessObjectType = AccessObjectTypeEnum.Company,
                            PermissionRightEnum.ConnectionCrud,
                    ))
                    add(connection.participantFrom.companyId!!)

                if (!permissionService.isHavePermission(
                            userId = connection.participantTo.userId!!,
                            companyId = connection.participantTo.companyId!!,
                            accessObjectType = AccessObjectTypeEnum.Company,
                            PermissionRightEnum.ConnectionCrud,
                    ))
                    add(connection.participantTo.companyId!!)
            }

            permissionService.isHavePermission(
                    userId = connection.participantTo.userId!!,
                    companyId = connection.participantTo.companyId!!,
                    accessObjectType = AccessObjectTypeEnum.Company,
                    PermissionRightEnum.ConnectionCrud,
            )
            created = connection.created
            companyIndustryId = industry?.id
            this.data = ConnectionReadEntity.Data(
                    participantFrom = ConnectionReadEntity.Participant(
                            user = ConnectionReadEntity.User(
                                    id = participantUsers[connection.participantFrom.userId]!!.id,
                                    slug = participantUsers[connection.participantFrom.userId]!!.data.slug,
                                    image = participantUsers[connection.participantFrom.userId]!!.data.image,
                                    firstName = participantUsers[connection.participantFrom.userId]!!.data.firstName,
                                    lastName = participantUsers[connection.participantFrom.userId]!!.data.lastName,
                            ),
                            userJobPositionTitle = connection.participantFrom.userJobPositionTitle,
                            company = ConnectionReadEntity.Company(
                                    id = connection.participantFrom.companyId!!,
                                    slug = participantCompanies[connection.participantFrom.companyId]!!.slug,
                                    name = participantCompanies[connection.participantFrom.companyId]!!.name,
                                    logo = participantCompanies[connection.participantFrom.companyId]!!.data.logo,
                            ),
                            companyRole = ConnectionReadEntity.CompanyRole(
                                    id = connection.participantFrom.companyRole!!.id,
                                    name = connection.participantFrom.companyRole!!.name,
                                    type = CompanyRoleTypeEnum.valueOf(connection.participantFrom.companyRole!!.type.name),
                            ),
                    ),
                    participantTo = connection.participantTo.let {
                        ConnectionReadEntity.Participant(
                                user = ConnectionReadEntity.User(
                                        id = participantUsers[connection.participantTo.userId]!!.id,
                                        slug = participantUsers[connection.participantTo.userId]!!.data.slug,
                                        image = participantUsers[connection.participantTo.userId]!!.data.image,
                                        firstName = participantUsers[connection.participantTo.userId]!!.data.firstName,
                                        lastName = participantUsers[connection.participantTo.userId]!!.data.lastName,
                                ),
                                userJobPositionTitle = it.userJobPositionTitle,
                                company = ConnectionReadEntity.Company(
                                        id = connection.participantTo.companyId!!,
                                        slug = participantCompanies[connection.participantTo.companyId!!]!!.slug,
                                        name = participantCompanies[connection.participantTo.companyId!!]!!.name,
                                        logo = participantCompanies[connection.participantTo.companyId!!]!!.data.logo,
                                ),
                                companyRole = it.companyRole.let { role ->
                                    ConnectionReadEntity.CompanyRole(
                                            id = role!!.id,
                                            name = role.name,
                                            type = CompanyRoleTypeEnum.valueOf(role.type.name),
                                    )
                                },
                        )
                    },
                    services = ArrayList(
                            connection.services.map {
                                ConnectionReadEntity.Service(
                                        id = it.id,
                                        serviceId = it.serviceId,
                                        serviceName = it.serviceName,
                                        startDate = it.startDate!!,
                                        endDate = it.endDate,
                                )
                            },
                    ),
                    industry = industry?.name,
            )

            return connectionReadRepository.save(this)
        }
    }

    fun delete(connectionId: UUID) {
        connectionReadRepository.deleteById(connectionId)
    }
}
