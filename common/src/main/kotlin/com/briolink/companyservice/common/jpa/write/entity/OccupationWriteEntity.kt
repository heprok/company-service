package com.briolink.companyservice.common.jpa.write.entity

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table

@Table(name = "occupation", catalog = "dev_write_company")
@Entity
class OccupationWriteEntity(
    @Column(name = "name", nullable = false)
    var name: String
) : BaseWriteEntity() {

}
