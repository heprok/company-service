package com.briolink.companyservice.api.dataloader

import com.briolink.companyservice.api.service.IndustryService
import com.briolink.companyservice.common.dataloader.DataLoader
import com.briolink.companyservice.common.jpa.read.repository.IndustryReadRepository
import com.briolink.companyservice.common.jpa.write.entity.IndustryWriteEntity
import com.briolink.companyservice.common.jpa.write.repository.IndustryWriteRepository
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import java.util.*

@Component
@Order(1)
class IndustryDataLoader(
    var readRepository: IndustryReadRepository,
    var writeRepository: IndustryWriteRepository,
    var service: IndustryService
) : DataLoader() {
    override fun loadData() {
        if (writeRepository.count().toInt() == 0) {
            val industryList: MutableList<String> = mutableListOf()
            industryList.add("Social")
            industryList.add("Internet")
            industryList.add("Software")
            industryList.add("Trade")
            industryList.add("Design")
            industryList.add("Beauty")
            industryList.add("Music")
            industryList.add("Health")
            industryList.add("Transport")
            industryList.add("Hardware")
            industryList.add("Consulting")

            industryList.forEach { nameIndustry ->
                service.create(nameIndustry)
            }
        }
    }
}
