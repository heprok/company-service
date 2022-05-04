package com.briolink.companyservice.common.jpa.read.entity

import com.briolink.companyservice.common.jpa.enumeration.ConnectionObjectTypeEnum
import org.hibernate.annotations.Type
import java.util.UUID
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Table

@Table(name = "connection", schema = "read")
@Entity
class ConnectionReadEntity(
    @Id
    @GeneratedValue
    @Type(type = "pg-uuid")
    @Column(name = "id")
    val id: UUID? = null,
) : BaseReadEntity() {

    @Type(type = "pg-uuid")
    @Column(name = "from_object_id", nullable = false)
    lateinit var fromObjectId: UUID

    @Column(name = "from_object_type", nullable = false)
    private var _fromObjectType: Int = ConnectionObjectTypeEnum.Company.value

    @Type(type = "pg-uuid")
    @Column(name = "to_object_id", nullable = false)
    lateinit var toObjectId: UUID

    @Column(name = "to_object_type", nullable = false)
    private var _toObjectType: Int = ConnectionObjectTypeEnum.Company.value

    var toObjectType: ConnectionObjectTypeEnum
        get() = ConnectionObjectTypeEnum.ofValue(_toObjectType)
        set(value) {
            _toObjectType = value.value
        }

    var fromObjectType: ConnectionObjectTypeEnum
        get() = ConnectionObjectTypeEnum.ofValue(_fromObjectType)
        set(value) {
            _fromObjectType = value.value
        }

    @Column(name = "hidden", nullable = false)
    var hidden: Boolean = false
}
