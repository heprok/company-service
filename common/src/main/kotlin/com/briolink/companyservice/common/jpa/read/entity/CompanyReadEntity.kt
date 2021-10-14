package com.briolink.companyservice.common.jpa.read.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import org.hibernate.annotations.Type
import java.util.UUID
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Table(name = "company", catalog = "schema_read")
@Entity
class CompanyReadEntity(
    @Id
    @Type(type = "uuid-char")
    @Column(name = "id", nullable = false, length = 36)
    val id: UUID,

    @Type(type = "string")
    @Column(name = "slug", nullable = false, length = 50)
    var slug: String
) : BaseReadEntity() {
    @Type(type = "json")
    @Column(name = "data", nullable = false, columnDefinition = "json")
    lateinit var data: Data

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Data(
        @JsonProperty("name")
        var name: String,
        @JsonProperty("website")
        var website: String,
        @JsonProperty("description")
        var description: String? = null,
//        @JsonProperty("country")
//        var country: String? = null,
//        @JsonProperty("state")
//        var state: String? = null,
        @JsonProperty("location")
        var location: String? = null,
        @JsonProperty("logo")
        var logo: String? = null,
        @JsonProperty("isTypePublic")
        var isTypePublic: Boolean = true,
        @JsonProperty("facebook")
        var facebook: String? = null,
        @JsonProperty("twitter")
        var twitter: String? = null,
        @JsonProperty("industry")
        var industry: Industry? = null,
        @JsonProperty("occupation")
        var occupation: Occupation? = null,
        @JsonProperty("statistic")
        var statistic: Statistic = Statistic(),
        @JsonProperty("keyWords")
        var keywords: List<Keyword?> = mutableListOf<Keyword>(),
    )

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Occupation(
        @JsonProperty("id")
        val id: String,
        @JsonProperty("name")
        val name: String,
    )

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Industry(
        @JsonProperty("id")
        val id: String,
        @JsonProperty("name")
        val name: String,
    )

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Keyword(
        @JsonProperty("id")
        val id: UUID,
        @JsonProperty("name")
        val name: String,
    )

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Statistic(
        @JsonProperty("serviceProvidedCount")
        var serviceProvidedCount: Int = 0,
        @JsonProperty("collaboratingCompanyCount")
        var collaboratingCompanyCount: Int = 0,
        @JsonProperty("collaboratingPeopleCount")
        val collaboratingPeopleCount: Int = 0,
        @JsonProperty("totalConnectionCount")
        var totalConnectionCount: Int = 0,
    )
}
