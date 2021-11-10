package com.briolink.companyservice.common.jpa.write.entity

import com.briolink.companyservice.common.util.StringUtil
import org.hibernate.annotations.Type
import java.net.URL
import java.util.*
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

    @Column(name = "location")
    var location: String? = null,

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

    @PrePersist
    fun prePersist() {
        slug = StringUtil.slugify(name, false)
    }

}
