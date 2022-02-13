package com.briolink.companyservice.updater.dataloader

import com.briolink.companyservice.common.dataloader.DataLoader
import com.briolink.companyservice.common.jpa.read.entity.UserPermissionRoleReadEntity
import com.briolink.companyservice.common.jpa.read.repository.CompanyReadRepository
import com.briolink.companyservice.common.jpa.read.repository.UserJobPositionReadRepository
import com.briolink.companyservice.common.jpa.read.repository.UserPermissionRoleReadRepository
import com.briolink.companyservice.common.jpa.read.repository.UserReadRepository
import com.briolink.companyservice.updater.handler.company.CompanyHandlerService
import com.briolink.companyservice.updater.handler.userjobposition.UserJobPosition
import com.briolink.companyservice.updater.handler.userjobposition.UserJobPositionHandlerService
import com.briolink.lib.permission.enumeration.AccessObjectTypeEnum
import com.briolink.lib.permission.enumeration.PermissionRoleEnum
import com.briolink.lib.permission.service.PermissionService
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import java.util.UUID
import kotlin.random.Random

@Component
@Order(2)
class UserJobPositionDataLoader(
    var userJobPositionReadRepository: UserJobPositionReadRepository,
    var userReadRepository: UserReadRepository,
    var companyReadRepository: CompanyReadRepository,
    var companyHandlerService: CompanyHandlerService,
    var userPermissionRoleReadRepository: UserPermissionRoleReadRepository,
    var permissionService: PermissionService,
    var userJobPositionHandlerService: UserJobPositionHandlerService

) : DataLoader() {
    private val listJobPosition: List<String> = listOf(
        "Product Manager",
        "IOS developer",
        "Android developer",
        "UX UI designer",
        "Regional finance manager",
        "Software developer",
        "Web developer",
        "Content maker",
        "Hardware developer",
        "Manager",
    )
    val listUserJobPosition = listOf<UUID>(
        UUID.randomUUID(),
        UUID.randomUUID(),
        UUID.randomUUID(),
        UUID.randomUUID(),
        UUID.randomUUID(),
        UUID.randomUUID(),
        UUID.randomUUID(),
        UUID.randomUUID(),
        UUID.randomUUID(),
        UUID.randomUUID(),
        UUID.randomUUID(),
        UUID.randomUUID(),
        UUID.randomUUID(),
        UUID.randomUUID(),
        UUID.randomUUID(),
        UUID.randomUUID(),
        UUID.randomUUID(),
        UUID.randomUUID(),
        UUID.randomUUID(),
        UUID.randomUUID(),
        UUID.randomUUID(),
        UUID.randomUUID(),
        UUID.randomUUID(),
        UUID.randomUUID(),
        UUID.randomUUID(),
        UUID.randomUUID(),
        UUID.randomUUID(),
        UUID.randomUUID(),
        UUID.randomUUID(),
        UUID.randomUUID(),
        UUID.randomUUID(),
        UUID.randomUUID(),
        UUID.randomUUID(),
        UUID.randomUUID(),
        UUID.randomUUID(),
        UUID.randomUUID(),
        UUID.randomUUID(),
    )

    override fun loadData() {
        if (
            userJobPositionReadRepository.count().toInt() == 0 &&
            userReadRepository.count().toInt() != 0 &&
            companyReadRepository.count().toInt() != 0
        ) {
            val listCompany = companyReadRepository.findAll()
            val listUser = userReadRepository.findAll()

            for (i in 1..COUNT_JOB_POSITION) {

                val userRandom = listUser.random()
                val companyRandom = listCompany.random()
                val startDate = randomDate(2010, 2021)
                val endDate = if (Random.nextBoolean()) randomDate(2010, 2021, startDate) else null
                val userJobPosition = UserJobPosition(
                    id = listUserJobPosition.random(),
                    title = listJobPosition.random(),
                    isCurrent = Random.nextBoolean(),
                    companyId = companyRandom.id,
                    userId = userRandom.id,
                    startDate = startDate,
                    endDate = endDate,
                )
                userJobPositionHandlerService.createOrUpdate(userJobPosition)

                try {
                    val permissionRole = permissionService.createPermissionRole(
                        userId = userRandom.id,
                        accessObjectType = AccessObjectTypeEnum.Company,
                        accessObjectId = companyRandom.id,
                        permissionRole = PermissionRoleEnum.values().random()
                    ) ?: throw RuntimeException("Don`t create permission role")

                    val permissionRights = permissionService.getUserPermissionRights(
                        userId = permissionRole.userId,
                        accessObjectId = permissionRole.accessObjectId,
                        accessObjectType = permissionRole.accessObjectType
                    ) ?: throw RuntimeException("Don`t get permission rights")

                    val userPermissionRoleRead = userPermissionRoleReadRepository.save(
                        UserPermissionRoleReadEntity(
                            id = permissionRole.id,
                            accessObjectUuid = permissionRole.accessObjectId,
                            userId = permissionRole.userId,
                            _accessObjectType = permissionRole.accessObjectType.id,
                            _role = permissionRole.permissionRole.id,
                            data = UserPermissionRoleReadEntity.Data(
                                level = permissionRole.permissionRole.level,
                                enabledPermissionRights = permissionRights.permissionRights
                            )
                        )
                    )
                    userJobPositionHandlerService.addUserPermission(userPermissionRoleRead)
                } catch (_: Exception) {
                }
            }
        }
    }

    companion object {
        const val COUNT_JOB_POSITION = 200
    }
}
