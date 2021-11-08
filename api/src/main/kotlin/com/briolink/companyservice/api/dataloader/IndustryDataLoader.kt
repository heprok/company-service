package com.briolink.companyservice.api.dataloader

import com.briolink.companyservice.api.service.IndustryService
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
            val industryList: MutableMap<String, String> = mutableMapOf()
            industryList["14b60a98-1e84-4657-af4e-b9b46a0fe3bc"] = "Social"
            industryList["6763e338-457a-408c-9935-63e26101227c"] = "Internet"
            industryList["4e5d6b07-2779-48b2-a6d8-b0249d058e9d"] = "Software"
            industryList["84303076-6477-4c78-b24a-91f9cdc1b8a7"] = "Trade"
            industryList["b70e4e34-9b68-43af-8026-0f277b6b0d1b"] = "Design"
            industryList["7ded339f-999c-4c42-9db0-b80fccfa33f1"] = "Beauty"
            industryList["04881229-ebc7-46a1-a42e-237794c87cb0"] = "Music"
            industryList["9b9a8ab3-4899-4904-9e83-118e6fae1522"] = "Health"
            industryList["e6bff832-dd2a-4451-9e58-c7c008a7b0ba"] = "Transport"
            industryList["5e352386-e80f-4489-8dc4-b24975e741b2"] = "Hardware"
            industryList["2b3faf20-6236-4ff5-98dc-269a3c214bfc"] = "Consulting"

            industryList.forEach { (uuid, name) ->
                service.create(IndustryWriteEntity().apply {
                    id = UUID.fromString(uuid)
                    this.name = name
                    println(name)
                })
            }
        }
    }
}
