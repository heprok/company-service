package com.briolink.companyservice.common.jpa.read.entity

import org.hibernate.annotations.Type
import java.util.UUID
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Table(name = "connection_service", schema = "read")
@Entity
class ConnectionServiceReadEntity : BaseReadEntity() {
    @Id
    @Type(type = "pg-uuid")
    @Column(name = "id", nullable = false)
    var id: UUID? = null

    @Type(type = "pg-uuid")
    @Column(name = "company_id", nullable = false)
    lateinit var companyId: UUID

    @Type(type = "pg-uuid")
    @Column(name = "service_id")
    var serviceId: UUID? = null

    @Column(name = "name", nullable = false, length = 200)
    lateinit var name: String
}
