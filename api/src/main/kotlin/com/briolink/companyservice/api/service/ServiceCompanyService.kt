package com.briolink.companyservice.api.service

import com.briolink.companyservice.api.types.ServiceFilter
import com.briolink.companyservice.api.types.ServiceSort
import com.briolink.companyservice.common.jpa.initSpec
import com.briolink.companyservice.common.jpa.read.entity.ServiceReadEntity
import com.briolink.companyservice.common.jpa.read.repository.service.ServiceReadRepository
import com.briolink.companyservice.common.jpa.read.repository.service.companyIdEqual
import com.briolink.companyservice.common.jpa.read.repository.service.betweenLastUsed
import com.briolink.companyservice.common.jpa.read.repository.service.betweenPrice
import com.briolink.companyservice.common.jpa.read.repository.service.equalHide
import com.briolink.companyservice.common.util.PageRequest
import org.springframework.data.domain.Page
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional
class ServiceCompanyService(
    private val serviceReadRepository: ServiceReadRepository,
) {
    fun getByCompanyId(id: UUID, limit: Int, offset: Int): Page<ServiceReadEntity> =
            serviceReadRepository.findByCompanyIdIs(id, PageRequest(offset, limit))

    fun getSpecification(filter: ServiceFilter?): Specification<ServiceReadEntity> =
            initSpec<ServiceReadEntity>()
                    .and(betweenPrice(filter?.cost?.start, filter?.cost?.end))
                    .and(equalHide(filter?.isHide))
                    .and(betweenLastUsed(filter?.lastUsed?.start, filter?.lastUsed?.end))


    fun findAll(companyId: UUID, limit: Int, sort: ServiceSort, offset: Int, filter: ServiceFilter?): Page<ServiceReadEntity> =
            serviceReadRepository.findAll(
                    getSpecification(filter).and(companyIdEqual(companyId)),
                    PageRequest(offset, limit, Sort.by(Sort.Direction.fromString(sort.direction.name), sort.sortBy.name)),
            )


    fun count(companyId: UUID, filter: ServiceFilter?): Long =
            serviceReadRepository.count(getSpecification(filter).and(companyIdEqual(companyId)))


    fun hideInCompany(companyId: UUID, serviceId: UUID, isHide: Boolean) =
            serviceReadRepository.hideServiceByIdAndCompanyId(companyId = companyId, id = serviceId, isHide = isHide)

    fun countServiceByCompany(companyId: UUID) = serviceReadRepository.existsByCompanyId(companyId)

}
