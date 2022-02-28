package com.briolink.companyservice.common.jpa.write.entity

import com.briolink.companyservice.common.domain.v1_0.Keyword
import org.hibernate.annotations.CreationTimestamp
import java.time.Instant
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table

@Table(name = "keyword", schema = "write")
@Entity
class KeywordWriteEntity : BaseWriteEntity() {
    @Column(name = "name", nullable = false)
    lateinit var name: String

    @CreationTimestamp
    @Column(name = "created", nullable = false)
    lateinit var created: Instant

    fun toDomain(): Keyword = Keyword(
        id = id!!,
        name = name,
    )
}
