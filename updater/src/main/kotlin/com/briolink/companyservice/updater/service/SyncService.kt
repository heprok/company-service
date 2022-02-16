package com.briolink.companyservice.updater.service

import com.briolink.companyservice.common.jpa.enumeration.ObjectSyncEnum
import com.briolink.companyservice.common.jpa.read.entity.SyncLogPK
import com.briolink.companyservice.common.jpa.read.entity.SyncLogReadEntity
import com.briolink.companyservice.common.jpa.read.repository.SyncLogReadRepository
import com.briolink.lib.sync.SyncWebClient
import com.briolink.lib.sync.enumeration.ServiceEnum
import com.briolink.lib.sync.enumeration.UpdaterEnum
import com.briolink.lib.sync.model.SyncError
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import javax.persistence.EntityNotFoundException

@Transactional
@Service
class SyncService(
    private val syncWebClient: SyncWebClient,
    private val syncLogReadRepository: SyncLogReadRepository
) {
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun startSync(syncId: Int, service: ServiceEnum): Boolean {
        if (syncLogReadRepository.existsBySyncIdAndServiceId(syncId, service.id)) {
            return false
        }
        when (service) {
            ServiceEnum.User -> startSyncAtUser(syncId)
            ServiceEnum.Company -> startSyncAtCompany(syncId)
            ServiceEnum.CompanyService -> startSyncAtCompanyService(syncId)
            ServiceEnum.Connection -> startSyncAtConnection(syncId)
        }
        return true
    }

    fun sendSyncError(syncError: SyncError) {
        syncLogReadRepository.findBySyncIdAndServiceId(syncError.syncId, syncError.service.id).forEach {
            it.completed = Instant.now()
            it.withError = true
            syncLogReadRepository.save(it)
        }

        syncWebClient.sendSyncErrorAtUpdater(syncError)
    }

    fun completedObjectSync(syncId: Int, service: ServiceEnum, objectSyncEnum: ObjectSyncEnum) {
        val pk = SyncLogPK().apply {
            this.syncId = syncId
            this._service = service.id
            this._objectSync = objectSyncEnum.value
        }
        syncLogReadRepository.findById(pk).orElseThrow { throw EntityNotFoundException("Sync not found with $pk") }
            .apply {
                completed = Instant.now()
                syncLogReadRepository.save(this)
            }

        if (!syncLogReadRepository.existsNotCompletedAndOrWithErrorBySyncIdAndServiceId(syncId, service.id))
            syncWebClient.sendCompletedSyncAtUpdater(updater = UpdaterEnum.Company, service = service)
    }

    private fun startSync(listSyncPK: List<SyncLogPK>): List<SyncLogReadEntity> {
        listSyncPK.forEach {
            if (syncLogReadRepository.existsNotCompletedBySyncIdAndObjectSyncAndServiceId(
                    it.syncId,
                    it._objectSync,
                    it._service
                )
            )
                throw RuntimeException("Sync already started")
        }
        val listSyncLog = mutableListOf<SyncLogReadEntity>()
        listSyncPK.forEach { pk ->
            SyncLogReadEntity(pk.syncId, pk._objectSync, pk._service).also {
                listSyncLog.add(syncLogReadRepository.save(it))
            }
        }
        return listSyncLog
    }

    private fun startSyncAtCompany(syncId: Int): List<SyncLogReadEntity> {
        val listSyncLogPK =
            listOf(
                SyncLogPK().apply {
                    this.syncId = syncId
                    this._service = ServiceEnum.Company.id
                    this._objectSync = ObjectSyncEnum.Company.value
                },
                SyncLogPK().apply {
                    this.syncId = syncId
                    this._service = ServiceEnum.Company.id
                    this._objectSync = ObjectSyncEnum.CompanyIndustry.value
                },
                SyncLogPK().apply {
                    this.syncId = syncId
                    this._service = ServiceEnum.Company.id
                    this._objectSync = ObjectSyncEnum.CompanyOccupation.value
                },
                SyncLogPK().apply {
                    this.syncId = syncId
                    this._service = ServiceEnum.Company.id
                    this._objectSync = ObjectSyncEnum.CompanyKeyword.value
                },
            )
        return startSync(listSyncLogPK)
    }

    private fun startSyncAtUser(syncId: Int): List<SyncLogReadEntity> {
        val listSyncLogPK =
            listOf(
                SyncLogPK().apply {
                    this.syncId = syncId
                    this._service = ServiceEnum.User.id
                    this._objectSync = ObjectSyncEnum.User.value
                },
                SyncLogPK().apply {
                    this.syncId = syncId
                    this._service = ServiceEnum.User.id
                    this._objectSync = ObjectSyncEnum.UserJobPosition.value
                }
            )
        return startSync(listSyncLogPK)
    }

    private fun startSyncAtConnection(syncId: Int): List<SyncLogReadEntity> {
        val listSyncLogPK =
            listOf(
                SyncLogPK().apply {
                    this.syncId = syncId
                    this._service = ServiceEnum.Connection.id
                    this._objectSync = ObjectSyncEnum.Connection.value
                }
            )
        return startSync(listSyncLogPK)
    }

    private fun startSyncAtCompanyService(syncId: Int): List<SyncLogReadEntity> {
        val listSyncLogPK =
            listOf(
                SyncLogPK().apply {
                    this.syncId = syncId
                    this._service = ServiceEnum.CompanyService.id
                    this._objectSync = ObjectSyncEnum.CompanyService.value
                }
            )
        return startSync(listSyncLogPK)
    }
}
