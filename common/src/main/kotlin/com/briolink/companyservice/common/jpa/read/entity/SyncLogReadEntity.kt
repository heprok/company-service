package com.briolink.companyservice.common.jpa.read.entity

import com.briolink.lib.sync.ISyncLogEntity
import com.briolink.lib.sync.SyncLogId
import com.briolink.lib.sync.enumeration.ObjectSyncEnum
import com.briolink.lib.sync.enumeration.ServiceEnum
import java.time.Instant
import javax.persistence.AttributeOverride
import javax.persistence.AttributeOverrides
import javax.persistence.Column
import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.persistence.Table

@Table(name = "sync_log", schema = "read")
@Entity
class SyncLogReadEntity(
    @AttributeOverrides(
        AttributeOverride(name = "syncId", column = Column(name = "sync_id")),
        AttributeOverride(name = "_objectSync", column = Column(name = "object_sync")),
        AttributeOverride(name = "_service", column = Column(name = "service")),
    )
    @EmbeddedId
    override var id: SyncLogId,

    @Column(name = "completed")
    override var completed: Instant? = null,

    @Column(name = "with_error")
    override var withError: Boolean = false

) : BaseReadEntity(), ISyncLogEntity {
    var objectSync: ObjectSyncEnum
        get() = ObjectSyncEnum.fromInt(id._objectSync)
        set(value) {
            id._objectSync = value.value
        }

    var service: ServiceEnum
        get() = ServiceEnum.ofId(id._service)
        set(value) {
            id._service = value.id
        }
}
