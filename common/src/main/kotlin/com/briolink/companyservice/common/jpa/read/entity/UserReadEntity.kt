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

@Table(name = "user", catalog = "test_read_company")
@Entity
class UserReadEntity {
    @Id
    @Type(type = "uuid-char")
    @Column(name = "id", nullable = false, length = 36)
    lateinit var id: UUID

    @Type(type = "uuid-char")
    @Column(name = "company_id", nullable = false, length = 36)
    lateinit var companyId: UUID

    @Type(type = "json")
    @Column(name = "data", nullable = false, columnDefinition = "json")
    lateinit var data: Data

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Data(
        @JsonProperty("firstName")
        val firstName: String,
        @JsonProperty("lastName")
        val lastName: String,
        @JsonProperty("jobPosition")
        val jobPosition: String,
        @JsonProperty("image")
        val image: String,
        @JsonProperty("company")
        val company: Company,
        @JsonProperty("created")
        val created: LocalDate,
        @JsonProperty("changed")
        val changed: LocalDate?,
    )
    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Company(
        @JsonProperty("name")
        val name: String,
        @JsonProperty("website")
        val website: String,
        @JsonProperty("logo")
        val logo: String
    )

}
