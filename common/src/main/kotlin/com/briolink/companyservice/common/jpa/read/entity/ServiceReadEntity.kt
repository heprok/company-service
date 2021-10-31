package com.briolink.companyservice.common.jpa.read.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import org.hibernate.annotations.Type
import java.net.URL
import java.time.LocalDate
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Table(name = "service", catalog = "schema_read")
@Entity
class ServiceReadEntity(
    @Id
    @Type(type = "uuid-char")
    @Column(name = "id", nullable = false, length = 36)
    var id: UUID,

    @Type(type = "uuid-char")
    @Column(name = "company_id", nullable = false, length = 36)
    var companyId: UUID,

    @Column(name = "name", nullable = false, length = 255)
    var name: String,

    @Column(name = "verified_uses", nullable = false)
    var verifiedUses: Int = 0,

    @Column(name = "price", nullable = false)
    var price: Double? = null,

    @Column(name = "last_used")
    var lastUsed: LocalDate? = null,

    @Column(name = "created")
    var created: LocalDate? = null,

    @Column(name = "is_hide")
    var isHide: Boolean = false,

    @Type(type = "json")
    @Column(name = "data", nullable = false, columnDefinition = "json")
    var data: Data
) : BaseReadEntity() {
    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Data(
        @JsonProperty("logo")
        var logo: URL? = null,
        @JsonProperty("slug")
        var slug: String,
//        @JsonProperty("created")
//        var created: LocalDate
    )

//    @JsonIgnoreProperties(ignoreUnknown = true)
//    data class Industry(
//        @JsonProperty("id")
//        val id: String,
//        @JsonProperty("name")
//        val name: String,
//    )
}
