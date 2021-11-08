package com.briolink.companyservice.common.jpa.write.entity

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table

@Table(name = "keyword", schema = "write")
@Entity
class KeywordWriteEntity : BaseWriteEntity() {
    @Column(name = "name", nullable = false)
    lateinit var name: String
}
