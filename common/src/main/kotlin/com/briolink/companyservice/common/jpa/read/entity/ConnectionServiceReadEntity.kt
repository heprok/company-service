package com.briolink.companyservice.common.jpa.read.entity

import org.hibernate.annotations.Type
import java.io.Serializable
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.IdClass
import javax.persistence.Table

@Table(name = "connection_service", catalog = "schema_read")
@Entity
@IdClass(ConnectionServicePK::class)
class ConnectionServiceReadEntity(
    @Id
    @Type(type = "uuid-char")
    @Column(name = "connection_id", nullable = false, length = 36)
    var connectionId: UUID,

    @Id
    @Type(type = "uuid-char")
    @Column(name = "company_id", nullable = false, length = 36)
    var companyId: UUID,

    @Id
    @Type(type = "uuid-char")
    @Column(name = "service_id", nullable = false, length = 36)
    var serviceId: UUID,

    @Column(name = "service_name", nullable = false, length = 255)
    var serviceName: String
) : BaseReadEntity()

class ConnectionServicePK(

) : Serializable {
    lateinit var connectionId: UUID
    lateinit var companyId: UUID
    lateinit var serviceId: UUID
}
