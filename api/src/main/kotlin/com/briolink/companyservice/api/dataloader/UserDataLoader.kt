package com.briolink.companyservice.api.dataloader

import com.briolink.companyservice.api.service.CompanyService
import com.briolink.companyservice.common.jpa.read.repository.UserReadRepository
import com.briolink.companyservice.common.jpa.read.entity.UserReadEntity
import com.briolink.companyservice.common.jpa.read.repository.CompanyReadRepository
import com.briolink.companyservice.common.jpa.write.repository.IndustryWriteRepository
import com.briolink.companyservice.common.util.StringUtil
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import java.net.URL
import java.util.*
import kotlin.random.Random

@Component
@Order(1)
class UserDataLoader(
    var readRepository: UserReadRepository,
    var companyService: CompanyService
) : DataLoader() {
    val listFirstName: List<String> = listOf(
            "Lynch", "Kennedy", "Williams", "Evans", "Jones", "Burton", "Miller", "Smith", "Nelson", "Lucas",
    )

    val listLastName: List<String> = listOf(
            "Scott", "Cynthia", "Thomas", "Thomas", "Lucy", "Dawn", "Jeffrey", "Ann", "Joan", "Lauren",
    )

    override fun loadData() {
        if (readRepository.count().toInt() == 0  ) {
            for (i in 1..COUNT_USER) {
                readRepository.save(
                        UserReadEntity(
                                id = UUID.randomUUID(),
                                data = UserReadEntity.Data(
                                        firstName = listFirstName.random(),
                                        lastName = listLastName.random(),
                                        image = URL("https://placeimg.com/148/148/people"),
                                ).apply {
                                    slug = StringUtil.slugify(firstName + " " + lastName + " " + UUID.randomUUID().toString())
                                },
                        ),
                )
            }
            readRepository.save(
                    UserReadEntity(
                            id = UUID.fromString("4753e3ca-aefc-4e6c-981e-2833c37171fb"),
                            data = UserReadEntity.Data(
                                    firstName = "Denis",
                                    lastName = "Nikonov",
                                    image = URL("https://briolink.s3.us-east-2.amazonaws.com/uploads/user/profile-image/fa33cc305b75476684333434ed464668.jpg"),
                            ).apply {
                                slug = "denis-nikonov"
                            }
                    )
            )
        }
    }

    companion object {
        const val COUNT_USER = 1
    }

}
