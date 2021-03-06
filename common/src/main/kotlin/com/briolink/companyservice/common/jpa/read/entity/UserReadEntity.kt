package com.briolink.companyservice.common.jpa.read.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import org.hibernate.annotations.Type
import java.net.URL
import java.util.UUID
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Table(name = "user", schema = "read")
@Entity
class UserReadEntity(
    @Id
    @Type(type = "pg-uuid")
    @Column(name = "id", nullable = false)
    var id: UUID,
) : BaseReadEntity() {
    @Type(type = "jsonb")
    @Column(name = "data", nullable = false, columnDefinition = "jsonb")
    lateinit var data: Data

    val fullName: String
        get() = "${data.firstName} ${data.lastName}"

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Data(
        @JsonProperty
        var firstName: String,
        @JsonProperty
        var lastName: String,
        @JsonProperty
        var image: URL? = null,
    ) {
        @JsonProperty
        lateinit var slug: String
    }

    data class BaseUser(
        @JsonProperty
        val id: UUID,
        @JsonProperty
        val pbId: String? = null,
        @JsonProperty
        val firstName: String,
        @JsonProperty
        val lastName: String,
        @JsonProperty
        val image: URL? = null,
        @JsonProperty
        val slug: String,
    ) {
        val fullName: String
            get() = "$firstName $lastName"
    }
}
