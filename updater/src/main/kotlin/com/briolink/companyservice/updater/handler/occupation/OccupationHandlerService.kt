package com.briolink.companyservice.updater.handler.occupation

import com.briolink.companyservice.common.domain.v1_0.Occupation
import com.briolink.companyservice.common.jpa.read.entity.OccupationReadEntity
import com.briolink.companyservice.common.jpa.read.repository.OccupationReadRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Transactional
@Service
class OccupationHandlerService(
    private val occupationReadRepository: OccupationReadRepository
) {
    fun createOrUpdate(
        entityPrevOccupation: OccupationReadEntity? = null,
        keywordEventData: Occupation
    ): OccupationReadEntity {
        val occupation = entityPrevOccupation ?: OccupationReadEntity(keywordEventData.id, keywordEventData.name)
        return occupationReadRepository.save(occupation)
    }

    fun findById(id: UUID) = occupationReadRepository.findByIdOrNull(id)
}
