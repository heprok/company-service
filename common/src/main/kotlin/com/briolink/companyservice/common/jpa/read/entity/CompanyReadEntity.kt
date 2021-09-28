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
class CompanyReadEntity {
    @Id
    @Type(type = "uuid-char")
    @Column(name = "id", nullable = false, length = 36)
    lateinit var id: UUID

    @Type(type = "string")
    @Column(name = "slug", nullable = false)
    val slug: String = ""

    @Type(type = "json")
    @Column(name = "data", nullable = false, columnDefinition = "json")
    lateinit var data: Data

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Data(
        @JsonProperty("name")
        val name: String,
        @JsonProperty("website")
        val website: String,
        @JsonProperty("about")
        val about: String,
        @JsonProperty("country")
        val country: String,
        @JsonProperty("state")
        val state: String,
        @JsonProperty("logo")
        val logo: String,
        @JsonProperty("isTypePublic")
        val isTypePublic: Boolean?,
        @JsonProperty("city")
        val city: String,
        @JsonProperty("facebook")
        val facebook: String,
        @JsonProperty("twitter")
        val twitter: String,
        @JsonProperty("industry")
        val industry: Industry?,
        @JsonProperty("occupation")
        val occupation: Occupation?,
        @JsonProperty("statistic")
        val statistic: Statistic,
        @JsonProperty("keyWords")
        val keywords: List<Keyword?>,
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
        val serviceProvidedCount: Int,
        @JsonProperty("collaboratingCompanyCount")
        val collaboratingCompanyCount: Int,
        @JsonProperty("collaboratingPeopleCount")
        val collaboratingPeopleCount: Int,
    )


}
