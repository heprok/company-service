package com.briolink.companyservice.common.jpa.read.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.JsonNode
import org.hibernate.annotations.Type
import java.net.URL
import java.time.LocalDate
import java.util.UUID
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Table(name = "user", catalog = "dev_read_company")
@Entity
class UserReadEntity(
    @Id
    @Type(type = "uuid-char")
    @Column(name = "id", nullable = false, length = 36)
    var id: UUID,

    @Type(type = "uuid-char")
    @Column(name = "company_id", length = 36)
    var companyId: UUID? = null,

    @Type(type = "json")
    @Column(name = "data", nullable = false, columnDefinition = "json")
    var data: Data
) : BaseReadEntity() {

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Data(
        @JsonProperty("firstName")
        var firstName: String,
        @JsonProperty("slug")
        var slug: String,
        @JsonProperty("lastName")
        var lastName: String,
        @JsonProperty("jobPosition")
        var jobPosition: String? = null,
        @JsonProperty("image")
        var image: URL? = null,
    )

}
