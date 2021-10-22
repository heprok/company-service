package com.briolink.companyservice.updater.dataloader

import com.briolink.companyservice.common.jpa.read.entity.UserJobPositionReadEntity
import com.briolink.companyservice.common.jpa.read.repository.UserReadRepository
import com.briolink.companyservice.common.jpa.read.repository.CompanyReadRepository
import com.briolink.companyservice.common.jpa.read.repository.UserJobPositionReadRepository
import com.briolink.companyservice.updater.dto.UserJobPosition
import com.briolink.companyservice.updater.event.UserJobPositionCreatedEvent
import com.briolink.companyservice.updater.service.CompanyService
import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.context.ApplicationEventPublisher
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import java.net.URL
import java.util.*
import kotlin.random.Random

@Component
@Order(2)
class UserJobPositionDataLoader(
    var readRepository: UserJobPositionReadRepository,
    var userReadRepository: UserReadRepository,
    var companyReadRepository: CompanyReadRepository,
    var companyService: CompanyService,
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
                val userRandom = listUser.random()
                val companyRandom = listCompany.random()
                if (!readRepository.existsByUserIdAndCompanyId(userId = userRandom.id, companyId = companyRandom.id)) {
                    readRepository.save(
                            UserJobPositionReadEntity(
                                    id = UUID.randomUUID(),
                                    companyId = companyRandom.id,
                                    userId = userRandom.id,
                            ).apply {
                                data = UserJobPositionReadEntity.Data(
                                        title = listJobPosition.random(),
                                        user = UserJobPositionReadEntity.User(
                                                firstName = userRandom.data.firstName,
                                                slug = userRandom.data.slug,
                                                lastName = userRandom.data.lastName,
                                                image = userRandom.data.image,
                                        ),
                                )
                            },
                    )
                }

                listCompany.forEach{
                    companyService.setOwner(it.id, UUID.fromString("4753e3ca-aefc-4e6c-981e-2833c37171fb"))
                }


//                UserJobPosition(
//                        id = UUID.randomUUID(),
//                        title = listJobPosition.random(),
//                        startDate = randomDate(2018, 2021),
//                        endDate = if (Random.nextBoolean())
//                            null
//                        else randomDate(2012, 2020),
//                        companyId = listCompany.random().id,
//                        userId = listUser.random().id,
//                ).apply {
//                    applicationEventPublisher.publishEvent(
//                            UserJobPositionCreatedEvent(this),
//                    )
//                }
            }
        }
    }

    companion object {
        const val COUNT_JOB_POSITION = 300
    }
}
