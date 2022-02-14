package com.briolink.companyservice.common.jpa.write.entity

import com.briolink.companyservice.common.domain.v1_0.Occupation
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table

@Table(name = "occupation", schema = "write")
@Entity
class OccupationWriteEntity : BaseWriteEntity() {
    @Column(name = "name", nullable = false)
    lateinit var name: String

    @CreationTimestamp
    @Column(name = "created", nullable = false)
    lateinit var created: Instant

    @UpdateTimestamp
    @Column(name = "changed")
    var changed: Instant? = null

    fun toDomain(): Occupation = Occupation(
        id = id!!,
        name = name,
    )
}
