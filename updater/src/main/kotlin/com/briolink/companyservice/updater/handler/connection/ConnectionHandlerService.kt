package com.briolink.companyservice.updater.handler.connection

import com.briolink.companyservice.common.jpa.enumeration.ConnectionObjectTypeEnum
import com.briolink.companyservice.common.jpa.read.entity.ConnectionReadEntity
import com.briolink.companyservice.common.jpa.read.repository.ConnectionReadRepository
import com.briolink.companyservice.updater.RefreshStatisticConnectionByCompanyId
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional
class ConnectionHandlerService(
    private val connectionReadRepository: ConnectionReadRepository,
    private val applicationEventPublisher: ApplicationEventPublisher
) {
    fun createOrUpdate(domain: ConnectionEventData, updateStats: Boolean = true) {
        var updateStatistic = false

        if (domain.status == ConnectionStatus.Connected &&
            domain.fromObjectType == ConnectionObjectType.Company || domain.toObjectType == ConnectionObjectType.Company
        ) {
            connectionReadRepository.findByFromObjectIdAndToObjectId(domain.fromObjectId, domain.toObjectId!!)
                .orElse(ConnectionReadEntity())
                .apply {
                    updateStatistic = if (this.id == null) true
                    else this.hidden != domain.hidden

                    this.toObjectId = domain.toObjectId
                    this.fromObjectId = domain.fromObjectId
                    this.fromObjectType = ConnectionObjectTypeEnum.ofValue(domain.fromObjectType.value)
                    this.toObjectType = ConnectionObjectTypeEnum.ofValue(domain.toObjectType.value)
                    this.hidden = domain.hidden

                    connectionReadRepository.save(this)
                }
        } else domain.toObjectId?.let {
            updateStatistic = connectionReadRepository.deleteBy(domain.fromObjectId, it) > 0
        }

        if (updateStats && updateStatistic) {
            if (domain.fromObjectType == ConnectionObjectType.Company)
                refreshConnectionStatistic(domain.fromObjectId)

            if (domain.toObjectType == ConnectionObjectType.Company)
                domain.toObjectId?.also { refreshConnectionStatistic(it) }
        }
    }

    fun refreshConnectionStatistic(companyId: UUID) {
        applicationEventPublisher.publishEvent(RefreshStatisticConnectionByCompanyId(companyId))
    }

    fun refreshAllConnectionStatistic() {
        val limit = 5000
        var offset = 0
        while (true) {
            val companyIds =
                connectionReadRepository.getAllCompanyIds(limit, offset)
            if (companyIds.isEmpty()) break
            companyIds.forEach {
                refreshConnectionStatistic(it.companyId)
            }
            offset += limit
        }
    }
}
