package com.briolink.companyservice.api.service.employee

import com.blazebit.persistence.CriteriaBuilderFactory
import com.blazebit.persistence.PagedList
import com.blazebit.persistence.ParameterHolder
import com.blazebit.persistence.WhereBuilder
import com.briolink.companyservice.api.service.employee.dto.EmployeeListFilter
import com.briolink.companyservice.api.service.employee.dto.EmployeeListRequest
import com.briolink.companyservice.api.service.employee.dto.EmployeeTab
import com.briolink.companyservice.api.service.employee.dto.EmployeeTabFilter
import com.briolink.companyservice.common.jpa.enumeration.UserJobPositionVerifyStatusEnum
import com.briolink.companyservice.common.jpa.read.entity.EmployeeReadEntity
import com.briolink.companyservice.common.jpa.read.entity.UserJobPositionReadEntity
import com.briolink.companyservice.common.jpa.read.repository.EmployeeReadRepository
import com.briolink.companyservice.common.util.PageRequest
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID
import javax.persistence.EntityManager

@Service
@Transactional
class EmployeeService(
    private val employeeReadRepository: EmployeeReadRepository,
    private val entityManager: EntityManager,
    private val criteriaBuilderFactory: CriteriaBuilderFactory
) {
    fun getByCompanyId(id: UUID, limit: Int, offset: Int): Page<EmployeeReadEntity> =
        employeeReadRepository.findByCompanyId(id, PageRequest(offset, limit))

    fun getListByCompanyId(request: EmployeeListRequest): PagedList<UserJobPositionReadEntity> {
        val cbf = criteriaBuilderFactory.create(entityManager, UserJobPositionReadEntity::class.java)
        val cb = cbf.from(UserJobPositionReadEntity::class.java)
        cb.where("companyId").eq(request.companyId)

        when (request.tab) {
            EmployeeTab.Current -> cb.whereExpression("upper(dates) is null")
            EmployeeTab.Former -> cb.whereExpression("upper(dates) is not null")
        }

        if (request.forConfirmation) cb.where("_status").eq(UserJobPositionVerifyStatusEnum.Pending.value)
        else cb.where("_status").eq(UserJobPositionVerifyStatusEnum.Verified.value)

        if (request.isOnlyUserWithPermission) cb.whereExpression("permissionLevel BETWEEN 0 AND 3")

        cb.whereExpression("dates != 'empty'")

        if (request.filters != null) applyFilters(request.filters, cb)

        return cb.orderByDesc("permissionLevel").orderByDesc("id").page(request.offset, request.limit).resultList
    }

    fun <T> applyFilters(filters: EmployeeListFilter, cb: T): T where T : WhereBuilder<T>, T : ParameterHolder<T> {
        with(filters) {
            if (!jobPositionTitles.isNullOrEmpty())
                cb.where("jobTitle").`in`(jobPositionTitles)
            if (!rights.isNullOrEmpty())
                cb.where("array_contains_common_element(rights, :rights").eq(true)
                    .setParameter("rights", rights.map { it.id })
            if (workDateRange != null)
                cb.where("daterange_cross(dates, :startDate, :endDate)").eq(true)
                    .setParameter("startDate", workDateRange.start ?: "null")
                    .setParameter("endDate", workDateRange.end ?: "null")
            if (!search.isNullOrBlank())
                cb.where("fts_partial(userFullNameTsv, :search)").eq(true)
                    .setParameter("search", search)
        }

        return cb
    }

    private fun getTabCurrentEmployees(
        companyId: UUID,
        filters: EmployeeListFilter?,
        withCount: Boolean = false
    ): EmployeeTabFilter {
        val tab = EmployeeTabFilter(tab = EmployeeTab.Current, value = 0)
        if (withCount) {
            val cbf = criteriaBuilderFactory.create(entityManager, UserJobPositionReadEntity::class.java)
            val cb = cbf.from(UserJobPositionReadEntity::class.java)
            cb.where("companyId").eq(companyId)
            cb.whereExpression("upper(dates) is null")
            cb.whereExpression("dates != 'empty'")
            if (filters != null) applyFilters(filters, cb)
            tab.value = cb.queryRootCountQuery.resultList[0].toInt()
        }

        return tab
    }

    private fun getTabFormerEmployees(
        companyId: UUID,
        filters: EmployeeListFilter?,
        withCount: Boolean = false
    ): EmployeeTabFilter {
        val tab = EmployeeTabFilter(tab = EmployeeTab.Former, value = 0)
        if (withCount) {
            val cbf = criteriaBuilderFactory.create(entityManager, UserJobPositionReadEntity::class.java)
            val cb = cbf.from(UserJobPositionReadEntity::class.java)
            cb.where("companyId").eq(companyId)
            cb.whereExpression("upper(dates) is not null")
            cb.whereExpression("dates != 'empty'")
            if (filters != null) applyFilters(filters, cb)
            tab.value = cb.queryRootCountQuery.resultList[0].toInt()
        }

        return tab
    }

    fun getTabs(companyId: UUID, filters: EmployeeListFilter?, withCount: Boolean = false): List<EmployeeTabFilter> {
        return listOf(
            getTabCurrentEmployees(companyId, filters, withCount),
            getTabFormerEmployees(companyId, filters, withCount)
        )
    }

//
//    fun getEmployees(companyId: UUID, limit: Int, offset: Int) : Page<UserJobPositionReadEntity> {
//        userJobPositionReadRepository.getEmployees(companyId, PageRequest(offset, limit))
//    }
}
