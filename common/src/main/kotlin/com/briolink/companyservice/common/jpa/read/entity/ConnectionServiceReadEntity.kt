package com.briolink.companyservice.common.jpa.read.entity

import com.briolink.companyservice.common.jpa.enumeration.ConnectionStatusEnum
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
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

    @Type(type = "pg-uuid")
    @Column(name = "collaborating_company_id")
    lateinit var collaboratingCompanyId: UUID

    @Column(name = "hidden", nullable = false)
    var hidden: Boolean = false

    @Column(name = "deleted", nullable = false)
    var deleted: Boolean = false

    @Column(name = "status", nullable = false)
    private var _status: Int = ConnectionStatusEnum.Pending.value

    var status: ConnectionStatusEnum
        get() = ConnectionStatusEnum.fromInt(_status)
        set(value) {
            _status = value.value
        }

    @Type(type = "jsonb")
    @Column(name = "data")
    lateinit var data: Data

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Data(
        @JsonProperty
        val company: Company,
        @JsonProperty
        val roleName: String,
        @JsonProperty
        val periodUsedStart: Year,
        @JsonProperty
        val periodUsedEnd: Year?
    )

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Company(
        @JsonProperty
        val id: UUID,
        @JsonProperty
        val name: String,
        @JsonProperty
        val logo: URL? = null,
        @JsonProperty
        val slug: String,
        @JsonProperty
        val location: String? = null,
        @JsonProperty
        val industryName: String? = null,
    )
}
