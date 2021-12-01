package com.briolink.companyservice.common.jpa.read.entity

import com.fasterxml.jackson.annotation.JsonProperty
import org.hibernate.annotations.Type
import java.net.URL
import java.time.Year
import java.util.UUID
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Table(name = "connection_service", schema = "read")
@Entity
class ConnectionServiceReadEntity : BaseReadEntity() {
    @Id
    @Type(type = "pg-uuid")
    @Column(name = "id", nullable = false)
    var id: UUID? = null

    @Type(type = "pg-uuid")
    @Column(name = "company_id", nullable = false)
    lateinit var companyId: UUID

    @Type(type = "pg-uuid")
    @Column(name = "service_id")
    var serviceId: UUID? = null

    @Column(name = "name", nullable = false, length = 200)
    lateinit var name: String

    @Type(type = "uuid-array")
    @Column(name = "collaborating_company_ids")
    var collaboratingCompanyIds: MutableSet<UUID> = mutableSetOf()

    @Type(type = "jsonb")
    @Column(name = "data")
    var data: Data = Data()

    data class Data(
        @JsonProperty
        var collaboratingCompanies: MutableMap<UUID, Company> = mutableMapOf(),
        @JsonProperty
        var connectionsInfo: MutableList<ConnectionInfo> = mutableListOf()
    )

    data class Company(
        @JsonProperty
        val name: String,
        @JsonProperty
        val logo: URL? = null,
        @JsonProperty
        val location: String? = null,
        @JsonProperty
        val industryName: String? = null,
    )

    data class ConnectionInfo(
        @JsonProperty
        val companyId: UUID,
        @JsonProperty
        val roleName: String,
        @JsonProperty
        val periodUsedStart: Year,
        @JsonProperty
        val periodUsedEnd: Year?
    )
}
