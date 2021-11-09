package com.briolink.companyservice.api.dataloader

import com.briolink.companyservice.api.service.OccupationService
import com.briolink.companyservice.common.dataloader.DataLoader
import com.briolink.companyservice.common.jpa.read.repository.OccupationReadRepository
import com.briolink.companyservice.common.jpa.write.entity.OccupationWriteEntity
import com.briolink.companyservice.common.jpa.write.repository.OccupationWriteRepository
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import java.util.*

@Component
@Order(1)
class OccupationDataLoader(
    var readRepository: OccupationReadRepository,
    var writeRepository: OccupationWriteRepository,
    var service: OccupationService
) : DataLoader() {
    override fun loadData() {
        if (writeRepository.count().toInt() == 0) {
            val occupationList: MutableMap<String, String> = mutableMapOf()
            occupationList["cab8111d-3bf7-4c56-871d-37aa1e1a6361"] = "Consulting"
            occupationList["dbadd3f3-24ba-4897-b5ba-34e14c95e7fe"] = "Technology"
            occupationList["9ac6ffa8-b109-4936-912c-956d0347fd7b"] = "Computer Systems Analyst"
            occupationList["9a65eed5-3933-4604-9796-54458a2c4906"] = "Software"
            occupationList["e2e3cbed-70ff-4f5e-8347-a61c2e52ae49"] = "Hardware"

            occupationList.forEach { (uuid, name) ->
                service.create(name)
            }
        }
    }

}
