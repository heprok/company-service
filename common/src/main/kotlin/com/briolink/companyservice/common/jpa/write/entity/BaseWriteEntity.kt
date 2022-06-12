package com.briolink.companyservice.common.jpa.write.entity

import com.briolink.lib.common.jpa.type.LTreeSetType
import com.briolink.lib.common.jpa.type.LTreeType
import com.briolink.lib.common.jpa.type.PersistentEnumSetType
import com.briolink.lib.common.jpa.type.PersistentEnumType
import com.briolink.lib.common.jpa.type.SetArrayType
import com.vladmihalcea.hibernate.type.basic.YearType
import com.vladmihalcea.hibernate.type.json.JsonBinaryType
import org.hibernate.annotations.Type
import org.hibernate.annotations.TypeDef
import org.hibernate.annotations.TypeDefs
import java.util.UUID
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.MappedSuperclass

@TypeDefs(
    TypeDef(name = "jsonb", typeClass = JsonBinaryType::class),
    TypeDef(name = "year", typeClass = YearType::class),
    TypeDef(name = "jsonb", typeClass = JsonBinaryType::class),
    TypeDef(name = "set-array", typeClass = SetArrayType::class),
    TypeDef(name = "persist-enum", typeClass = PersistentEnumType::class),
    TypeDef(name = "persist-enum-set", typeClass = PersistentEnumSetType::class),
    TypeDef(name = "ltree", typeClass = LTreeType::class),
    TypeDef(name = "ltree-set", typeClass = LTreeSetType::class)
)
@MappedSuperclass
abstract class BaseWriteEntity {
    @Id
    @GeneratedValue
    @Type(type = "pg-uuid")
    var id: UUID? = null
}
