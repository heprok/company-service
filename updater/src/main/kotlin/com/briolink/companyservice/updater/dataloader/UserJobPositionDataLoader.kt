package com.briolink.companyservice.updater.dataloader

import com.briolink.companyservice.common.dataloader.DataLoader
import com.briolink.companyservice.common.jpa.read.entity.UserReadEntity
import com.briolink.companyservice.common.jpa.read.repository.CompanyReadRepository
import com.briolink.companyservice.common.jpa.read.repository.UserJobPositionReadRepository
import com.briolink.companyservice.common.jpa.read.repository.UserReadRepository
import com.briolink.companyservice.common.util.StringUtil
import com.briolink.companyservice.updater.handler.userjobposition.UserJobPositionEventData
import com.briolink.companyservice.updater.handler.userjobposition.UserJobPositionHandlerService
import com.briolink.lib.permission.enumeration.AccessObjectTypeEnum
import com.briolink.lib.permission.enumeration.PermissionRoleEnum
import com.briolink.lib.permission.service.PermissionService
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import java.net.URL
import java.util.UUID
import kotlin.random.Random

@Component
@Order(2)
class UserJobPositionDataLoader(
    var userJobPositionReadRepository: UserJobPositionReadRepository,
    var userReadRepository: UserReadRepository,
    var companyReadRepository: CompanyReadRepository,
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

    private fun addPermissionToUser() {
        val user =
            UserReadEntity(
                id = UUID.fromString("a7bfe294-2586-452b-b5fc-77700df058d3"),
            ).apply {
                data = UserReadEntity.Data(
                    firstName = "Admin",
                    lastName = "Admin",
                    image = if (Random.nextBoolean()) URL("https://placeimg.com/148/148/people") else null,
                ).apply {
                    slug = StringUtil.slugify(
                        firstName + " " + lastName + " " + UUID.randomUUID().toString(),
                    )
                }
                userReadRepository.save(this)
            }
        val listCompany = companyReadRepository.findAll()
        listCompany.forEach {
            val startDate = randomDate(2010, 2021)
            val endDate = null
            val userJobPosition = UserJobPositionEventData(
                id = listUserJobPosition.random(),
                title = listJobPosition.random(),
                isCurrent = Random.nextBoolean(),
                companyId = it.id,
                userId = user.id,
                startDate = startDate,
                endDate = endDate,
            )
            userJobPositionHandlerService.createOrUpdate(userJobPosition)

            try {
                val permissionRole = permissionService.editPermissionRole(
                    userId = user.id,
                    accessObjectType = AccessObjectTypeEnum.Company,
                    accessObjectId = it.id,
                    permissionRole = PermissionRoleEnum.Owner,
                ) ?: throw RuntimeException("Don`t create permission role")

//                val permissionRights = permissionService.getUserPermissionRights(
//                    userId = permissionRole.userId,
//                    accessObjectId = permissionRole.accessObjectId,
//                    accessObjectType = permissionRole.accessObjectType,
//                ) ?: throw RuntimeException("Don`t get permission rights")

//                val userPermissionRoleRead = userPermissionRoleReadRepository.save(
//                    UserPermissionRoleReadEntity(
//                        id = permissionRole.id,
//                        accessObjectUuid = permissionRole.accessObjectId,
//                        userId = permissionRole.userId,
//                        _accessObjectType = permissionRole.accessObjectType.id,
//                        _role = permissionRole.permissionRole.id,
//                        data = UserPermissionRoleReadEntity.Data(
//                            level = permissionRole.permissionRole.level,
//                            enabledPermissionRights = permissionRights.permissionRights,
//                        ),
//                    ),
//                )
                userJobPositionHandlerService.updateUserPermission(
                    userId = permissionRole.userId,
                    companyId = permissionRole.accessObjectId,
                )
            } catch (_: Exception) {
            }
        }
    }

    override fun loadData() {

        if (
            userJobPositionReadRepository.count().toInt() == 0 &&
            userReadRepository.count().toInt() != 0 &&
            companyReadRepository.count().toInt() != 0
        ) {
            addPermissionToUser()

            val listCompany = companyReadRepository.findAll()
            val listUser = userReadRepository.findAll()
            for (i in 1..COUNT_JOB_POSITION) {

                val userRandom = listUser.random()
                val companyRandom = listCompany.random()
                val startDate = randomDate(2010, 2021)
                val isCurrent = Random.nextBoolean()
                val endDate = if (isCurrent) null else if (Random.nextBoolean()) randomDate(2010, 2021, startDate) else null
                val userJobPosition = UserJobPositionEventData(
                    id = listUserJobPosition.random(),
                    title = listJobPosition.random(),
                    isCurrent = isCurrent,
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
                        permissionRole = PermissionRoleEnum.values().random(),
                    ) ?: throw RuntimeException("Don`t create permission role")

                    userJobPositionHandlerService.updateUserPermission(
                        userId = permissionRole.userId,
                        companyId = permissionRole.accessObjectId,
                    )
                } catch (_: Exception) {
                }
            }
        }
    }

    companion object {
        const val COUNT_JOB_POSITION = 500
    }
}
