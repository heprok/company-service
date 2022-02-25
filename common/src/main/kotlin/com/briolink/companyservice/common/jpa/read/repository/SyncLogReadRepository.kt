package com.briolink.companyservice.common.jpa.read.repository

import com.briolink.companyservice.common.jpa.read.entity.SyncLogReadEntity
import com.briolink.lib.sync.ISyncLogRepository
import com.briolink.lib.sync.SyncLogId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

interface SyncLogReadRepository : JpaRepository<SyncLogReadEntity, SyncLogId>, ISyncLogRepository {

    @Query("""SELECT count(1) > 0 FROM SyncLogReadEntity s WHERE s.id.syncId = ?1 AND s.id._objectSync = ?2 AND s.id._service = ?3 AND s.completed IS NULL""") // ktlint-disable max-line-length
    override fun existsNotCompleted(syncId: Int, objectSync: Int, serviceId: Int): Boolean

    @Query("""SELECT s FROM SyncLogReadEntity s WHERE s.id.syncId = ?1 AND s.id._service = ?2""")
    override fun findBySyncIdAndService(syncId: Int, serviceId: Int): List<SyncLogReadEntity>

    @Query("SELECT count(1) > 0 FROM SyncLogReadEntity s WHERE s.id.syncId = ?1 AND s.id._service = ?2 AND s.completed IS NULL OR s.withError = true") // ktlint-disable max-line-length
    override fun existsNotCompleted(syncId: Int, serviceId: Int): Boolean

    @Query("SELECT count(1) > 0 FROM SyncLogReadEntity s WHERE s.id.syncId = ?1 AND s.id._service = ?2")
    override fun existsBySyncIdAndService(syncId: Int, serviceId: Int): Boolean

    /*
     * Method must be @query and @Modify
     * @query must be contain 'ON CONFLICT DO NOTHING'
     */
    @Modifying
    @Query("INSERT INTO read.sync_log(sync_id, service, object_sync) VALUES(?1, ?2, ?3) ON CONFLICT DO NOTHING", nativeQuery = true)
    override fun insert(syncId: Int, serviceId: Int, objectSync: Int)

    @Transactional
    @Modifying
    @Query("UPDATE SyncLogReadEntity s SET s.completed = ?3, s.withError = ?4 WHERE s.id._service = ?2 AND s.id.syncId = ?1")
    override fun update(syncId: Int, serviceId: Int, completed: Instant?, withError: Boolean)

    @Transactional
    @Modifying
    @Query("UPDATE SyncLogReadEntity s SET s.completed = ?4, s.withError = ?5 WHERE s.id.syncId = ?1 AND s.id._objectSync = ?3 AND s.id._service = ?2")
    override fun update(syncId: Int, serviceId: Int, objectSync: Int, completed: Instant?, withError: Boolean)
}
