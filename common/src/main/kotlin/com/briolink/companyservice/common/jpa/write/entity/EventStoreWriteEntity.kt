package com.briolink.companyservice.common.jpa.write.entity

import com.fasterxml.jackson.databind.JsonNode
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.Type
import java.time.Instant
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table

@Table(name = "event_store", catalog = "test_write_company")
@Entity
class EventStoreWriteEntity : BaseWriteEntity() {
    @Type(type = "com.vladmihalcea.hibernate.type.json.JsonNodeStringType")
    @Column(name = "data", nullable = false)
    lateinit var data: JsonNode

    @CreationTimestamp
    @Column(name = "created", nullable = false)
    lateinit var created: Instant
}
