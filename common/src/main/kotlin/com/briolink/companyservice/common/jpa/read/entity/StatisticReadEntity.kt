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
    @Column(name = "statsNumberConnection", columnDefinition = "json", nullable = false)
    lateinit var statsNumberConnection: StatsNumberConnection

    @Type(type = "json")
    @Column(name = "statsByIndustry", columnDefinition = "json", nullable = false)
    lateinit var statsByIndustry: StatsByIndustry

    @Type(type = "json")
    @Column(name = "statsByCountry", columnDefinition = "json", nullable = false)
    lateinit var statsByCountry: StatsByCountry

    @Type(type = "json")
    @Column(name = "statsServiceProvided", columnDefinition = "json", nullable = false)
    lateinit var statsServiceProvided: StatsServiceProvided

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class StatsNumberConnection(
        @JsonProperty("companiesStatsByYear")
        var companiesStatsByYear: MutableMap<Int, CompaniesStats> = mutableMapOf()
    )

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class StatsByIndustry(
        @JsonProperty("companiesStatsByIndustry")
        var companiesStatsByIndustry: MutableMap<String, CompaniesStats> = mutableMapOf(),
        @JsonProperty("totalCountByIndustry")
        var totalCountByIndustry: MutableMap<String, Int> = mutableMapOf(),
    )

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class StatsByCountry(
        @JsonProperty("companiesStatsByCountry")
        var companiesStatsByCountry: MutableMap<String, CompaniesStats> = mutableMapOf(),
        @JsonProperty("totalCountByCountry")
        var totalCountByCountry: MutableMap<String, Int> = mutableMapOf(),
    )

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class StatsServiceProvided(
        @JsonProperty("companiesStatsByService")
        var companiesStatsByService: MutableMap<String, CompaniesStats> = mutableMapOf(),
        @JsonProperty("totalCountByService")
        var totalCountByService: MutableMap<String, Int> = mutableMapOf(),
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
        var role: MutableSet<Role> = mutableSetOf(),
    ) {
        @JsonProperty("industry")
        var industry: String? = null
        @JsonProperty("location")
        var location: String? = null
        @JsonProperty("countService")
        var countService: Int = 0
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
        val id: UUID,
        @JsonProperty("name")
        val name: String,
    )

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Role(
        @JsonProperty("id")
        val id: UUID,
        @JsonProperty("name")
        val name: String,
        @JsonProperty("type")
        val type: ConnectionRoleReadEntity.RoleType,
    )
}
