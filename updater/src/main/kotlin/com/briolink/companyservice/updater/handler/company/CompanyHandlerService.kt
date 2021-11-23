package com.briolink.companyservice.updater.handler.company

import com.briolink.companyservice.common.domain.v1_0.Company
import com.briolink.companyservice.common.jpa.enumration.AccessObjectTypeEnum
import com.briolink.companyservice.common.jpa.enumration.UserPermissionRoleTypeEnum
import com.briolink.companyservice.common.jpa.read.entity.CompanyReadEntity
import com.briolink.companyservice.common.jpa.read.entity.UserPermissionRoleReadEntity
import com.briolink.companyservice.common.jpa.read.repository.CompanyReadRepository
import com.briolink.companyservice.common.jpa.read.repository.UserPermissionRoleReadRepository
import com.briolink.companyservice.common.service.LocationService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Transactional
@Service
class CompanyHandlerService(
    private val companyReadRepository: CompanyReadRepository,
    private val userPermissionRoleReadRepository: UserPermissionRoleReadRepository,
    private val locationService: LocationService
) {

    fun createOrUpdate(company: Company): CompanyReadEntity {
        companyReadRepository.findById(company.id).orElse(
                CompanyReadEntity(company.id, company.slug, company.name),
        ).apply {
            data = CompanyReadEntity.Data(
                    name = company.name,
                    website = company.website,
                    facebook = company.facebook,
                    twitter = company.twitter,
                    isTypePublic = company.isTypePublic,
                    logo = company.logo,
                    description = company.description,
                    industry = company.industry?.let { CompanyReadEntity.Industry(it.id, it.name) },
                    keywords = company.keywords?.let { list ->
                        list.map {
                            CompanyReadEntity.Keyword(
                                    it.id,
                                    it.name,
                            )
                        }
                    } ?: mutableListOf(),
                    occupation = company.occupation?.let { CompanyReadEntity.Occupation(it.id.toString(), it.name) },
            ).apply {
                location = company.locationId?.let { locationService.getLocation(it) }
            }
            return companyReadRepository.save(this)
        }
    }

    fun setPermission(companyId: UUID, userId: UUID, roleType: UserPermissionRoleTypeEnum): UserPermissionRoleReadEntity =
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

    fun addEmployee(companyId: UUID, userId: UUID): UserPermissionRoleReadEntity {
//        return if (!userPermissionRoleReadRepository.existsByCompanyId(companyId) || true) {
        //TODO добавить условие
        return if (true) {
            setPermission(companyId = companyId, userId = userId, roleType = UserPermissionRoleTypeEnum.Owner)
        } else {
            setPermission(companyId = companyId, userId = userId, roleType = UserPermissionRoleTypeEnum.Employee)
        }
    }
}

