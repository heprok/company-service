package com.briolink.companyservice.api.service.connection

import com.blazebit.persistence.CriteriaBuilder
import com.blazebit.persistence.CriteriaBuilderFactory
import com.blazebit.persistence.PagedList
import com.blazebit.persistence.ParameterHolder
import com.blazebit.persistence.WhereBuilder
import com.briolink.companyservice.api.service.connection.dto.FiltersDto
import com.briolink.companyservice.api.service.connection.dto.SortDto
import com.briolink.companyservice.api.service.connection.dto.TabItemDto
import com.briolink.companyservice.common.domain.v1_0.CompanyServiceRefreshVerifyUses
import com.briolink.companyservice.common.domain.v1_0.Statistic
import com.briolink.companyservice.common.event.v1_0.RefreshConnectionServiceEvent
import com.briolink.companyservice.common.event.v1_0.StatisticRefreshEvent
import com.briolink.companyservice.common.jpa.enumeration.CompanyRoleTypeEnum
import com.briolink.companyservice.common.jpa.read.entity.ConnectionReadEntity
import com.briolink.companyservice.common.jpa.read.entity.IndustryReadEntity
import com.briolink.companyservice.common.jpa.read.entity.cte.RoleProjectionCte
import com.briolink.companyservice.common.jpa.read.repository.ConnectionReadRepository
import com.briolink.companyservice.common.jpa.read.repository.ConnectionServiceReadRepository
import com.briolink.event.publisher.EventPublisher
import com.vladmihalcea.hibernate.type.array.UUIDArrayType
import org.hibernate.jpa.TypedParameterValue
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID
import javax.persistence.EntityManager
import javax.persistence.Tuple

@Service
@Transactional
class ConnectionService(
    private val connectionReadRepository: ConnectionReadRepository,
    private val connectionServiceReadRepository: ConnectionServiceReadRepository,
    private val entityManager: EntityManager,
    private val eventPublisher: EventPublisher,
    private val criteriaBuilderFactory: CriteriaBuilderFactory
) {
//    fun getByCompanyId(id: UUID, limit: Int, offset: Int): Page<ConnectionReadEntity> =
//            connectionReadRepository.findByBuyerIdIs(id, PageRequest(offset, limit))

    fun getList(
        companyId: UUID,
        tabId: String?,
        filters: FiltersDto,
        sort: SortDto<SortDto.ConnectionSortKeys>,
        offset: Int = 0,
        limit: Int = 10
    ): PagedList<ConnectionReadEntity> {
        val cbf = criteriaBuilderFactory.create(entityManager, ConnectionReadEntity::class.java)
        val cb = cbf.from(ConnectionReadEntity::class.java)

        setActiveTab(companyId, tabId, cb)
        setFilters(companyId, cb, filters)

        return cb.orderBy(sort.key.name, sort.direction.name == "ASC").orderByAsc("id").page(offset, limit).resultList
    }

    fun <T> setFilters(
        companyId: UUID,
        cb: T,
        filters: FiltersDto
    ): T where T : WhereBuilder<T>, T : ParameterHolder<T> {
        cb
            .where("array_contains_element(hiddenCompanyIds, :currentCompanyId)").notEq(true)
            .where("array_contains_element(deletedCompanyIds, :currentCompanyId)").notEq(true)
            .setParameter("currentCompanyId", companyId)
        with(filters) {
            if (!collaboratorIds.isNullOrEmpty()) {
                cb.whereOr()
                    .whereAnd()
                    .where("participantFromCompanyId").`in`(collaboratorIds)
                    .where("participantFromCompanyId").notEq(companyId)
                    .endAnd()
                    .whereAnd()
                    .where("participantToCompanyId").`in`(collaboratorIds)
                    .where("participantToCompanyId").notEq(companyId)
                    .endAnd()
                    .endOr()
            }

            if (collaborationStartDate != null || collaborationEndDate != null) {
                cb
                    .whereExpression(
                        "int4range_contains(dates, :collaborationStartDate, :collaborationEndDate) = true",
                    )
                    .setParameter("collaborationStartDate", collaborationStartDate?.value)
                    .setParameter("collaborationEndDate", collaborationEndDate?.value)
            }

            if (!industryIds.isNullOrEmpty()) {
                cb.where("companyIndustryId").`in`(industryIds)
            }

            if (!serviceIds.isNullOrEmpty()) {
                cb
                    .whereExpression("array_contains(serviceIds, :serviceIds) = true")
                    .setParameter("serviceIds", TypedParameterValue(UUIDArrayType.INSTANCE, serviceIds.toTypedArray()))
            }

            if (locationId != null) {
                cb.whereExpression("${locationId.type.name.lowercase()}Id = :locationId")
                    .setParameter("locationId", locationId.id)
            }

            if (!status.isNullOrEmpty()) {
                cb.where("_status").`in`(status.map { it.value })
            }
        }

        return cb
    }

    fun tabs(companyId: UUID, withCount: Boolean = true, filters: FiltersDto? = null): List<TabItemDto> {
        val cbf = criteriaBuilderFactory.create(entityManager, Tuple::class.java)

        val cb = cbf.fromSubquery(RoleProjectionCte::class.java, "r")
            .from(ConnectionReadEntity::class.java)
            .bind("id").select("participantToRoleId")
            .bind("name").select("participantToRoleName")
            .bind("type").select("_participantToRoleType")
            .also { if (filters != null) setFilters(companyId, it, filters) }
            .where("participantFromCompanyId").eq(companyId)
            .whereOr()
            .where("_participantFromRoleType").eq(CompanyRoleTypeEnum.Buyer.value)
            .where("_participantFromRoleType").eq(CompanyRoleTypeEnum.Seller.value)
            .endOr()
            .unionAll()
            .from(ConnectionReadEntity::class.java)
            .bind("id").select("participantFromRoleId")
            .bind("name").select("participantFromRoleName")
            .bind("type").select("_participantFromRoleType")
            .also { if (filters != null) setFilters(companyId, it, filters) }
            .where("participantToCompanyId").eq(companyId)
            .whereOr()
            .where("_participantToRoleType").eq(CompanyRoleTypeEnum.Buyer.value)
            .where("_participantToRoleType").eq(CompanyRoleTypeEnum.Seller.value)
            .endOr()
            .endSet().end()
            .select("r.id", "id")
            .select("max(r.name)", "name")
            .select("max(r.type)", "type")
            .groupBy("r.id")

        if (withCount)
            cb.select("count(r.id)", "count")

        val resultList = cb.resultList

        return resultList.map {
            TabItemDto(
                "${it.get("type")}:${it.get("id")}",
                it.get("name") as String,
                if (withCount) (it.get("count") as Long).toInt() else null,
            )
        }
    }

    fun existsConnectionByCompany(companyId: UUID): Boolean =
        connectionReadRepository.existsByParticipantFromCompanyIdOrParticipantToCompanyId(companyId, companyId)

    fun setActiveTab(
        companyId: UUID,
        tab: String?,
        cb: CriteriaBuilder<ConnectionReadEntity>
    ): CriteriaBuilder<ConnectionReadEntity> {

        if (tab != null) {
            val tabType = tab.take(1).toInt()
            val tabId = UUID.fromString(tab.drop(2))
            cb.whereOr()
                .whereAnd()
                .where("participantFromCompanyId").eq(companyId)
                .where("participantToRoleId").eq(tabId)
                .where("_participantToRoleType").eq(tabType)
                .endAnd()
                .whereAnd()
                .where("participantToCompanyId").eq(companyId)
                .where("participantFromRoleId").eq(tabId)
                .where("_participantFromRoleType").eq(tabType)
                .endAnd()
                .endOr()
        } else {
            cb.whereOr()
                .where("participantFromCompanyId").eq(companyId)
                .where("participantToCompanyId").eq(companyId)
                .endOr()
        }
        return cb
    }

//    fun getCount(companyId: UUID, filter: FiltersDto?): Long {
//        val cbf = criteriaBuilderFactory.create(entityManager, ConnectionReadEntity::class.java)
//        val cb = cbf.from(ConnectionReadEntity::class.java)
//
//        setFilters(companyId, cb, filter ?: FiltersDto())
//    }

    fun getIndustriesInConnectionFromCompany(
        companyId: String,
        query: String?,
    ): List<IndustryReadEntity> =
        connectionReadRepository.getConnectionIndustriesByCompanyId(companyId, query = query?.ifBlank { null })
            .map { IndustryReadEntity(id = it.id, name = it.name) }

    fun changeVisibilityByIdAndCompanyId(companyId: UUID, connectionId: UUID, isHide: Boolean) {
        connectionReadRepository.changeVisibilityByIdAndCompanyId(
            connectionId = connectionId,
            companyId = companyId,
            hidden = isHide
        )
        connectionServiceReadRepository.changeVisibilityByConnectionId(connectionId, isHide)
        refreshVerifyUsesByConnectionId(connectionId)
        eventPublisher.publishAsync(StatisticRefreshEvent(Statistic(companyId)))
    }

    fun refreshVerifyUsesByConnectionId(connectionId: UUID) {
        connectionServiceReadRepository.getServiceIdsByConnectionId(connectionId).forEach {
            eventPublisher.publishAsync(RefreshConnectionServiceEvent(CompanyServiceRefreshVerifyUses(serviceId = it)))
        }
    }

    fun deleteConnectionInCompany(connectionId: UUID, companyId: UUID) {
        connectionReadRepository.softDeleteByIdAndCompanyId(id = connectionId, companyId = companyId)
        refreshVerifyUsesByConnectionId(connectionId)
        connectionServiceReadRepository.deleteByConnectionId(connectionId = connectionId)
        eventPublisher.publishAsync(StatisticRefreshEvent(Statistic(companyId)))
    }
}
