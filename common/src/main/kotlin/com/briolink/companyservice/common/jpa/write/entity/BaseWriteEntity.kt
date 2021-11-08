package com.briolink.companyservice.common.jpa.write.entity

import com.vladmihalcea.hibernate.type.basic.YearType
import com.vladmihalcea.hibernate.type.json.JsonBinaryType
import com.vladmihalcea.hibernate.type.json.JsonType
import org.hibernate.annotations.Type
import org.hibernate.annotations.TypeDef
import org.hibernate.annotations.TypeDefs
import java.util.UUID
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.MappedSuperclass

@TypeDefs(
        TypeDef(name = "jsonb", typeClass = JsonBinaryType::class),
        TypeDef(name = "year", typeClass = YearType::class)
)
@MappedSuperclass
abstract class BaseWriteEntity {
    @Id
    @GeneratedValue
    @Type(type = "pg-uuid")
    var id: UUID? = null
}
