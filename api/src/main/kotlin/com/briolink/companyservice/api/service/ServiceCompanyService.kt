package com.briolink.companyservice.api.service

import com.briolink.companyservice.api.types.ServiceFilter
import com.briolink.companyservice.api.types.ServiceSort
import com.briolink.companyservice.common.config.AppEndpointsProperties
import com.briolink.companyservice.common.jpa.initSpec
import com.briolink.companyservice.common.jpa.read.entity.ServiceReadEntity
import com.briolink.companyservice.common.jpa.read.repository.service.ServiceReadRepository
import com.briolink.companyservice.common.jpa.read.repository.service.betweenLastUsed
import com.briolink.companyservice.common.jpa.read.repository.service.betweenPrice
import com.briolink.companyservice.common.jpa.read.repository.service.companyIdEqual
import com.briolink.companyservice.common.jpa.read.repository.service.equalHide
import com.briolink.companyservice.common.jpa.read.repository.service.isNotDeleted
import com.briolink.companyservice.common.util.PageRequest
import com.netflix.graphql.dgs.client.MonoGraphQLClient
import org.springframework.data.domain.Page
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.reactive.function.client.WebClient
import java.util.UUID
import javax.servlet.UnavailableException

@Service
@Transactional
class ServiceCompanyService(
    private val serviceReadRepository: ServiceReadRepository,
    val appEndpointsProperties: AppEndpointsProperties,
) {
    fun getSpecification(filter: ServiceFilter?): Specification<ServiceReadEntity> =
        initSpec<ServiceReadEntity>()
            .and(betweenPrice(filter?.cost?.start, filter?.cost?.end))
            .and(equalHide(filter?.isHide))
            .and(isNotDeleted())
            .and(betweenLastUsed(filter?.lastUsed?.start, filter?.lastUsed?.end))

    fun findAll(
        companyId: UUID,
        limit: Int,
        sort: ServiceSort,
        offset: Int,
        filter: ServiceFilter?
    ): Page<ServiceReadEntity> =
        serviceReadRepository.findAll(
            getSpecification(filter).and(companyIdEqual(companyId)),
            PageRequest(offset, limit, Sort.by(Sort.Direction.fromString(sort.direction.name), sort.sortBy.name)),
        )

    fun count(companyId: UUID, filter: ServiceFilter?): Long =
        serviceReadRepository.count(getSpecification(filter).and(companyIdEqual(companyId)))

    fun countServiceByCompany(companyId: UUID) = serviceReadRepository.existsByCompanyId(companyId)

    fun deleteServiceInCompany(serviceId: UUID, userId: UUID): Boolean {
        val webClient = MonoGraphQLClient.createWithWebClient(
            WebClient.create(appEndpointsProperties.companyservice),
        )

        val result = webClient.reactiveExecuteQuery(
            """
                mutation deleteServiceLocal(${'$'}serviceId: ID!, ${'$'}userId: ID! ) {
                  deleteServiceLocal(serviceId: ${'$'}serviceId, userId: ${'$'}userId) {
                    success
                  }
                }
                """,
            mapOf(
                "serviceId" to serviceId,
                "userId" to userId,
            ),
        ).block() ?: throw UnavailableException("CompanyService service unavailable")

        return try {
            val success = result.extractValue<Boolean>("deleteServiceLocal.success")
            serviceReadRepository.deleteById(serviceId)
            success
        } catch (e: Exception) {
            throw UnavailableException("CompanyService service unavailable")
        }
    }

    fun toggleVisibilityByIdAndCompanyId(serviceId: UUID, companyId: UUID): Boolean {
        val webClient = MonoGraphQLClient.createWithWebClient(
            WebClient.create(appEndpointsProperties.companyservice),
        )

        val result = webClient.reactiveExecuteQuery(
            """
                mutation hideServiceLocal(${'$'}serviceId: ID!) {
                  hideServiceLocal(serviceId: ${'$'}serviceId) {
                    success
                  }
                }
                """,
            mapOf(
                "serviceId" to serviceId,
            ),
        ).block() ?: throw UnavailableException("CompanyService service unavailable")

        return try {
            val success = result.extractValue<Boolean>("hideServiceLocal.success")
            serviceReadRepository.toggleVisibilityByIdAndCompanyId(serviceId = serviceId, companyId = companyId)
            success
        } catch (e: Exception) {
            throw UnavailableException("CompanyService service unavailable")
        }
    }
}
