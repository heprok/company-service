package com.briolink.companyservice.updater.handler.keyword

import com.briolink.companyservice.common.domain.v1_0.Keyword
import com.briolink.companyservice.common.jpa.read.entity.KeywordReadEntity
import com.briolink.companyservice.common.jpa.read.repository.KeywordReadRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Transactional
@Service
class KeywordHandlerService(
    private val keywordReadRepository: KeywordReadRepository
) {
    fun createOrUpdate(
        entityPrevKeyword: KeywordReadEntity? = null,
        keywordEventData: Keyword
    ): KeywordReadEntity {
        val keyword = entityPrevKeyword ?: KeywordReadEntity(keywordEventData.id, keywordEventData.name)
        return keywordReadRepository.save(keyword)
    }

    fun findById(id: UUID) = keywordReadRepository.findByIdOrNull(id)
}
