package com.briolink.companyservice.updater.dataloader

import com.briolink.companyservice.common.jpa.read.entity.UserJobPositionReadEntity
import com.briolink.companyservice.common.jpa.read.repository.UserReadRepository
import com.briolink.companyservice.common.jpa.read.repository.CompanyReadRepository
import com.briolink.companyservice.common.jpa.read.repository.UserJobPositionReadRepository
import com.briolink.companyservice.updater.handler.service.CompanyHandlerService
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import java.util.*

@Component
@Order(2)
class UserJobPositionDataLoader(
    var userJobPositionReadRepository: UserJobPositionReadRepository,
    var userReadRepository: UserReadRepository,
    var companyReadRepository: CompanyReadRepository,
    var companyHandlerService: CompanyHandlerService,

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
            userJobPositionReadRepository.count().toInt() == 0 &&
            userReadRepository.count().toInt() != 0 &&
            companyReadRepository.count().toInt() != 0
        ) {
            val listCompany = companyReadRepository.findAll()
            val listUser = userReadRepository.findAll()

            for (i in 1..COUNT_JOB_POSITION) {
                val userRandom = listUser.random()
                val companyRandom = listCompany.random()
                if (!userJobPositionReadRepository.existsByUserIdAndCompanyId(userId = userRandom.id, companyId = companyRandom.id)) {
                    userJobPositionReadRepository.save(
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
                    companyHandlerService.setOwner(it.id, UUID.fromString("e0983f0e-68ad-4a5a-8968-7406f915ff90"))
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
//                    eventPublisher.publishAsync(
//                            UserJobPositionCreatedEvent(this),
//                    )
//                }
            }
        }
    }

    companion object {
        const val COUNT_JOB_POSITION = 20
    }
}
