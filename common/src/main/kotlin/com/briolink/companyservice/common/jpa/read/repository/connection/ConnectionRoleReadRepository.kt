package com.briolink.companyservice.common.jpa.read.repository.connection

import com.briolink.companyservice.common.jpa.read.entity.ConnectionRoleReadEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.*

interface ConnectionRoleReadRepository : JpaRepository<ConnectionRoleReadEntity, UUID> {
    @Query(
            value = """
            SELECT 
                id, name
            FROM 
                schema_read.connection_role
            WHERE 
                MATCH (`name`) AGAINST (:query IN BOOLEAN MODE) LIMIT 10
        """,
            nativeQuery = true,
    )
    fun findByName(@Param("query") query: String?): List<ConnectionRoleReadEntity>
}
