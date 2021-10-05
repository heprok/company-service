package com.briolink.companyservice.common.jpa.write.entity

import com.fasterxml.jackson.databind.JsonNode
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.Type
import java.time.Instant
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table

@Table(name = "event_store", catalog = "schema_write")
@Entity
class EventStoreWriteEntity(
    @Column(name = "data", nullable = false)
    val data: String,
    @Column(name = "created", nullable = false)
    var created: Instant

) : BaseWriteEntity() {
}
