package com.briolink.companyservice.api.service

import com.briolink.companyservice.api.types.ServiceFilter
import com.briolink.companyservice.api.types.ServiceSort
import com.briolink.companyservice.common.jpa.read.entity.ServiceReadEntity
import com.briolink.companyservice.common.jpa.read.entity.ServiceReadEntity_
import com.briolink.companyservice.common.jpa.read.repository.ServiceReadRepository
import com.briolink.companyservice.common.util.PageRequest
import org.springframework.data.domain.Page
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.Year
import java.util.UUID
import javax.persistence.criteria.Predicate

@Service
@Transactional
class ServiceCompanyService(
    private val serviceReadRepository: ServiceReadRepository,
) {
    fun getByCompanyId(id: UUID, limit: Int, offset: Int): Page<ServiceReadEntity> =
            serviceReadRepository.findByCompanyIdIs(id, PageRequest(offset, limit))

    fun findAll(companyId: UUID, limit: Int, sort: ServiceSort, offset: Int, filter: ServiceFilter?): Page<ServiceReadEntity> {

        val spec = Specification<ServiceReadEntity> { root, query, builder ->
            builder.and(
                    equalsCompanyId(companyId)
                            .and(isByPriceBetween(filter?.cost?.start, filter?.cost?.end))
                            .and(isHide(filter?.isHide))
                            .and(isByLastUsedBetween(filter?.lastUsed?.start, filter?.lastUsed?.end))
                            .toPredicate(root, query, builder),
            )
        }
        return serviceReadRepository.findAll(
                spec,
                PageRequest(offset, limit, Sort.by(Sort.Direction.fromString(sort.direction.name), sort.sortBy.name)),
        )
    }

    fun equalsCompanyId(companyId: UUID): Specification<ServiceReadEntity> {
        return Specification<ServiceReadEntity> { root, _, builder ->
            builder.equal(root.get(ServiceReadEntity_.companyId), companyId)
        }
    }

    fun isHide(hide: Boolean?): Specification<ServiceReadEntity>? {
        return if (hide != null) {
            Specification<ServiceReadEntity> { root, _, builder ->
                builder.equal(root.get(ServiceReadEntity_.isHide), hide)
            }
        } else null

    }

    fun isByPriceBetween(start: Double?, end: Double?): Specification<ServiceReadEntity>? {
        return if (start != null && end != null) {
            Specification<ServiceReadEntity> { root, _, builder ->
                builder.between(root.get(ServiceReadEntity_.price), start, end)
            }
        } else if (start != null && end == null) {
            Specification<ServiceReadEntity> { root, _, builder ->
                builder.greaterThanOrEqualTo(root.get(ServiceReadEntity_.price), start)
            }
        } else if (start == null && end != null) {
            Specification<ServiceReadEntity> { root, _, builder ->
                builder.lessThanOrEqualTo(root.get(ServiceReadEntity_.price), end)
            }
        } else {
            null
        }
    }

    fun isByNumberUsesBetween(start: Int?, end: Int?): Specification<ServiceReadEntity>? {
        return if (start != null && end != null) {
            Specification<ServiceReadEntity> { root, _, builder ->
                builder.between(root.get(ServiceReadEntity_.verifiedUses), start, end)
            }
        } else if (start != null && end == null) {
            Specification<ServiceReadEntity> { root, _, builder ->
                builder.greaterThanOrEqualTo(root.get(ServiceReadEntity_.verifiedUses), start)
            }
        } else if (start == null && end != null) {
            Specification<ServiceReadEntity> { root, _, builder ->
                builder.lessThanOrEqualTo(root.get(ServiceReadEntity_.verifiedUses), end)
            }
        } else {
            null
        }
    }

    fun isByLastUsedBetween(start: Year?, end: Year?): Specification<ServiceReadEntity>? {
        val startDate = start?.let {
            LocalDate.of(it.value, 1, 1)
        }
        val endDate = end?.let {
            LocalDate.of(it.value, 12, 31)
        }
        return if (start != null && end != null) {
            Specification<ServiceReadEntity> { root, _, builder ->
                builder.between(root.get(ServiceReadEntity_.lastUsed), startDate, endDate)
            }
        } else if (start != null && end == null) {
            Specification<ServiceReadEntity> { root, _, builder ->
                builder.greaterThanOrEqualTo(root.get(ServiceReadEntity_.lastUsed), startDate)
            }
        } else if (start == null && end != null) {
            Specification<ServiceReadEntity> { root, _, builder ->
                builder.lessThanOrEqualTo(root.get(ServiceReadEntity_.lastUsed), endDate)
            }
        } else {
            null
        }
    }

    fun hideInCompany(companyId: UUID, serviceId: UUID, isHide: Boolean) =
            serviceReadRepository.hideServiceByIdAndCompanyId(companyId = companyId, id = serviceId, isHide = isHide)

    fun countServiceByCompany(companyId: UUID) = serviceReadRepository.existsByCompanyId(companyId)

}
