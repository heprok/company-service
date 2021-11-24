package com.briolink.companyservice.common.jpa.read.repository.service

import com.briolink.companyservice.common.jpa.read.entity.ServiceReadEntity
import com.briolink.companyservice.common.jpa.read.entity.ServiceReadEntity_
import org.springframework.data.jpa.domain.Specification
import java.time.LocalDate
import java.time.Year
import java.util.UUID

fun companyIdEqual(companyId: UUID): Specification<ServiceReadEntity> {
    return Specification<ServiceReadEntity> { root, _, builder ->
        builder.equal(root.get(ServiceReadEntity_.companyId), companyId)
    }
}

fun equalHide(hide: Boolean?): Specification<ServiceReadEntity>? {
    return if (hide != null) {
        Specification<ServiceReadEntity> { root, _, builder ->
            builder.equal(root.get(ServiceReadEntity_.isHide), hide)
        }
    } else null
}

fun betweenPrice(start: Double?, end: Double?): Specification<ServiceReadEntity>? {
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

fun betweenNumberUses(start: Int?, end: Int?): Specification<ServiceReadEntity>? {
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

fun betweenLastUsed(start: Year?, end: Year?): Specification<ServiceReadEntity>? {
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
