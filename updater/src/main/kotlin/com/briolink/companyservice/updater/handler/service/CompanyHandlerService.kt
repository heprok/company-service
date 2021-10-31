package com.briolink.companyservice.updater.handler.service

import com.briolink.companyservice.common.domain.v1_0.Company
import com.briolink.companyservice.common.jpa.read.entity.CompanyReadEntity
import com.briolink.companyservice.common.jpa.read.entity.UserPermissionRoleReadEntity
import com.briolink.companyservice.common.jpa.read.repository.CompanyReadRepository
import com.briolink.companyservice.common.jpa.read.repository.UserPermissionRoleReadRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*
import javax.persistence.EntityNotFoundException

@Transactional
@Service
class CompanyHandlerService(
    private val companyReadRepository: CompanyReadRepository,
    private val userPermissionRoleReadRepository: UserPermissionRoleReadRepository,
) {

    fun createOrUpdate(company: Company) {
       companyReadRepository.findById(company.id).orElse(
                CompanyReadEntity(company.id, company.slug),
        ).apply {
            data = CompanyReadEntity.Data(
                    name = company.name,
                    website = company.website,
                    location = company.location,
                    facebook = company.facebook,
                    twitter = company.twitter,
                    isTypePublic = company.isTypePublic,
                    logo = company.logo,
                    description = company.description,
                    industry = company.industry?.let { CompanyReadEntity.Industry(it.id.toString(), it.name) },
                    keywords = company.keywords?.let { list ->
                        list.map {
                            CompanyReadEntity.Keyword(
                                    it.id,
                                    it.name,
                            )
                        }
                    } ?: mutableListOf(),
                    occupation = company.occupation?.let { CompanyReadEntity.Occupation(it.id.toString(), it.name) },
            )
           companyReadRepository.save(this)
        }
    }

    fun setOwner(companyId: UUID, userId: UUID): Boolean {
        val company =
                companyReadRepository.findById(companyId).orElseThrow { throw EntityNotFoundException("$companyId company not found") }
        return if (company.data.createdBy == null) {
            company.data.createdBy = userId
            companyReadRepository.save(company)
            userPermissionRoleReadRepository.save(
                    UserPermissionRoleReadEntity(
                            userId = userId,
                            role = UserPermissionRoleReadEntity.RoleType.Owner,
                            accessObjectUuid = companyId,
                    ),
            )
            true
        } else {
            false
        }
    }

    fun setPermission(companyId: UUID, userId: UUID, roleType: UserPermissionRoleReadEntity.RoleType) {
        userPermissionRoleReadRepository.save(
                userPermissionRoleReadRepository.findByAccessObjectUuidAndAccessObjectTypeAndUserId(
                        accessObjectUuid = companyId,
                        userId = userId,
                )?.apply {
                    role = roleType
                } ?: UserPermissionRoleReadEntity(accessObjectUuid = companyId, userId = userId, role = roleType),
        )
    }

    fun getPermission(companyId: UUID, userId: UUID): UserPermissionRoleReadEntity.RoleType? {
        return userPermissionRoleReadRepository.findByAccessObjectUuidAndAccessObjectTypeAndUserId(
                accessObjectUuid = companyId,
                accessObjectType = 1,
                userId = userId,
        )?.role
    }
}

