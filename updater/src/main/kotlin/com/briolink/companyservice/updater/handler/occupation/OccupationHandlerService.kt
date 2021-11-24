package com.briolink.companyservice.updater.handler.occupation

import com.briolink.companyservice.common.domain.v1_0.Occupation
import com.briolink.companyservice.common.jpa.read.entity.OccupationReadEntity
import com.briolink.companyservice.common.jpa.read.repository.OccupationReadRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional
@Service
class OccupationHandlerService(
    private val occupationReadRepository: OccupationReadRepository
) {
    fun create(occupation: Occupation) {
        occupationReadRepository.save(
            OccupationReadEntity(id = occupation.id, name = occupation.name),
        )
    }
}
