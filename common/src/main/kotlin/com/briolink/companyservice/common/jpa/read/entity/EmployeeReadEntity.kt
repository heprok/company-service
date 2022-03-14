package com.briolink.companyservice.common.jpa.read.entity

import org.hibernate.annotations.Type
import java.io.Serializable
import java.util.UUID
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.IdClass
import javax.persistence.Table

class EmployeePK() : Serializable {
    lateinit var companyId: UUID
    lateinit var userId: UUID
}

@Table(name = "employee", schema = "read")
@Entity
@IdClass(EmployeePK::class)
class EmployeeReadEntity(
    @Id
    @Type(type = "pg-uuid")
    @Column(name = "company_id")
    var companyId: UUID,

    @Id
    @Type(type = "pg-uuid")
    @Column(name = "user_id")
    var userId: UUID,

    @Id
    @Type(type = "pg-uuid")
    @Column(name = "user_job_position_id")
    var userJobPositionId: UUID,

    @Type(type = "jsonb")
    @Column(name = "data", nullable = false, columnDefinition = "jsonb")
    var data: UserJobPositionReadEntity.Data
) : BaseReadEntity() {
    companion object {
        fun fromUserJobPosition(userJobPosition: UserJobPositionReadEntity) = EmployeeReadEntity(
            userId = userJobPosition.userId,
            companyId = userJobPosition.companyId,
            userJobPositionId = userJobPosition.id,
            data = userJobPosition.data,
        )
    }
}
