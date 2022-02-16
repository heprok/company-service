package com.briolink.companyservice.common.jpa.read.entity

import com.briolink.companyservice.common.jpa.enumeration.ObjectSyncEnum
import com.briolink.lib.sync.enumeration.ServiceEnum
import java.io.Serializable
import java.time.Instant
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.IdClass
import javax.persistence.Table

class SyncLogPK() : Serializable {
    var syncId: Int = 0
    var _objectSync: Int = 0
    var _service: Int = 0
}

@Table(name = "sync_log", schema = "read")
@Entity
@IdClass(SyncLogPK::class)
class SyncLogReadEntity(
    @Id
    @Column(name = "sync_id", nullable = false)
    var syncId: Int,

    @Id
    @Column(name = "object_sync")
    private var _objectSync: Int,

    @Id
    @Column(name = "service")
    private var _service: Int,

    @Column(name = "completed")
    var completed: Instant? = null,

    @Column(name = "with_error")
    var withError: Boolean = false

) : BaseReadEntity() {
    var objectSync: ObjectSyncEnum
        get() = ObjectSyncEnum.fromInt(_objectSync)
        set(value) {
            _objectSync = value.value
        }

    var service: ServiceEnum
        get() = ServiceEnum.ofId(_service)
        set(value) {
            _service = value.id
        }
}
