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
import com.briolink.companyservice.common.jpa.read.repository.UserJobPositionReadRepository
import com.briolink.companyservice.common.util.PageRequest
import com.briolink.lib.permission.enumeration.AccessObjectTypeEnum
import com.briolink.lib.permission.enumeration.PermissionRoleEnum
import com.briolink.lib.permission.exception.notfound.UserPermissionRoleNotFoundException
import com.briolink.lib.permission.model.PermissionRight
import com.briolink.lib.permission.service.PermissionService
import com.vladmihalcea.hibernate.type.util.ObjectMapperWrapper
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
    private val userJobPositionReadRepository: UserJobPositionReadRepository,
    private val criteriaBuilderFactory: CriteriaBuilderFactory,
    private val permissionService: PermissionService,
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
                    .setParameter("rights", rights.map { it })
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
            getTabFormerEmployees(companyId, filters, withCount),
        )
    }

    fun checkPermissionLevel(companyId: UUID, byUserId: UUID, userId: UUID): Boolean {
        val byUserRole = permissionService.getUserPermissionRights(byUserId, companyId, AccessObjectTypeEnum.Company) ?: return false
        val userRole = permissionService.getUserPermissionRights(userId, companyId, AccessObjectTypeEnum.Company)

        if (byUserRole.permissionRole.level < (userRole?.permissionRole?.level ?: 5)) return false

        return true
    }

    private fun updatePermission(
        userId: UUID,
        companyId: UUID,
        permissionRole: PermissionRoleEnum,
        rights: List<PermissionRight>
    ): Boolean {
        val result = userJobPositionReadRepository.updateUserPermission(
            userId = userId,
            companyId = companyId,
            level = permissionRole.level,
            rights = rights.map { it.toString() }.toTypedArray(),
            permissionRoleId = permissionRole.id,
            enabledPermissionRightsJson = ObjectMapperWrapper.INSTANCE.objectMapper.writeValueAsString(rights),
        ) > 0

        employeeReadRepository.updateUserPermission(
            userId,
            companyId,
            permissionRole.id,
            ObjectMapperWrapper.INSTANCE.objectMapper.writeValueAsString(rights),
        )
        return result
    }

    fun editPermissionRight(
        companyId: UUID,
        userId: UUID,
        role: PermissionRoleEnum,
        rights: List<PermissionRight>?
    ): Boolean {
        permissionService.editPermissionRole(userId, AccessObjectTypeEnum.Company, companyId, role)
        permissionService.setPermissionRights(userId, companyId, "Company", role.name, rights?.map { it.toString() } ?: listOf())?.also {
            return updatePermission(userId, companyId, it.permissionRole, it.permissionRights)
        }
        return false
    }

    fun deleteEmployee(companyId: UUID, userId: UUID): Boolean {
        deletePermissionRole(userId, companyId)
        refreshEmployees(companyId)
        return userJobPositionReadRepository.deleteByCompanyIdAndUserId(companyId, userId) > 0
    }

    fun setFormerEmployee(companyId: UUID, userId: UUID): Boolean {
        deletePermissionRole(userId, companyId)
        userJobPositionReadRepository.deleteUserPermission(userId, companyId)
        refreshEmployees(companyId)
        return userJobPositionReadRepository.setFormerEmployee(userId, companyId) > 0
    }

    private fun deletePermissionRole(userId: UUID, companyId: UUID): Boolean {
        return try {
            permissionService.deletePermissionRole(
                userId = userId,
                accessObjectType = AccessObjectTypeEnum.Company,
                accessObjectId = companyId,
            )
            true
        } catch (ex: UserPermissionRoleNotFoundException) {
            false
        }
    }

    fun refreshEmployees(companyId: UUID) {
        employeeReadRepository.deleteAllByCompanyId(companyId)
        employeeReadRepository.refreshEmployeesByCompanyId(companyId)
    }
//
//    fun getEmployees(companyId: UUID, limit: Int, offset: Int) : Page<UserJobPositionReadEntity> {
//        userJobPositionReadRepository.getEmployees(companyId, PageRequest(offset, limit))
//    }
}
