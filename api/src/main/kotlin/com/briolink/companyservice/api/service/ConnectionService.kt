package com.briolink.companyservice.api.service

import com.briolink.companyservice.api.types.ConnectionFilter
import com.briolink.companyservice.api.types.ConnectionSort
import com.briolink.companyservice.common.jpa.read.entity.ConnectionReadEntity
import com.briolink.companyservice.common.jpa.read.entity.ConnectionReadEntity_
import com.briolink.companyservice.common.jpa.read.repository.ConnectionReadRepository
import com.briolink.companyservice.common.util.PageRequest
import org.springframework.data.domain.Page
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class ConnectionService(
    private val connectionReadRepository: ConnectionReadRepository,
) {
//    fun getByCompanyId(id: UUID, limit: Int, offset: Int): Page<ConnectionReadEntity> =
//            connectionReadRepository.findByBuyerIdIs(id, PageRequest(offset, limit))


    fun findAll(companyId: UUID, limit: Int, offset: Int, sort: ConnectionSort, filter: ConnectionFilter?): Page<ConnectionReadEntity> {

        val spec = Specification<ConnectionReadEntity> { root, query, builder ->

            builder.and(
                    equalsBuyerId(companyId)
                            .or(equalsSellerId(companyId))
                            .and(isInIndustryId(filter?.industryId?.map { UUID.fromString(it) }))
                            .and(isInBuyerRoleId(filter?.collaboratorRoleId?.map { UUID.fromString(it) }))
                            .or(isInSellerRoleId(filter?.collaboratorRoleId?.map { UUID.fromString(it) }))
                            .and(isInBuyerId(filter?.collaboratorId?.map { UUID.fromString(it) }))
                            .or(isInSellerId(filter?.collaboratorId?.map { UUID.fromString(it) }))
//                            .and(containsLocation(filter.location))
                            .toPredicate(root, query, builder),

                    )
        }

        return connectionReadRepository.findAll(
                spec,
                PageRequest(offset, limit, Sort.by(Sort.Direction.fromString(sort.direction.name), sort.sortBy.name)),
        )
    }


    fun equalsBuyerId(companyId: UUID): Specification<ConnectionReadEntity> {
        return Specification<ConnectionReadEntity> { root, _, builder ->
            builder.equal(root.get(ConnectionReadEntity_.buyerId), companyId)
        }
    }

    fun equalsSellerId(companyId: UUID): Specification<ConnectionReadEntity> {
        return Specification<ConnectionReadEntity> { root, _, builder ->
            builder.equal(root.get(ConnectionReadEntity_.sellerId), companyId)
        }
    }

    fun isInIndustryId(industryIds: List<UUID>?): Specification<ConnectionReadEntity>? {
        return if (industryIds != null && industryIds.isNotEmpty()) {
            Specification<ConnectionReadEntity> { root, _, builder ->
                builder.and(root.get(ConnectionReadEntity_.industryId).`in`(industryIds))
            }
        } else {
            null
        }
    }

    fun isInBuyerRoleId(roleIds: List<UUID>?): Specification<ConnectionReadEntity>? {
        return if (roleIds != null && roleIds.isNotEmpty()) {
            Specification<ConnectionReadEntity> { root, query, builder ->
                builder.and(root.get(ConnectionReadEntity_.buyerRoleId).`in`(roleIds))
            }
        } else {
            null
        }
    }

    fun isInSellerRoleId(roleIds: List<UUID>?): Specification<ConnectionReadEntity>? {
        return if (roleIds != null && roleIds.isNotEmpty()) {
            Specification<ConnectionReadEntity> { root, _, builder ->
                builder.and(root.get(ConnectionReadEntity_.sellerRoleId).`in`(roleIds))
            }
        } else {
            null
        }
    }

    fun isInBuyerId(companyIds: List<UUID>?): Specification<ConnectionReadEntity>? {
        return if (companyIds != null && companyIds.isNotEmpty()) {
            Specification<ConnectionReadEntity> { root, query, builder ->
                builder.and(root.get(ConnectionReadEntity_.buyerId).`in`(companyIds))
            }
        } else {
            null
        }
    }

    fun isInSellerId(companyIds: List<UUID>?): Specification<ConnectionReadEntity>? {
        return if (companyIds != null && companyIds.isNotEmpty()) {
            Specification<ConnectionReadEntity> { root, _, builder ->
                builder.and(root.get(ConnectionReadEntity_.sellerId).`in`(companyIds))
            }
        } else {
            null
        }
    }

    fun existsConnectionByCompany(companyId: UUID): Boolean {
        return connectionReadRepository.existsBySellerIdAndBuyerId(companyId, companyId)
    }
//
//    fun containsLocation(location: String?): Specification<ConnectionReadEntity>? {
//        return Specification<ConnectionReadEntity> { root, query, builder ->
//            if (location.isNotBlank() && location != null) {
//                builder.like(builder.lower(root.get(ConnectionReadEntity_.)), "%${location.toLowerCase()}%")
//            } else {
//                null
//            }
//        }
//    }
//    fun isByPriceBetween(start: Double?, end: Double?): Specification<ConnectionReadEntity>? {
//
//        return if (start != null && end != null) {
//            Specification<ConnectionReadEntity> { root, _, builder ->
//                builder.between(root.get(ConnectionReadEntity_.price), start, end)
//            }
//        } else if (start != null && end == null) {
//            Specification<ConnectionReadEntity> { root, _, builder ->
//                builder.greaterThanOrEqualTo(root.get(ConnectionReadEntity_.price), start)
//            }
//        } else if (start == null && end != null) {
//            Specification<ConnectionReadEntity> { root, _, builder ->
//                builder.lessThanOrEqualTo(root.get(ConnectionReadEntity_.price), end)
//            }
//        } else {
//            null
//        }
//    }
//
//    fun isByNumberUsesBetween(start: Int?, end: Int?): Specification<ConnectionReadEntity>? {
//
//        return if (start != null && end != null) {
//            Specification<ConnectionReadEntity> { root, _, builder ->
//                builder.between(root.get(ConnectionReadEntity_.verifiedUses), start, end)
//            }
//        } else if (start != null && end == null) {
//            Specification<ConnectionReadEntity> { root, _, builder ->
//                builder.greaterThanOrEqualTo(root.get(ConnectionReadEntity_.verifiedUses), start)
//            }
//        } else if (start == null && end != null) {
//            Specification<ConnectionReadEntity> { root, _, builder ->
//                builder.lessThanOrEqualTo(root.get(ConnectionReadEntity_.verifiedUses), end)
//            }
//        } else {
//            null
//        }
//    }
//
//    fun isByLastUsedBetween(start: LocalDate?, end: LocalDate?): Specification<ConnectionReadEntity>? {
//
//        return if (start != null && end != null) {
//            Specification<ConnectionReadEntity> { root, _, builder ->
//                builder.between(root.get(ConnectionReadEntity_.lastUsed), start, end)
//            }
//        } else if (start != null && end == null) {
//            Specification<ConnectionReadEntity> { root, _, builder ->
//                builder.greaterThanOrEqualTo(root.get(ConnectionReadEntity_.lastUsed), start)
//            }
//        } else if (start == null && end != null) {
//            Specification<ConnectionReadEntity> { root, _, builder ->
//                builder.lessThanOrEqualTo(root.get(ConnectionReadEntity_.lastUsed), end)
//            }
//        } else {
//            null
//        }
//    }
}
