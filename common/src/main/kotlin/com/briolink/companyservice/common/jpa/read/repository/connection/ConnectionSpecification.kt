package com.briolink.companyservice.common.jpa.read.repository.connection

import com.briolink.companyservice.common.jpa.initSpec
import com.briolink.companyservice.common.jpa.matchBoolMode
import com.briolink.companyservice.common.jpa.read.entity.CompanyReadEntity
import com.briolink.companyservice.common.jpa.read.entity.ConnectionReadEntity
import com.briolink.companyservice.common.jpa.read.entity.ConnectionReadEntity_
import com.briolink.companyservice.common.jpa.read.entity.ConnectionRoleReadEntity
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

fun equalsBuyerRoleId(roleId: UUID): Specification<ConnectionReadEntity> {
    return Specification<ConnectionReadEntity> { root, _, builder ->
        builder.equal(root.get(ConnectionReadEntity_.buyerRoleId), roleId)
    }
}

fun equalsSellerRoleId(roleId: UUID): Specification<ConnectionReadEntity> {
    return Specification<ConnectionReadEntity> { root, _, builder ->
        builder.equal(root.get(ConnectionReadEntity_.sellerRoleId), roleId)
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
        }
    } else if (start != null && end != null) {
        Specification<ConnectionReadEntity> { root, _, builder ->
            builder.greaterThanOrEqualTo(root.get(ConnectionReadEntity_.startCollaboration), start)
        }.and { root, _, builder ->
            builder.lessThanOrEqualTo(root.get(ConnectionReadEntity_.endCollaboration), end)
        }
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

fun inCollaboratorRoles(collaboratorRoles: MutableMap<UUID, ConnectionRoleReadEntity.RoleType>?): Specification<ConnectionReadEntity>? {
    return if (!collaboratorRoles.isNullOrEmpty()) {
        var spec = Specification<ConnectionReadEntity> { _, _, _ -> null }
        collaboratorRoles.forEach { (id, type) ->
            spec = spec.or(
                    if (type == ConnectionRoleReadEntity.RoleType.Buyer)
                        equalsBuyerId(id) else equalsSellerId(id),
            )
        }
        spec
    } else {
        null
    }
}

fun inCollaborators(collaborators: Map<UUID, ConnectionRoleReadEntity.RoleType>?): Specification<ConnectionReadEntity>? {
    return if (!collaborators.isNullOrEmpty()) {
        var spec = Specification<ConnectionReadEntity> { _, _, _ -> null }
        collaborators.forEach { (id, type) ->
            spec = spec.or(
                    if (type == ConnectionRoleReadEntity.RoleType.Buyer)
                        equalsBuyerId(id) else equalsSellerId(id),
            )
        }
        spec
    } else {
        null
    }
}

fun fullTextSearchByService(serviceId: UUID): Specification<ConnectionReadEntity> {
    return initSpec()
}

fun fullTextSearchByLocation(location: String?): Specification<ConnectionReadEntity>? {
    return if (!location.isNullOrEmpty()) Specification { root, _, criteriaBuilder ->
        criteriaBuilder.like(
                root.get(ConnectionReadEntity_.location),
                "%$location%",
        )
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
