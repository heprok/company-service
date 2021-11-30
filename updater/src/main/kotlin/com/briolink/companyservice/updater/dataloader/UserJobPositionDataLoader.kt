package com.briolink.companyservice.updater.dataloader

import com.briolink.companyservice.common.dataloader.DataLoader
import com.briolink.companyservice.common.jpa.read.repository.CompanyReadRepository
import com.briolink.companyservice.common.jpa.read.repository.UserJobPositionReadRepository
import com.briolink.companyservice.common.jpa.read.repository.UserReadRepository
import com.briolink.companyservice.updater.handler.company.CompanyHandlerService
import com.briolink.companyservice.updater.handler.userjobposition.UserJobPosition
import com.briolink.companyservice.updater.handler.userjobposition.UserJobPositionHandlerService
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
    )

    override fun loadData() {
        if (
//            userJobPositionReadRepository.count().toInt() == 0 &&
            userReadRepository.count().toInt() != 0 &&
            companyReadRepository.count().toInt() != 0
        ) {
            val listCompany = companyReadRepository.findAll()
            val listUser = userReadRepository.findAll()

            for (i in 1..COUNT_JOB_POSITION) {
                val userRandom = listUser.random()
                val companyRandom = listCompany.random()
                val startDate = randomDate(2010, 2021)
                val endDate = if (Random.nextBoolean()) randomDate(startDate.year, 2021) else null
                val userJobPosition = UserJobPosition(
                    id = listUserJobPosition.random(),
                    title = listJobPosition.random(),
                    isCurrent = Random.nextBoolean(),
                    companyId = companyRandom.id,
                    userId = userRandom.id,
                    startDate = startDate,
                    endDate = endDate,
                )
                if (Random.nextBoolean()) userJobPositionHandlerService.create(userJobPosition)
                else userJobPositionHandlerService.update(userJobPosition)
            }
        }
    }

    companion object {
        const val COUNT_JOB_POSITION = 200
    }
}
