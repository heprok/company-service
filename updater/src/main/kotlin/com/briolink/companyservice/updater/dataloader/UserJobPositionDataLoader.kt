package com.briolink.companyservice.updater.dataloader

import com.briolink.companyservice.common.jpa.read.repository.UserReadRepository
import com.briolink.companyservice.common.jpa.read.repository.CompanyReadRepository
import com.briolink.companyservice.common.jpa.read.repository.UserJobPositionReadRepository
import com.briolink.companyservice.updater.dto.UserJobPosition
import com.briolink.companyservice.updater.event.UserJobPositionCreatedEvent
import org.springframework.context.ApplicationEventPublisher
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import java.util.*
import kotlin.random.Random

@Component
@Order(2)
class UserJobPositionDataLoader(
    var readRepository: UserJobPositionReadRepository,
    var userReadRepository: UserReadRepository,
    var companyReadRepository: CompanyReadRepository,
    private val applicationEventPublisher: ApplicationEventPublisher,

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

    override fun loadData() {
        if (
            readRepository.count().toInt() == 0 &&
            userReadRepository.count().toInt() != 0 &&
            companyReadRepository.count().toInt() != 0
        ) {
            val listCompany = companyReadRepository.findAll()
            val listUser = userReadRepository.findAll()
            for (i in 1..COUNT_JOB_POSITION) {
                applicationEventPublisher.publishEvent(
                        UserJobPositionCreatedEvent(
                                UserJobPosition(
                                        id = UUID.randomUUID(),
                                        title = listJobPosition.random(),
                                        startDate = randomDate(2018, 2021),
                                        endDate = if (Random.nextBoolean()) null else randomDate(2012, 2020),
                                        companyId = listCompany.random().id,
                                        userId = listUser.random().id,
                                ),
                        ),
                )
            }
        }
    }

    companion object {
        const val COUNT_JOB_POSITION = 300
    }
}
