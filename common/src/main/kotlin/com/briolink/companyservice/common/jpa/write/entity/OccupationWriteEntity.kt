package com.briolink.companyservice.common.jpa.write.entity

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table

@Table(name = "occupation", catalog = "schema_write")
@Entity
class OccupationWriteEntity() : BaseWriteEntity() {
    @Column(name = "name", nullable = false)
    lateinit var name: String
}
