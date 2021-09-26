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

@Table(name = "company", catalog = "test_read_company")
@Entity
class CompanyReadEntity {
    @Id
    @Type(type = "uuid-char")
    @Column(name = "id", nullable = false, length = 36)
    lateinit var id: UUID

    @Type(type = "json")
    @Column(name = "data", nullable = false, columnDefinition = "json")
    lateinit var data: Data

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Data(
        @JsonProperty("name")
        val name: String,
        @JsonProperty("website")
        val website: String,
        @JsonProperty("logo")
        val logo: String,
        @JsonProperty("about")
        val about: String,
        @JsonProperty("country")
        val country: String,
        @JsonProperty("state")
        val state: String,
        @JsonProperty("isTypePublic")
        val isTypePublic: Boolean?,
        @JsonProperty("city")
        val city: String,
        @JsonProperty("industry")
        val industry: Industry?,
        @JsonProperty("occupation")
        val occupation: Occupation?,
        @JsonProperty("statistic")
        val statistic: Statistic,
        @JsonProperty("keyWords")
        val keyWords: List<KeyWord?>,
        @JsonProperty("socialProfiles")
        val socialProfiles: List<SocialProfile?>,
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
    data class KeyWord(
        @JsonProperty("id")
        val id: String,
        @JsonProperty("name")
        val name: String,
    )

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class SocialProfile(
        @JsonProperty("socialNetworkType")
        val socialNetworkType: SocialNetworkType,
        @JsonProperty("value")
        val value: String,
    )

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class SocialNetworkType(
        @JsonProperty("id")
        val id: String,
        @JsonProperty("name")
        val name: String,
        @JsonProperty("url")
        val url: String,
        @JsonProperty("logo")
        val logo: String,
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
