package com.briolink.companyservice.common.jpa.read.repository.connection.service

import com.briolink.companyservice.common.jpa.matchBoolMode
import com.briolink.companyservice.common.jpa.read.entity.ConnectionReadEntity
import com.briolink.companyservice.common.jpa.read.entity.ConnectionReadEntity_
import com.briolink.companyservice.common.jpa.read.entity.ConnectionServiceReadEntity
import com.briolink.companyservice.common.jpa.read.entity.ConnectionServiceReadEntity_
import org.springframework.data.jpa.domain.Specification
import java.util.*

fun fullTextSearchByConnectionServiceName(serviceName: String): Specification<ConnectionServiceReadEntity> {
    return Specification { root, _, criteriaBuilder ->
        val match = criteriaBuilder.matchBoolMode(
                root.get(ConnectionServiceReadEntity_.serviceName),
                criteriaBuilder.literal(serviceName),
        )
        criteriaBuilder.greaterThan(match, 0.0)
    }
}

fun equalsCompanyId(companyId: UUID): Specification<ConnectionServiceReadEntity> {
    return Specification<ConnectionServiceReadEntity> { root, _, builder ->
        builder.equal(root.get(ConnectionServiceReadEntity_.companyId), companyId)
    }
}
