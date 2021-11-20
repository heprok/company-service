package com.briolink.companyservice.common.jpa.read.entity

import com.briolink.companyservice.common.jpa.dto.location.LocationInfoDto
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import org.hibernate.annotations.Type
import java.net.URL
import java.util.UUID
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Table(name = "company", schema = "read")
@Entity
class CompanyReadEntity(
    @Id
    @Type(type = "pg-uuid")
    @Column(name = "id", nullable = false)
    val id: UUID,

    @Column(name = "slug", nullable = false, length = 50)
    var slug: String,

    @Column(name = "name", nullable = false, length = 255)
    var name: String

) : BaseReadEntity() {
    @Type(type = "jsonb")
    @Column(name = "data", nullable = false, columnDefinition = "jsonb")
    lateinit var data: Data

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Data(
        @JsonProperty
        var name: String,
        @JsonProperty
        var website: URL? = null,
        @JsonProperty
        var description: String? = null,
        @JsonProperty
        var location: LocationInfoDto? = null,
        @JsonProperty
        var logo: URL? = null,
        @JsonProperty
        var isTypePublic: Boolean = true,
        @JsonProperty
        var facebook: String? = null,
        @JsonProperty
        var twitter: String? = null,
        @JsonProperty
        var industry: Industry? = null,
        @JsonProperty
        var occupation: Occupation? = null,
        @JsonProperty
        var keywords: List<Keyword?> = mutableListOf<Keyword>(),
    )

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Occupation(
        @JsonProperty
        val id: String,
        @JsonProperty
        val name: String,
    )

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Industry(
        @JsonProperty
        val id: UUID,
        @JsonProperty
        val name: String,
    )

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Keyword(
        @JsonProperty
        val id: UUID,
        @JsonProperty
        val name: String,
    )
}
