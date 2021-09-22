package com.briolink.companyservice.common.jpa.write.entity

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table

@Table(name = "key_word", catalog = "test_write_company")
@Entity
class KeyWordWriteEntity : BaseWriteEntity() {
    @Column(name = "name", nullable = false)
    lateinit var name: String
}
