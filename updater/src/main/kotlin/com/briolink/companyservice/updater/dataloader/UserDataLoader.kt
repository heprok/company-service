package com.briolink.companyservice.updater.dataloader

import com.briolink.companyservice.common.dataloader.DataLoader
import com.briolink.companyservice.common.jpa.read.entity.UserReadEntity
import com.briolink.companyservice.common.jpa.read.repository.UserReadRepository
import com.briolink.companyservice.common.util.StringUtil
import java.net.URL
import java.util.UUID
import kotlin.random.Random
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

@Component
@Order(1)
class UserDataLoader(
    var userReadRepository: UserReadRepository,

    ) : DataLoader() {
    val listFirstName: List<String> = listOf(
            "Lynch", "Kennedy", "Williams", "Evans", "Jones", "Burton", "Miller", "Smith", "Nelson", "Lucas",
    )

    val listLastName: List<String> = listOf(
            "Scott", "Cynthia", "Thomas", "Thomas", "Lucy", "Dawn", "Jeffrey", "Ann", "Joan", "Lauren",
    )

    override fun loadData() {
        if (userReadRepository.count().toInt() == 0) {
            for (i in 1..COUNT_USER) {
                userReadRepository.save(
                        UserReadEntity(
                                id = UUID.randomUUID(),
                        ).apply {
                            data = UserReadEntity.Data(
                                    firstName = listFirstName.random(),
                                    lastName = listLastName.random(),
                                    image = if (Random.nextBoolean()) URL("https://placeimg.com/148/148/people") else null,
                            ).apply {
                                slug = StringUtil.slugify(
                                        firstName + " " + lastName + " " + UUID.randomUUID().toString(),
                                )
                            }
                        },
                )
            }
        }
    }

    companion object {
        const val COUNT_USER = 10
    }
}
