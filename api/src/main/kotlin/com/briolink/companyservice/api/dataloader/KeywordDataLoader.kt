package com.briolink.companyservice.api.dataloader

import com.briolink.companyservice.api.service.KeywordService
import com.briolink.companyservice.common.jpa.read.repository.KeywordReadRepository
import com.briolink.companyservice.common.jpa.write.entity.KeywordWriteEntity
import com.briolink.companyservice.common.jpa.write.repository.KeywordWriteRepository
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import java.util.*

@Component
@Order(1)
class KeywordDataLoader(
    var readRepository: KeywordReadRepository,
    var writeRepository: KeywordWriteRepository,
    var service: KeywordService
) : DataLoader() {
    override fun loadData() {
        if (writeRepository.count().toInt() == 0) {
            val keywordList: MutableMap<String, String> = mutableMapOf()
            keywordList["1763c0ec-8704-4b32-bcc4-590e6d27a3c0"] = "Device"
            keywordList["d1c66ba1-9b04-4179-a816-15b129a7940a"] = "Electronic"
            keywordList["f2a3b949-a9ad-4bbb-a81b-43fba5e269b2"] = "Innovative"
            keywordList["5b844f0c-de9a-4c14-91fb-978f9f865288"] = "Telephone"
            keywordList["fcb3cef6-2983-40de-b67a-a6e9c4805dd3"] = "Retail"
            keywordList["d2308497-7369-4689-a248-b082ee6063ca"] = "Internet"
            keywordList["d555af6e-fd00-4872-8e2d-4547fa5d5f13"] = "Search"
            keywordList["3832bfed-fa70-466d-b4b5-32c86b1ac05b"] = "Technology"
            keywordList["c658ba40-ac1d-47a9-b6dc-736ea840c8a0"] = "Trade"
            keywordList["849e58f0-fb6c-42ae-b1da-5484256a89b5"] = "Design"

            keywordList.forEach { (uuid, name) ->
                service.create(
                        KeywordWriteEntity().apply {
                            id = UUID.fromString(uuid)
                            this.name = name
                        },
                )
            }
        }
    }

}
