package com.briolink.companyservice.common.jpa.read.entity

import org.hibernate.annotations.Type
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Table(name = "company", catalog = "schema_read")
@Entity
class KeywordReadEntity() : BaseReadEntity() {
    @Id
    @Type(type = "uuid-char")
    @Column(name = "id", nullable = false, length = 36)
    var id: UUID? = null

    @Type(type = "string")
    @Column(name = "name", nullable = false, length = 128, columnDefinition = "string")
    lateinit var name: String
}
