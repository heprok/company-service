package com.briolink.companyservice.api.service

import com.briolink.companyservice.api.types.ServiceFilter
import com.briolink.companyservice.common.jpa.read.entity.ServiceReadEntity
import com.briolink.companyservice.common.jpa.read.entity.ServiceReadEntity_
import com.briolink.companyservice.common.jpa.read.repository.ServiceReadRepository
import com.briolink.companyservice.common.util.PageRequest
import org.springframework.data.domain.Page
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.util.UUID
import javax.persistence.criteria.Predicate

@Service
class ServiceCompanyService(
    private val serviceReadRepository: ServiceReadRepository,
) {
    fun getByCompanyId(id: UUID, limit: Int, offset: Int): Page<ServiceReadEntity> =
            serviceReadRepository.findByCompanyIdIs(id, PageRequest(offset, limit))

    fun findAll(companyId : UUID, limit: Int, offset: Int, filter: ServiceFilter): Page<ServiceReadEntity> {
        val spec = Specification<ServiceReadEntity> { root, query, builder ->
            builder.and(
                    equalsCompanyId(companyId)
                            .and(isByPriceBetween(filter.cost?.get(0), filter.cost?.get(1)))
                            .and(isByLastUsedBetween(filter.lastUsed?.get(0), filter.lastUsed?.get(1)))
                            .and(isByNumberUsesBetween(filter.numberUses?.get(0), filter.numberUses?.get(1)))
                            .toPredicate(root, query, builder),
            )
        }
        return serviceReadRepository.findAll(spec, PageRequest(offset, limit))
    }


    fun equalsCompanyId(companyId: UUID): Specification<ServiceReadEntity> {
        return Specification<ServiceReadEntity> { root, _, builder ->
            builder.equal(root.get(ServiceReadEntity_.companyId), companyId)
        }
    }

    fun isByPriceBetween(start: Double?, end: Double?): Specification<ServiceReadEntity>? {
        println(start)
        println(end)
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

    fun isByLastUsedBetween(start: LocalDate?, end: LocalDate?): Specification<ServiceReadEntity>? {
        return if (start != null && end != null) {
            Specification<ServiceReadEntity> { root, _, builder ->
                builder.between(root.get(ServiceReadEntity_.lastUsed), start, end)
            }
        } else if (start != null && end == null) {
            Specification<ServiceReadEntity> { root, _, builder ->
                builder.greaterThanOrEqualTo(root.get(ServiceReadEntity_.lastUsed), start)
            }
        } else if (start == null && end != null) {
            Specification<ServiceReadEntity> { root, _, builder ->
                builder.lessThanOrEqualTo(root.get(ServiceReadEntity_.lastUsed), end)
            }
        } else {
            null
        }
    }
}
