package com.briolink.companyservice.api.service

import com.briolink.companyservice.common.jpa.read.entity.OccupationReadEntity
import com.briolink.companyservice.common.jpa.read.repository.OccupationReadRepository
import com.briolink.companyservice.common.jpa.write.entity.OccupationWriteEntity
import com.briolink.companyservice.common.jpa.write.repository.OccupationWriteRepository
import com.briolink.companyservice.common.util.PageRequest
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class OccupationService(
    private val occupationReadRepository: OccupationReadRepository,
    private val occupationWriteRepository: OccupationWriteRepository
) {
    fun createOccupation(occupation: OccupationWriteEntity): OccupationWriteEntity = occupationWriteRepository.save(occupation)
    fun updateOccupation(occupation: OccupationWriteEntity) = occupationWriteRepository.save(occupation)
    fun deleteOccupation(id: UUID) = occupationWriteRepository.deleteById(id)
    fun findById(id: UUID) = occupationWriteRepository.findById(id)
}
