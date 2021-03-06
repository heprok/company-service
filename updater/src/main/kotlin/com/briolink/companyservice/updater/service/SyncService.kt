package com.briolink.companyservice.updater.service

import com.briolink.companyservice.common.jpa.read.repository.SyncLogReadRepository
import com.briolink.lib.sync.BaseSyncService
import com.briolink.lib.sync.SyncLogId
import com.briolink.lib.sync.SyncWebClient
import com.briolink.lib.sync.enumeration.ObjectSyncEnum
import com.briolink.lib.sync.enumeration.ServiceEnum
import com.briolink.lib.sync.enumeration.UpdaterEnum
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional
@Service
class SyncService(
    override val syncWebClient: SyncWebClient,
    override val syncLogRepository: SyncLogReadRepository
) : BaseSyncService() {
    override val CURRENT_UPDATER: UpdaterEnum = UpdaterEnum.Company
    override fun getListSyncLogIdAtCompany(syncId: Int): List<SyncLogId> =
        listOf(
            SyncLogId().apply {
                this.syncId = syncId
                this._service = ServiceEnum.Company.id
                this._objectSync = ObjectSyncEnum.Company.value
            },
            SyncLogId().apply {
                this.syncId = syncId
                this._service = ServiceEnum.Company.id
                this._objectSync = ObjectSyncEnum.CompanyIndustry.value
            },
            SyncLogId().apply {
                this.syncId = syncId
                this._service = ServiceEnum.Company.id
                this._objectSync = ObjectSyncEnum.CompanyOccupation.value
            },
            SyncLogId().apply {
                this.syncId = syncId
                this._service = ServiceEnum.Company.id
                this._objectSync = ObjectSyncEnum.CompanyKeyword.value
            },
        )

    override fun getListSyncLogIdAtUser(syncId: Int): List<SyncLogId> =
        listOf(
            SyncLogId().apply {
                this.syncId = syncId
                this._service = ServiceEnum.User.id
                this._objectSync = ObjectSyncEnum.User.value
            },
            SyncLogId().apply {
                this.syncId = syncId
                this._service = ServiceEnum.User.id
                this._objectSync = ObjectSyncEnum.UserJobPosition.value
            },
        )

    override fun getListSyncLogIdAtConnection(syncId: Int): List<SyncLogId> =
        listOf(
            SyncLogId().apply {
                this.syncId = syncId
                this._service = ServiceEnum.Connection.id
                this._objectSync = ObjectSyncEnum.Connection.value
            },
            SyncLogId().apply {
                this.syncId = syncId
                this._service = ServiceEnum.Connection.id
                this._objectSync = ObjectSyncEnum.Project.value
            },
        )

    override fun getListSyncLogIdAtCompanyService(syncId: Int): List<SyncLogId> =
        listOf(
            SyncLogId().apply {
                this.syncId = syncId
                this._service = ServiceEnum.CompanyService.id
                this._objectSync = ObjectSyncEnum.CompanyService.value
            },
        )

    override fun getListSyncLogIdAtExpVerification(syncId: Int): List<SyncLogId> =
        listOf(
            SyncLogId().apply {
                this.syncId = syncId
                this._service = ServiceEnum.ExpVerification.id
                this._objectSync = ObjectSyncEnum.ExpVerification.value
            },
        )
}
