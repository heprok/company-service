package com.briolink.companyservice.common.jpa.read.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.JsonNode
import org.hibernate.annotations.Type
import java.time.LocalDate
import java.util.UUID
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Table(name = "company", catalog = "dev_read_company")
@Entity
class CompanyReadEntity(
    @Id
    @Type(type = "uuid-char")
    @Column(name = "id", nullable = false, length = 36)
    val id: UUID
) : BaseReadEntity() {
    @Type(type = "string")
    @Column(name = "slug", nullable = false)
    var slug: String = ""

    @Type(type = "json")
    @Column(name = "data", nullable = false, columnDefinition = "json")
    lateinit var data: Data

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Data(
        @JsonProperty("name")
        var name: String,
        @JsonProperty("website")
        var website: String,
        @JsonProperty("about")
        var about: String? = null,
        @JsonProperty("country")
        var country: String? = null,
        @JsonProperty("state")
        var state: String? = null,
        @JsonProperty("logo")
        var logo: String? = null,
        @JsonProperty("isTypePublic")
        var isTypePublic: Boolean = true,
        @JsonProperty("city")
        var city: String? = null,
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
        val id: String,
        @JsonProperty("name")
        val name: String,
    )

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Statistic(
        @JsonProperty("serviceProvidedCount")
        val serviceProvidedCount: Int = 0,
        @JsonProperty("collaboratingCompanyCount")
        val collaboratingCompanyCount: Int = 0,
        @JsonProperty("collaboratingPeopleCount")
        val collaboratingPeopleCount: Int = 0,
        @JsonProperty("totalConnectionCount")
        val totalConnectionCount: Int = 0,
    )
}
