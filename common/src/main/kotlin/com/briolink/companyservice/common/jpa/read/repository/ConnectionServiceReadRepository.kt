package com.briolink.companyservice.common.jpa.read.repository

import com.briolink.companyservice.common.jpa.enumeration.ConnectionStatusEnum
import com.briolink.companyservice.common.jpa.read.entity.ConnectionServiceReadEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.UUID

interface ConnectionServiceReadRepository : JpaRepository<ConnectionServiceReadEntity, UUID> {
    @Modifying
    @Query(
        """UPDATE ConnectionServiceReadEntity c
           SET c.data = function('jsonb_sets', c.data,
                           '{company,slug}', :slug, text,
                           '{company,location}', :location, text,
                           '{company,industryName}', :industryName, text,
                           '{company,logo}', :logo, text,
                           '{company,name}', :name, text
               )    
           WHERE c.collaboratingCompanyId = :companyId""",
    )
    fun updateCompany(
        @Param("companyId") companyId: UUID,
        @Param("slug") slug: String,
        @Param("name") name: String,
        @Param("logo") logo: String? = null,
        @Param("industryName") industryName: String? = null,
        @Param("location") location: String? = null,
    )

//    @Modifying
//    @Query(
//        """UPDATE ConnectionServiceReadEntity c
//           SET c.hidden = :hidden
//           WHERE c.id IN (:connectionServiceIds) AND c.hidden <> :hidden
//        """
//    )
//    fun changeVisibilityByIds(
//        @Param("connectionServiceIds") connectionServiceIds: List<UUID>,
//        @Param("hidden") hidden: Boolean
//    )

    @Query(
        """SELECT c
            FROM ConnectionServiceReadEntity c
            WHERE c.serviceId = :serviceId AND c.hidden = :hidden AND c.deleted = :deleted AND c._status = :status
        """
    )
    fun findByServiceId(
        @Param("serviceId") serviceId: UUID,
        @Param("hidden") hidden: Boolean = false,
        @Param("deleted") deleted: Boolean = false,
        @Param("status") status: Int = ConnectionStatusEnum.Verified.value,
        @Param("pageable") pageable: Pageable? = null
    ): Page<ConnectionServiceReadEntity>
}
