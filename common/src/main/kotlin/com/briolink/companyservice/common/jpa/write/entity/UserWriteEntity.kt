package com.briolink.companyservice.common.jpa.write.entity

import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table


@Table(name = "user", catalog = "test_write_company")
@Entity
class UserWriteEntity : BaseWriteEntity() {
    @Column(name = "first_name", nullable = false)
    lateinit var firstName: String

    @Column(name = "last_name", nullable = false)
    lateinit var lastName: String

    @Column(name = "image")
    var image: String? = null

    @ManyToOne(cascade = [CascadeType.PERSIST])
    @JoinColumn(name = "company_id", nullable = false)
    lateinit var company: CompanyWriteEntity

    @CreationTimestamp
    @Column(name = "created", nullable = false)
    lateinit var created: Instant

    @UpdateTimestamp
    @Column(name = "changed")
    var changed: Instant? = null
}
