package com.briolink.companyservice.common.jpa.read.repository.service

import com.briolink.companyservice.common.jpa.read.entity.ServiceReadEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.UUID

interface ServiceReadRepository : JpaRepository<ServiceReadEntity, UUID>, JpaSpecificationExecutor<ServiceReadEntity> {
    fun existsByCompanyId(companyId: UUID): Boolean

    @Modifying
    @Query(
        """
        UPDATE read.service SET is_hidden = NOT is_hidden WHERE id = ?1 AND company_id = ?2
    """,
        nativeQuery = true,
    )
    fun toggleVisibilityByIdAndCompanyId(serviceId: UUID, companyId: UUID)

    @Modifying
    @Query(
        """UPDATE ServiceReadEntity s
           SET s.hidden = :hidden
           WHERE s.id = :id""",
    )
    fun setHidden(
        @Param("id") id: UUID,
        @Param("hidden") hidden: Boolean
    )

    @Query(
        "SELECT data ->>'slug' FROM read.service WHERE id = ?1",
        nativeQuery = true
    )
    fun getSlugById(serviceId: UUID): String?

    @Modifying
    @Query(
        """UPDATE ServiceReadEntity s
           SET s.deleted = :deleted
           WHERE s.id = :id""",
    )
    fun setDeleted(
        @Param("id") id: UUID,
        @Param("deleted") deleted: Boolean
    )

    @Query(
        """SELECT s FROM ServiceReadEntity s WHERE s.hidden = false AND s.deleted = false"""
    )
    @Deprecated("Remove after merge request")
    fun findAllAndNotHiddenAndNotDeleted(): List<ServiceReadEntity>

    @Modifying
    @Query("DELETE from ServiceReadEntity c WHERE c.id = ?1")
    override fun deleteById(id: UUID)

    @Query(
        "SELECT data ->>'slug' FROM read.service WHERE id = ?1 AND is_hidden = false AND is_deleted = false",
        nativeQuery = true
    )
    @Deprecated("Remove after merge request")
    fun getSlugOrNullByServiceIdAndNotHiddenAndNotDeleted(serviceId: UUID): String?

    @Deprecated("Remove after merge request")
    fun countByCompanyIdAndHiddenAndDeleted(companyId: UUID, hidden: Boolean = false, deleted: Boolean = false): Long
}
