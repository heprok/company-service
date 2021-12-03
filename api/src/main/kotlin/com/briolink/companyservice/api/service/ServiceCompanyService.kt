package com.briolink.companyservice.api.service

import com.briolink.companyservice.api.types.ServiceFilter
import com.briolink.companyservice.api.types.ServiceSort
import com.briolink.companyservice.common.config.AppEndpointsProperties
import com.briolink.companyservice.common.jpa.initSpec
import com.briolink.companyservice.common.jpa.read.entity.ConnectionServiceReadEntity
import com.briolink.companyservice.common.jpa.read.entity.ServiceReadEntity
import com.briolink.companyservice.common.jpa.read.repository.ConnectionServiceReadRepository
import com.briolink.companyservice.common.jpa.read.repository.service.ServiceReadRepository
import com.briolink.companyservice.common.jpa.read.repository.service.betweenLastUsed
import com.briolink.companyservice.common.jpa.read.repository.service.betweenPrice
import com.briolink.companyservice.common.jpa.read.repository.service.companyIdEqual
import com.briolink.companyservice.common.jpa.read.repository.service.equalHide
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
    private val connectionServiceReadRepository: ConnectionServiceReadRepository,
    val appEndpointsProperties: AppEndpointsProperties,
) {
    fun getByCompanyId(id: UUID, limit: Int, offset: Int): Page<ServiceReadEntity> =
        serviceReadRepository.findByCompanyIdIs(id, PageRequest(offset, limit))

    fun getSpecification(filter: ServiceFilter?): Specification<ServiceReadEntity> =
        initSpec<ServiceReadEntity>()
            .and(betweenPrice(filter?.cost?.start, filter?.cost?.end))
            .and(equalHide(filter?.isHide))
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
    fun toggleVisibilityByIdAndCompanyId(companyId: UUID, serviceId: UUID) {
        serviceReadRepository.toggleVisibilityByIdAndCompanyId(serviceId = serviceId, companyId = companyId)
    }

    fun deleteServiceInCompany(serviceId: UUID, authorization: String): Boolean {
        val webClient = MonoGraphQLClient.createWithWebClient(
            WebClient.create(appEndpointsProperties.companyservice),
            headersConsumer = {
                it.add("Authorization", authorization)
            },
        )

        val result = webClient.reactiveExecuteQuery(
            """
                mutation deleteServiceLocal(${'$'}serviceId: ID!) {
                  deleteServiceLocal(serviceId: ${'$'}serviceId) {
                    success
                  }
                }
                """,
            mapOf(
                "serviceId" to serviceId,
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

    fun getVerifyUsesByServiceId(serviceId: UUID, limit: Int = 10, offset: Int = 0): Page<ConnectionServiceReadEntity> =
        connectionServiceReadRepository.findByServiceId(serviceId, PageRequest(offset = offset, limit = limit))
}
