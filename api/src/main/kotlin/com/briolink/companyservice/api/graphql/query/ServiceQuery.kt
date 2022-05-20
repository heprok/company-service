package com.briolink.companyservice.api.graphql.query

import com.briolink.companyservice.api.graphql.fromEntity
import com.briolink.companyservice.api.service.ServiceCompanyService
import com.briolink.companyservice.api.types.BaseResult
import com.briolink.companyservice.api.types.CompanyInfoByServiceItem
import com.briolink.companyservice.api.types.CompanyInfoByServiceList
import com.briolink.companyservice.api.types.Service
import com.briolink.companyservice.api.types.ServiceFilter
import com.briolink.companyservice.api.types.ServiceList
import com.briolink.companyservice.api.types.ServiceSort
import com.briolink.companyservice.api.types.ServiceSortBy
import com.briolink.companyservice.api.types.SortDirection
import com.briolink.lib.common.BaseDataFetcherExceptionHandler
import com.briolink.lib.common.type.interfaces.IBaseLocalDateRange
import com.briolink.lib.common.util.BlSecurityUtil
import com.briolink.lib.common.validation.ConsistentLocalDates
import com.briolink.lib.common.validation.NullOrValidUUID
import com.briolink.lib.common.validation.NullOrValidWebsite
import com.briolink.lib.permission.service.PermissionService
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsQuery
import com.netflix.graphql.dgs.InputArgument
import org.springframework.security.access.prepost.PreAuthorize
import java.time.LocalDate
import java.util.UUID
import javax.validation.ConstraintViolationException

@ConsistentLocalDates
data class TestDto(
    @get:NullOrValidUUID
    val id: String? = null,
    @get:NullOrValidWebsite
    val website: String? = null,
    override val endDate: LocalDate,
    override val startDate: LocalDate? = null
) : IBaseLocalDateRange

@DgsComponent
class ServiceQuery(
    private val serviceCompanyService: ServiceCompanyService,
    private val permissionService: PermissionService,
) {

    @DgsQuery
    @PreAuthorize("isAuthenticated()")
    fun test(): BaseResult {
        var userErrors: List<Error>
        val dto = TestDto(
            id = "123",
            website = "asdasd",
            endDate = LocalDate.now().minusDays(13.toLong()),
            startDate = LocalDate.now()
        )

        try {
            serviceCompanyService.test(dto)
            userErrors = emptyList()
        } catch (e: ConstraintViolationException) {
            println(e)
            userErrors = BaseDataFetcherExceptionHandler.mapUserErrors(e)
        }
        return BaseResult(
            userErrors = userErrors.map { com.briolink.companyservice.api.types.Error(it.message ?: "") }
        )
    }

    @DgsQuery
    fun getServices(
        @InputArgument companyId: String,
        @InputArgument sort: ServiceSort?,
        @InputArgument limit: Int?,
        @InputArgument filter: ServiceFilter?,
        @InputArgument offset: Int?,

    ): ServiceList {
        return if (serviceCompanyService.countServiceByCompany(companyId = UUID.fromString(companyId))) {
            val filterSecurity =
                if (!BlSecurityUtil.isGuest && permissionService.checkPermission(
                        userId = BlSecurityUtil.currentUserId,
                        accessObjectId = UUID.fromString(companyId),
                        right = "EditCompanyService@Company"
                    )
                ) {
                    filter?.copy(isHide = false) ?: ServiceFilter(isHide = false)
                } else filter

            val page =
                serviceCompanyService.findAll(
                    companyId = UUID.fromString(companyId),
                    sort = sort ?: ServiceSort(ServiceSortBy.Name, SortDirection.ASC),
                    limit = limit ?: 10,
                    offset = offset ?: 0,
                    filter = filterSecurity,
                )

            ServiceList(
                items = page.content.map { Service.fromEntity(it) },
                totalItems = page.totalElements.toInt(),
            )
        } else {
            ServiceList(items = listOf(), totalItems = -1)
        }
    }

    @DgsQuery
    fun getServicesCount(
        @InputArgument companyId: String,
        @InputArgument filter: ServiceFilter?
    ): Int = serviceCompanyService.count(companyId = UUID.fromString(companyId), filter = filter).toInt()

    @DgsQuery
    fun getVerifyUsesByService(
        @InputArgument serviceId: String,
        @InputArgument limit: Int?,
        @InputArgument offset: Int?,
    ): CompanyInfoByServiceList {
        val page = serviceCompanyService.getVerifyUsesByServiceId(
            serviceId = UUID.fromString(serviceId),
            limit = limit ?: 10,
            offset = offset ?: 0
        )
        return CompanyInfoByServiceList(
            items = page.content.map {
                CompanyInfoByServiceItem.fromEntity(it)
            },
            totalItems = page.totalElements.toInt()
        )
    }
}
