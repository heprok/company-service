package com.briolink.companyservice.common.jpa.read.repository

import com.briolink.companyservice.common.jpa.read.entity.OccupationReadEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.*

interface OccupationReadRepository : JpaRepository<OccupationReadEntity, UUID> {
    @Query(
            value = """
            SELECT 
                id, name
            FROM 
                schema_read.occupation
            WHERE 
                MATCH (`name`) AGAINST (:query IN BOOLEAN MODE) LIMIT 10
        """,
            nativeQuery = true,
    )
    fun findByName(@Param("query") query: String?): List<OccupationReadEntity>
}
