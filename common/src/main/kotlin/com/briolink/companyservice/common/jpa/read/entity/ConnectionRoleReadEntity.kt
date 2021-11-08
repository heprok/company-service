package com.briolink.companyservice.common.jpa.read.entity

import com.fasterxml.jackson.annotation.JsonFormat
import org.hibernate.annotations.Type
import java.util.*
import java.util.UUID.randomUUID
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

class ConnectionRoleReadEntity(
    @Id
    @Type(type = "pg-uuid")
    @Column(name = "id", nullable = false)
    var id: UUID,

    @Column(name = "name", nullable = false, length = 128)
    var name: String,

    @Column(name = "type", nullable = false)
    var type: RoleType,

) : BaseReadEntity() {
    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    enum class RoleType(val value: Int) {
            Buyer(0),
            Seller(1)
    }

}
