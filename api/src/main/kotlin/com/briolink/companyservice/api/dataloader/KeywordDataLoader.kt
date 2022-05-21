package com.briolink.companyservice.api.dataloader

import com.briolink.companyservice.api.service.KeywordService
import com.briolink.companyservice.common.jpa.read.repository.KeywordReadRepository
import com.briolink.companyservice.common.jpa.write.repository.KeywordWriteRepository
import com.briolink.lib.common.utils.BlDataLoader
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

@Component
@Order(1)
class KeywordDataLoader(
    var readRepository: KeywordReadRepository,
    var writeRepository: KeywordWriteRepository,
    var service: KeywordService
) : BlDataLoader() {
    override fun loadData() {
        if (writeRepository.count().toInt() == 0) {
            val keywordList: MutableList<String> = mutableListOf()
            keywordList.add("Device")
            keywordList.add("Electronic")
            keywordList.add("Innovative")
            keywordList.add("Telephone")
            keywordList.add("Retail")
            keywordList.add("Internet")
            keywordList.add("Search")
            keywordList.add("Technology")
            keywordList.add("Trade")
            keywordList.add("Design")

            keywordList.forEach {
                service.create(it)
            }
        }
    }
}
