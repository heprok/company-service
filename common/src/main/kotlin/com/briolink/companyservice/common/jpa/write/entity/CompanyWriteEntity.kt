package com.briolink.companyservice.common.jpa.write.entity

import com.briolink.companyservice.common.domain.v1_0.Company
import com.briolink.companyservice.common.domain.v1_0.Industry
import com.briolink.companyservice.common.domain.v1_0.Occupation
import com.briolink.companyservice.common.dto.location.LocationId
import com.briolink.companyservice.common.jpa.enumeration.LocationTypeEnum
import com.briolink.companyservice.common.util.StringUtil
import org.hibernate.annotations.Type
import java.net.URL
import java.util.UUID
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.JoinColumn
import javax.persistence.JoinTable
import javax.persistence.ManyToMany
import javax.persistence.ManyToOne
import javax.persistence.PrePersist
import javax.persistence.Table

@Table(name = "company", schema = "write")
@Entity
class CompanyWriteEntity(
    @Column(name = "name", nullable = false)
    var name: String,

    @Column(name = "slug", nullable = false, length = 255)
    var slug: String = "",

    @Column(name = "logo")
    var logo: URL? = null,

    @Column(name = "description")
    var description: String? = null,

    @Column(name = "isTypePublic")
    var isTypePublic: Boolean = true,

    @Column(name = "country_id")
    var countryId: Int? = null,

    @Column(name = "state_id")
    var stateId: Int? = null,

    @Column(name = "city_id")
    var cityId: Int? = null,

    @Column(name = "facebook")
    var facebook: String? = null,

    @Column(name = "twitter")
    var twitter: String? = null,

    @Column(name = "created_by", nullable = false)
    @Type(type = "pg-uuid")
    var createdBy: UUID,

    @ManyToOne(cascade = [CascadeType.MERGE])
    @JoinColumn(name = "industry_id")
    var industry: IndustryWriteEntity? = null,

    @ManyToOne(cascade = [CascadeType.MERGE])
    @JoinColumn(name = "occupation_id")
    var occupation: OccupationWriteEntity? = null,

    @ManyToMany(cascade = [CascadeType.MERGE])
    @JoinTable(
        name = "companies_keywords",
        schema = "write",
        joinColumns = [JoinColumn(name = "company_id")],
        inverseJoinColumns = [JoinColumn(name = "keyword_id")],
    )
    var keywords: MutableList<KeywordWriteEntity> = mutableListOf()
) : BaseWriteEntity() {

    @Column(name = "website")
    private var website: String? = null

    var websiteUrl: URL?
        get() = website?.let { URL("https://$it") }
        set(value) {
            website = value?.host
        }

    fun getLocationId(): LocationId? {
        return if (cityId != null)
            LocationId(
                id = cityId!!,
                type = LocationTypeEnum.City,
            )
        else if (stateId != null)
            LocationId(
                id = stateId!!,
                type = LocationTypeEnum.State,
            )
        else if (countryId != null)
            LocationId(
                id = countryId!!,
                type = LocationTypeEnum.Country,
            )
        else null
    }

    @PrePersist
    fun prePersist() {
        slug = StringUtil.slugify(name, true)
    }

    fun toDomain() = Company(
        id = id!!,
        name = name,
        website = websiteUrl,
        description = description,
        slug = slug,
        logo = logo,
        isTypePublic = isTypePublic,
        locationId = getLocationId(),
        facebook = facebook,
        twitter = twitter,
        industry = industry?.let {
            Industry(
                it.id!!,
                it.name,
            )
        },
        createdBy = createdBy,
        occupation = occupation?.let {
            Occupation(
                it.id!!,
                it.name,
            )
        },
        keywords = ArrayList(
            keywords.map {
                Company.Keyword(
                    it.id!!,
                    it.name,
                )
            },
        ),
    )
}
