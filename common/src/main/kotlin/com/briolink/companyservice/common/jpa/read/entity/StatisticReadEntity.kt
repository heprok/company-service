package com.briolink.companyservice.common.jpa.read.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.JsonNode
import org.hibernate.annotations.Type
import java.net.URL
import java.time.LocalDate
import java.time.Year
import java.util.UUID
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Table(name = "statistic", catalog = "schema_read")
@Entity
class StatisticReadEntity(
    @Id
    @Type(type = "uuid-char")
    @Column(name = "companyId", length = 36)
    var companyId: UUID? = null,
) : BaseReadEntity() {

    @Type(type = "json")
    @Column(name = "statsNumberConnection", columnDefinition = "json")
    var statsNumberConnection: StatsNumberConnection? = null

    @Type(type = "json")
    @Column(name = "statsByIndustry", columnDefinition = "json")
    var statsByIndustry: StatsByIndustry? = null

    @Type(type = "json")
    @Column(name = "statsByCountry", columnDefinition = "json")
    var statsByCountry: StatsByCountry? = null

    @Type(type = "json")
    @Column(name = "statsServiceProvided", columnDefinition = "json")
    var statsServiceProvided: StatsServiceProvided? = null

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class StatsNumberConnection(
        @JsonProperty("years")
        var years: MutableMap<Int, CompaniesStats> = mutableMapOf()
    )

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class StatsByIndustry(
        @JsonProperty("industries")
        var industries: MutableMap<String, CompaniesStats> = mutableMapOf(),
    )

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class StatsByCountry(
        @JsonProperty("countries")
        var countries: MutableMap<String, CompaniesStats> = mutableMapOf(),
    )

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class StatsServiceProvided(
        @JsonProperty("services")
        var services: MutableMap<UUID, ServiceStats> = mutableMapOf(),
    )

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class CompaniesStats(
        @JsonProperty("listCompanies")
        var listCompanies: MutableSet<Company> = mutableSetOf(),
        @JsonProperty("totalCount")
        var totalCount: MutableMap<UUID, Int> = mutableMapOf(),
    )
    @JsonIgnoreProperties(ignoreUnknown = true)
    data class ServiceStats(
        @JsonProperty("service")
        var service: Service,
        @JsonProperty("totalCount")
        var totalCount: Int
    )

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Company(
        @JsonProperty("id")
        var id: UUID,
        @JsonProperty("name")
        var name: String,
        @JsonProperty("logo")
        var logo: URL?,
        @JsonProperty("slug")
        var slug: String,
        @JsonProperty("role")
        var role: String
    ) {
        @JsonProperty("industry")
        var industry: Industry? = null
        @JsonProperty("location")
        var location: String? = null
        @JsonProperty("lastCollaborate")
        val  lastCollaborate: Year = Year.now()
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Service(
        @JsonProperty("id")
        var id: UUID,
        @JsonProperty("name")
        var name: String,
        @JsonProperty("slug")
        var slug: String,
    )

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Industry(
        @JsonProperty("id")
        val id: String,
        @JsonProperty("name")
        val name: String,
    )

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Role(
        @JsonProperty("id")
        val id: String,
        @JsonProperty("name")
        val name: String,
    )
}
