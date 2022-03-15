package com.briolink.companyservice.common.jpa.write.entity

import com.briolink.companyservice.common.domain.v1_0.Industry
import org.hibernate.annotations.CreationTimestamp
import java.time.Instant
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table

@Table(name = "industry", schema = "write")
@Entity
class IndustryWriteEntity : BaseWriteEntity() {
    @Column(name = "name", nullable = false)
    lateinit var name: String

    @CreationTimestamp
    @Column(name = "created", nullable = false)
    lateinit var created: Instant

    fun toDomain(): Industry = Industry(
        id = id!!,
        name = name,
    )
}
