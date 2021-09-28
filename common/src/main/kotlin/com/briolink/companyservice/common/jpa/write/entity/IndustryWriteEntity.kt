package com.briolink.companyservice.common.jpa.write.entity

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table

@Table(name = "industry", catalog = "dev_write_company")
@Entity
class IndustryWriteEntity : BaseWriteEntity() {
    @Column(name = "name", nullable = false)
    lateinit var name: String
}
