package com.briolink.companyservice.common.jpa.read.entity

import com.vladmihalcea.hibernate.type.json.JsonType
import org.hibernate.annotations.TypeDef
import org.hibernate.annotations.TypeDefs
import javax.persistence.MappedSuperclass

@TypeDefs(TypeDef(name = "json", typeClass = JsonType::class))
@MappedSuperclass
abstract class BaseReadEntity
