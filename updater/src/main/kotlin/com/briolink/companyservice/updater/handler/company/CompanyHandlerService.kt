package com.briolink.companyservice.updater.handler.company

import com.briolink.companyservice.common.domain.v1_0.Company
import com.briolink.companyservice.common.jpa.enumeration.AccessObjectTypeEnum
import com.briolink.companyservice.common.jpa.enumeration.UserPermissionRoleTypeEnum
import com.briolink.companyservice.common.jpa.read.entity.CompanyReadEntity
import com.briolink.companyservice.common.jpa.read.entity.UserPermissionRoleReadEntity
import com.briolink.companyservice.common.jpa.read.repository.CompanyReadRepository
import com.briolink.companyservice.common.jpa.read.repository.UserPermissionRoleReadRepository
import com.briolink.companyservice.common.service.LocationService
import com.briolink.companyservice.common.service.PermissionService
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Transactional
@Service
class CompanyHandlerService(
    private val companyReadRepository: CompanyReadRepository,
    private val userPermissionRoleReadRepository: UserPermissionRoleReadRepository,
    private val locationService: LocationService,
    private val permissionService: PermissionService
) {

    fun createOrUpdate(entityPrevCompany: CompanyReadEntity? = null, companyDomain: Company): CompanyReadEntity {
        val company = entityPrevCompany ?: CompanyReadEntity(companyDomain.id, companyDomain.slug, companyDomain.name)
        company.apply {
            data = CompanyReadEntity.Data(
                name = companyDomain.name,
                website = companyDomain.website,
                facebook = companyDomain.facebook,
                twitter = companyDomain.twitter,
                isTypePublic = companyDomain.isTypePublic,
                logo = companyDomain.logo,
                description = companyDomain.description,
                industry = companyDomain.industry?.let { CompanyReadEntity.Industry(it.id, it.name) },
                keywords = companyDomain.keywords?.let { list ->
                    list.map {
                        CompanyReadEntity.Keyword(
                            it.id,
                            it.name,
                        )
                    }
                } ?: mutableListOf(),
                occupation = companyDomain.occupation?.let { CompanyReadEntity.Occupation(it.id, it.name) },
            ).apply {
                location = companyDomain.locationId?.let { locationService.getLocation(it) }
            }
            return companyReadRepository.save(this)
        }
    }

    fun findById(companyId: UUID): CompanyReadEntity? = companyReadRepository.findByIdOrNull(companyId)

    fun setPermission(
        companyId: UUID,
        userId: UUID,
        roleType: UserPermissionRoleTypeEnum
    ): UserPermissionRoleReadEntity =
        userPermissionRoleReadRepository.save(
            userPermissionRoleReadRepository.getUserPermissionRole(
                accessObjectUuid = companyId,
                userId = userId,
                accessObjectType = AccessObjectTypeEnum.Company.value,
            )?.apply {
                role = roleType
            } ?: UserPermissionRoleReadEntity(
                accessObjectUuid = companyId,
                userId = userId,
            ).apply {
                role = roleType
            },
        )

    fun addOwner(companyId: UUID, userId: UUID): UserPermissionRoleReadEntity {
        return permissionService.createPermission(
            accessObjectType = AccessObjectTypeEnum.Company,
            userId = userId,
            accessObjectUuid = companyId,
            roleType = UserPermissionRoleTypeEnum.Owner,
        )
    }

    fun addEmployee(companyId: UUID, userId: UUID): UserPermissionRoleReadEntity {
//        return if (!userPermissionRoleReadRepository.existsByCompanyId(companyId) || true) {
        // TODO добавить условие, если нет owner то добавить employee
        return if (true) {
            addOwner(companyId = companyId, userId = userId)
        } else {
            setPermission(companyId = companyId, userId = userId, roleType = UserPermissionRoleTypeEnum.Employee)
        }
    }
}
