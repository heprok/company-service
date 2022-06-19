package com.briolink.companyservice.common.jpa.read.entity

import com.briolink.lib.common.jpa.type.LTreeSetType
import com.briolink.lib.common.jpa.type.LTreeType
import com.briolink.lib.common.jpa.type.PersistentEnumSetType
import com.briolink.lib.common.jpa.type.PersistentEnumType
import com.briolink.lib.common.jpa.type.SetArrayType
import com.vladmihalcea.hibernate.type.array.IntArrayType
import com.vladmihalcea.hibernate.type.array.StringArrayType
import com.vladmihalcea.hibernate.type.array.UUIDArrayType
import com.vladmihalcea.hibernate.type.basic.YearType
import com.vladmihalcea.hibernate.type.json.JsonBinaryType
import com.vladmihalcea.hibernate.type.range.PostgreSQLRangeType
import com.vladmihalcea.hibernate.type.range.Range
import org.hibernate.annotations.TypeDef
import org.hibernate.annotations.TypeDefs
import javax.persistence.MappedSuperclass

@TypeDefs(
    TypeDef(name = "jsonb", typeClass = JsonBinaryType::class),
    TypeDef(name = "year", typeClass = YearType::class),
    TypeDef(name = "uuid-array", typeClass = UUIDArrayType::class),
    TypeDef(name = "int-array", typeClass = IntArrayType::class),
    TypeDef(name = "text-array", typeClass = StringArrayType::class),
    TypeDef(name = "jsonb", typeClass = JsonBinaryType::class),
    TypeDef(name = "set-array", typeClass = SetArrayType::class),
    TypeDef(name = "persist-enum", typeClass = PersistentEnumType::class),
    TypeDef(name = "persist-enum-set", typeClass = PersistentEnumSetType::class),
    TypeDef(name = "ltree", typeClass = LTreeType::class),
    TypeDef(name = "ltree-set", typeClass = LTreeSetType::class),
    TypeDef(typeClass = PostgreSQLRangeType::class, defaultForType = Range::class),
)
@MappedSuperclass
abstract class BaseReadEntity
