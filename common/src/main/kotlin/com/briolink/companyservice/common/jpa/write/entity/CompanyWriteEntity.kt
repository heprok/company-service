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

@Table(name = "company", catalog = "schema_write")
@Entity
class CompanyWriteEntity(
    @Column(name = "name", nullable = false)
    var name: String,

    @Column(name = "website", nullable = false)
    var website: URL,

    @Column(name = "slug", nullable = false)
    var slug: String = "",

    @Column(name = "logo")
    var logo: URL? = null,

    @Column(name = "description", length = 10240)
    var description: String? = null,

    @Column(name = "isTypePublic")
    var isTypePublic: Boolean = true,

//    @Column(name = "country")
//    var country: String? = null,

    @Column(name = "location")
    var location: String? = null,

//    @Column(name = "state")
//    var state: String? = null,

    @Column(name = "facebook")
    var facebook: String? = null,

    @Column(name = "twitter")
    var twitter: String? = null,

    @Column(name = "created_by", nullable = false, length = 36)
    @Type(type = "uuid-char")
    var createdBy: UUID? = null,

    @ManyToOne(cascade = [CascadeType.PERSIST])
    @JoinColumn(name = "industry_id")
    var industry: IndustryWriteEntity? = null,

    @ManyToOne(cascade = [CascadeType.PERSIST])
    @JoinColumn(name = "occupation_id")
    var occupation: OccupationWriteEntity? = null,

    @ManyToMany(cascade = [CascadeType.PERSIST])
    @JoinTable(
            name = "companies_keywords",
            catalog = "schema_write",
            joinColumns = [JoinColumn(name = "company_id")],
            inverseJoinColumns = [JoinColumn(name = "keyword_id")],
    )
    var keywords: MutableList<KeywordWriteEntity> = mutableListOf()
) : BaseWriteEntity() {

    @PrePersist
    fun prePersist() {
        slug = StringUtil.slugify(name)
    }

}
