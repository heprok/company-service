package com.briolink.companyservice.api.dataloader

import com.briolink.companyservice.api.service.ServiceCompanyService
import com.briolink.companyservice.common.jpa.read.repository.ServiceReadRepository
import com.briolink.companyservice.common.jpa.read.entity.ServiceReadEntity
import com.briolink.companyservice.common.jpa.read.repository.CompanyReadRepository
import com.briolink.companyservice.common.jpa.write.repository.CompanyWriteRepository
import com.briolink.companyservice.common.jpa.write.repository.IndustryWriteRepository
import com.briolink.companyservice.common.util.StringUtil
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import java.util.*
import kotlin.random.Random

@Component
@Order(3)
class ServiceDataLoader(
    var readRepository: ServiceReadRepository,
    var companyWriteRepository: CompanyWriteRepository,
    var serviceCompanyService: ServiceCompanyService
) : DataLoader() {
    val listName: List<String> = listOf(
            "Advertising on Google services",
            "Software provision",
            "Software provision",
            "Executive Education",
            "Innovation culture",
            "Technology evalution",
            "Executive Education",
            "Innovation culture",
            "Online sales",
    )

    override fun loadData() {
        if (readRepository.count().toInt() == 0 &&
            companyWriteRepository.count().toInt() != 0
        ) {
            val companyList = companyWriteRepository.findAll()
            for (i in 1..COUNT_SERVICE) {
                serviceCompanyService.createService(
                        ServiceReadEntity(
                                id = UUID.randomUUID(),
                                companyId = companyList.random().id!!,
                                verifiedUses = Random.nextInt(0, 600),
                                price = Random.nextDouble(0.0, 6000000.0),
                                lastUsed = randomDate(2010, 2021),
                                name = listName[(i % 9)],
                                created = randomDate(2010, 2021),
                                data = ServiceReadEntity.Data(
                                        image = "https://placeimg.com/640/640/tech",
                                        slug = StringUtil.slugify(listName[(i % 9)]),
                                ),
                        ),
                )
            }
        }
    }

    companion object {
        const val COUNT_SERVICE = 500
    }
}
