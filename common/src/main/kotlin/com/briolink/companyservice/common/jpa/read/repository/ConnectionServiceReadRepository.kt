package com.briolink.companyservice.common.jpa.read.repository

import com.briolink.companyservice.common.jpa.read.entity.ConnectionServiceReadEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.UUID

interface ConnectionServiceReadRepository : JpaRepository<ConnectionServiceReadEntity, UUID> {
    @Modifying
    @Query(
        """update ConnectionReadEntity u
           set u.data = 
                   then function('jsonb_sets', u.data,
                           '{collaboratingCompanies,:companyId,slug}', :slug, text,
                           '{collaboratingCompanies,:companyId,location}', :location, text,
                           '{collaboratingCompanies,:companyId,industryName}', :industryName, text,
                           '{collaboratingCompanies,:companyId,logo}', :logo, text,
                           '{collaboratingCompanies,:companyId,name}', :name, text
                   
           where
            function('array_contains_element', c.collaboratingCompanyIds, :companyId) """,
    )
    fun updateCompany(
        @Param("companyId") companyId: UUID,
        @Param("slug") slug: String,
        @Param("name") name: String,
        @Param("logo") logo: String? = null,
        @Param("industryName") industryName: String? = null,
        @Param("location") location: String? = null,
    )
}
