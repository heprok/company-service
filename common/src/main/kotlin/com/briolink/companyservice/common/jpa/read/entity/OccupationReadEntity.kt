package com.briolink.companyservice.common.jpa.read.entity

import org.hibernate.annotations.Type
import java.util.*
import java.util.UUID.randomUUID
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Table(name = "occupation", schema = "read")
@Entity
class OccupationReadEntity(
    @Id
    @Type(type = "pg-uuid")
    @Column(name = "id", nullable = false)
    var id: UUID,

    @Column(name = "name", nullable = false, length = 128)
    var name: String
) : BaseReadEntity() {}
