package com.briolink.companyservice.api.service

import com.briolink.companyservice.common.jpa.write.repository.KeywordWriteRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class KeywordService(
    private val keywordWriteRepository: KeywordWriteRepository
) {
    fun findById(id: UUID) = keywordWriteRepository.findById(id)
}
