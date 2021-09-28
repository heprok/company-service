package com.briolink.companyservice.common.jpa.read.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import org.hibernate.annotations.Type
import java.time.LocalDate
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Table(name = "service", catalog = "dev_read_company")
@Entity
class ServiceReadEntity(
    @Id
    @Type(type = "uuid-char")
    @Column(name = "id", nullable = false, length = 36)
    var id: UUID,

    @Type(type = "uuid-char")
    @Column(name = "company_id", nullable = false, length = 36)
    var companyId: UUID,

    @Type(type = "json")
    @Column(name = "data", nullable = false, columnDefinition = "json")
    var data: Data
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Data(
        @JsonProperty("name")
        val name: String,
        @JsonProperty("price")
        val price: Double,
        @JsonProperty("image")
        val image: String,
        @JsonProperty("verifiedUses")
        val verifiedUses: Int,
        @JsonProperty("industriesUsed")
        val industriesUsed: List<Industry?>,
        @JsonProperty("created")
        val created: LocalDate
    )

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Industry(
        @JsonProperty("id")
        val id: String,
        @JsonProperty("name")
        val name: String,
    )
}
