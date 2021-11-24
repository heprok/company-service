package com.briolink.companyservice.common.jpa.write.entity

import java.time.Instant
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table
import org.hibernate.annotations.Type

@Table(name = "event_store", schema = "write")
@Entity
class EventStoreWriteEntity(
    @Type(type = "jsonb")
    @Column(name = "data", nullable = false, columnDefinition = "jsonb")
    val data: String,
    @Column(name = "created", nullable = false)
    var created: Instant

) : BaseWriteEntity() {
}
