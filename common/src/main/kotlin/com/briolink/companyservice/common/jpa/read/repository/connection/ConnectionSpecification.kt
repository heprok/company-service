package com.briolink.companyservice.common.jpa.read.repository.connection

import com.briolink.companyservice.common.jpa.initSpec
import com.briolink.companyservice.common.jpa.matchBoolMode
import com.briolink.companyservice.common.jpa.read.entity.ConnectionReadEntity
import com.briolink.companyservice.common.jpa.read.entity.ConnectionReadEntity_
import org.springframework.data.jpa.domain.Specification
import java.time.Year
import java.util.*

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

fun equalsSellerIdOrBuyerId(companyId: UUID): Specification<ConnectionReadEntity> {
    return equalsSellerId(companyId).or(equalsBuyerId(companyId))
}

fun betweenDateCollab(start: Year?, end: Year?): Specification<ConnectionReadEntity>? {

    return if (start == null && end != null) {
        Specification<ConnectionReadEntity> { root, _, builder ->
            builder.lessThanOrEqualTo(root.get(ConnectionReadEntity_.endCollaboration), end)
        }
    } else if (start != null && end == null) {
        Specification<ConnectionReadEntity> { root, _, builder ->
            builder.greaterThanOrEqualTo(root.get(ConnectionReadEntity_.startCollaboration), start)
        }.and { root, _, builder ->
            builder.lessThanOrEqualTo(root.get(ConnectionReadEntity_.endCollaboration), start)
        }.or { root, _, builder ->
            builder.isNull(root.get(ConnectionReadEntity_.endCollaboration))
        }
    } else if (start != null && end != null) {
        initSpec<ConnectionReadEntity>().and(betweenStartCollaboration(start, end)).or(betweenEndCollaboration(start, end))
    } else {
        null
    }
}

fun betweenStartCollaboration(start: Year?, end: Year?): Specification<ConnectionReadEntity>? {
    return if (start != null && end != null) {
        Specification<ConnectionReadEntity> { root, _, builder ->
            builder.between(root.get(ConnectionReadEntity_.startCollaboration), start, end)
        }
    } else if (start != null && end == null) {
        Specification<ConnectionReadEntity> { root, _, builder ->
            builder.greaterThanOrEqualTo(root.get(ConnectionReadEntity_.startCollaboration), start)
        }
    } else if (start == null && end != null) {
        Specification<ConnectionReadEntity> { root, _, builder ->
            builder.lessThanOrEqualTo(root.get(ConnectionReadEntity_.startCollaboration), end)
        }
    } else {
        null
    }
}

fun betweenEndCollaboration(start: Year?, end: Year?): Specification<ConnectionReadEntity>? {
    return if (start != null && end != null) {
        Specification<ConnectionReadEntity> { root, _, builder ->
            builder.between(root.get(ConnectionReadEntity_.endCollaboration), start, end)
        }
    } else if (start != null && end == null) {
        Specification<ConnectionReadEntity> { root, _, builder ->
            builder.greaterThanOrEqualTo(root.get(ConnectionReadEntity_.endCollaboration), start)
        }.and { root, _, builder ->
            builder.isNull(root.get(ConnectionReadEntity_.endCollaboration))
        }
    } else if (start == null && end != null) {
        Specification<ConnectionReadEntity> { root, _, builder ->
            builder.lessThanOrEqualTo(root.get(ConnectionReadEntity_.endCollaboration), end)
        }
    } else {
        null
    }
}

fun inServiceIds(serviceIds: List<UUID>?): Specification<ConnectionReadEntity>? {
    return if (serviceIds != null) {
        var spec = Specification<ConnectionReadEntity> { _, _, _ -> null }
        serviceIds.forEach {
            spec = spec.or(fullTextSearchByService(it))
        }
        spec
    } else {
        null
    }
}

fun inVerificationStage(status: List<ConnectionReadEntity.ConnectionStatus>?): Specification<ConnectionReadEntity>? {
    return if (status != null && status.isNotEmpty()) {
        Specification<ConnectionReadEntity> { root, _, builder ->
            builder.and(root.get(ConnectionReadEntity_.verificationStage).`in`(status))
        }
    } else {
        null
    }
}

fun inIndustryIds(industryIds: List<UUID>?): Specification<ConnectionReadEntity>? {
    return if (industryIds != null && industryIds.isNotEmpty()) {
        Specification<ConnectionReadEntity> { root, _, builder ->
            builder.and(root.get(ConnectionReadEntity_.industryId).`in`(industryIds))
        }
    } else {
        null
    }
}

fun fullTextSearchByService(serviceId: UUID): Specification<ConnectionReadEntity> {
    return Specification { root, _, criteriaBuilder ->
        val match = criteriaBuilder.matchBoolMode(
                root.get(ConnectionReadEntity_.serviceIds),
                criteriaBuilder.literal(serviceId),
        )
        criteriaBuilder.greaterThan(match, 0.0)
    }
}

fun fullTextSearchByLocation(location: String?): Specification<ConnectionReadEntity>? {
    return if (location != null) Specification { root, _, criteriaBuilder ->
        val match = criteriaBuilder.matchBoolMode(
                root.get(ConnectionReadEntity_.location),
                criteriaBuilder.literal(location),
        )
        criteriaBuilder.greaterThan(match, 0.0)
    } else {
        null
    }
}

fun inBuyerRoleIds(roleIds: List<UUID>?): Specification<ConnectionReadEntity>? {
    return if (roleIds != null && roleIds.isNotEmpty()) {
        Specification<ConnectionReadEntity> { root, _, builder ->
            builder.and(root.get(ConnectionReadEntity_.buyerRoleId).`in`(roleIds))
        }
    } else {
        null
    }
}

fun inSellerRoleIds(roleIds: List<UUID>?): Specification<ConnectionReadEntity>? {
    return if (roleIds != null && roleIds.isNotEmpty()) {
        Specification<ConnectionReadEntity> { root, _, builder ->
            builder.and(root.get(ConnectionReadEntity_.sellerRoleId).`in`(roleIds))
        }
    } else {
        null
    }
}

fun inBuyerIds(companyIds: List<UUID>?): Specification<ConnectionReadEntity>? {
    return if (companyIds != null && companyIds.isNotEmpty()) {
        Specification<ConnectionReadEntity> { root, _, builder ->
            builder.and(root.get(ConnectionReadEntity_.buyerId).`in`(companyIds))
        }
    } else {
        null
    }
}

fun inSellerIds(companyIds: List<UUID>?): Specification<ConnectionReadEntity>? {
    return if (companyIds != null && companyIds.isNotEmpty()) {
        Specification<ConnectionReadEntity> { root, _, builder ->
            builder.and(root.get(ConnectionReadEntity_.sellerId).`in`(companyIds))
        }
    } else {
        null
    }
}
