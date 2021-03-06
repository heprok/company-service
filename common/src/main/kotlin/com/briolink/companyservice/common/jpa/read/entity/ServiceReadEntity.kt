package com.briolink.companyservice.common.jpa.read.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import org.hibernate.annotations.Type
import java.net.URL
import java.time.LocalDate
import java.util.UUID
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Table(name = "service", schema = "read")
@Entity
class ServiceReadEntity(
    @Id
    @Type(type = "pg-uuid")
    @Column(name = "id", nullable = false)
    var id: UUID,
) : BaseReadEntity() {

    @Type(type = "pg-uuid")
    @Column(name = "company_id", nullable = false)
    lateinit var companyId: UUID

    @Type(type = "jsonb")
    @Column(name = "data", nullable = false, columnDefinition = "jsonb")
    lateinit var data: Data

    @Column(name = "name", nullable = false, length = 255)
    lateinit var name: String

    @Column(name = "verified_uses", nullable = false)
    var verifiedUses: Int = 0

    @Column(name = "price")
    var price: Double? = null

    @Column(name = "last_used")
    var lastUsed: LocalDate? = null

    @Column(name = "created")
    var created: LocalDate? = LocalDate.now()

    @Column(name = "is_hidden")
    var hidden: Boolean = false

    @Column(name = "is_deleted")
    var deleted: Boolean = false

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Data(
        @JsonProperty
        var logo: URL? = null,
        @JsonProperty
        var slug: String,
    )
}
