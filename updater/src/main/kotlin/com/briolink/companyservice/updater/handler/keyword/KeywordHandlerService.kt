package com.briolink.companyservice.updater.handler.keyword

import com.briolink.companyservice.common.domain.v1_0.Keyword
import com.briolink.companyservice.common.jpa.read.entity.KeywordReadEntity
import com.briolink.companyservice.common.jpa.read.repository.KeywordReadRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional
@Service
class KeywordHandlerService(
    private val keywordReadRepository: KeywordReadRepository
) {
    fun create(keyword: Keyword) {
        keywordReadRepository.save(
                KeywordReadEntity(id = keyword.id, name = keyword.name),
        )
    }

}

