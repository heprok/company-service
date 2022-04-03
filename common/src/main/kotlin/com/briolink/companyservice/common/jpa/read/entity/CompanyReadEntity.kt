package com.briolink.companyservice.common.jpa.read.entity

import com.briolink.lib.location.model.LocationMinInfo
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
    var name: String,

    @Type(type = "pg-uuid")
    @Column(name = "created_by", nullable = false)
    var createdBy: UUID

) : BaseReadEntity() {
    @Type(type = "jsonb")
    @Column(name = "data", nullable = false, columnDefinition = "jsonb")
    lateinit var data: Data

    data class Data(
        @JsonProperty
        var name: String,
        @JsonProperty
        var website: URL? = null,
        @JsonProperty
        var description: String? = null,
        @JsonProperty
        var location: LocationMinInfo? = null,
        @JsonProperty
        var logo: URL? = null,
        @JsonProperty
        var isTypePublic: Boolean = true,
        @JsonProperty
        var facebook: String? = null,
        @JsonProperty
        var shortDescription: String? = null,
        @JsonProperty
        var twitter: String? = null,
        @JsonProperty
        var industry: Industry? = null,
        @JsonProperty
        var occupation: Occupation? = null,
        @JsonProperty
        var keywords: List<Keyword?> = mutableListOf<Keyword>(),
    )

    data class Occupation(
        @JsonProperty
        val id: UUID,
        @JsonProperty
        val name: String,
    )

    data class Industry(
        @JsonProperty
        val id: UUID,
        @JsonProperty
        val name: String,
    )

    data class Keyword(
        @JsonProperty
        val id: UUID,
        @JsonProperty
        val name: String,
    )
}
