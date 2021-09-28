package com.briolink.companyservice.common.jpa.write.entity

import com.briolink.companyservice.common.util.StringUtil
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.JoinColumn
import javax.persistence.JoinTable
import javax.persistence.ManyToMany
import javax.persistence.ManyToOne
import javax.persistence.PrePersist
import javax.persistence.Table

@Table(name = "company", catalog = "dev_write_company")
@Entity
class CompanyWriteEntity(
    @Column(name = "name", nullable = false)
    var name: String,

    @Column(name = "website", nullable = false)
    var website: String,

    @Column(name = "slug", nullable = false)
    var slug: String = "",

    @Column(name = "logo")
    var logo: String? = null,

    @Column(name = "about", length = 10240)
    var about: String? = null,

    @Column(name = "type", columnDefinition = "TINYINT")
    var type: Short? = null,

    @Column(name = "country")
    var country: String? = null,

    @Column(name = "city")
    var city: String? = null,

    @Column(name = "state")
    var state: String? = null,

    @Column(name = "facebook")
    var facebook: String? = null,

    @Column(name = "twitter")
    var twitter: String? = null,

    @ManyToOne(cascade = [CascadeType.PERSIST])
    @JoinColumn(name = "industry_id")
    var industry: IndustryWriteEntity? = null,

    @ManyToOne(cascade = [CascadeType.PERSIST])
    @JoinColumn(name = "occupation_id")
    var ocuupation: OccupationWriteEntity? = null,

    @ManyToMany(cascade = [CascadeType.PERSIST])
    @JoinTable(
            name = "company_key_words",
            joinColumns = [JoinColumn(name = "company_id")],
            inverseJoinColumns = [JoinColumn(name = "key_words_id")],
    )
    var keyWords: MutableList<KeyWordWriteEntity> = mutableListOf()
) : BaseWriteEntity() {

    @PrePersist
    fun prePersist() {
        slug = StringUtil.slugify(name)
    }
}
