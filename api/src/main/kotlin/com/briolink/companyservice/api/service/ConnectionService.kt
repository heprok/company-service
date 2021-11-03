package com.briolink.companyservice.api.service

import com.briolink.companyservice.api.types.Collaborator
import com.briolink.companyservice.api.types.ConnectionFilter
import com.briolink.companyservice.api.types.ConnectionRoleType
import com.briolink.companyservice.api.types.ConnectionSort
import com.briolink.companyservice.common.jpa.read.entity.ConnectionReadEntity
import com.briolink.companyservice.common.jpa.read.entity.ConnectionRoleReadEntity
import com.briolink.companyservice.common.jpa.read.entity.ConnectionServiceReadEntity
import com.briolink.companyservice.common.jpa.read.entity.IndustryReadEntity
import com.briolink.companyservice.common.jpa.read.repository.connection.service.ConnectionServiceReadRepository
import com.briolink.companyservice.common.jpa.read.repository.connection.ConnectionReadRepository
import com.briolink.companyservice.common.jpa.read.repository.connection.betweenDateCollab
import com.briolink.companyservice.common.jpa.read.repository.connection.equalsBuyerId
import com.briolink.companyservice.common.jpa.read.repository.connection.equalsSellerId
import com.briolink.companyservice.common.jpa.read.repository.connection.equalsSellerIdOrBuyerId
import com.briolink.companyservice.common.jpa.read.repository.connection.fullTextSearchByLocation
import com.briolink.companyservice.common.jpa.read.repository.connection.inBuyerRoleIds
import com.briolink.companyservice.common.jpa.read.repository.connection.inCollaboratorRoles
import com.briolink.companyservice.common.jpa.read.repository.connection.inCollaborators
import com.briolink.companyservice.common.jpa.read.repository.connection.inIndustryIds
import com.briolink.companyservice.common.jpa.read.repository.connection.inSellerRoleIds
import com.briolink.companyservice.common.jpa.read.repository.connection.inServiceIds
import com.briolink.companyservice.common.jpa.read.repository.connection.inVerificationStage
import com.briolink.companyservice.common.jpa.read.repository.connection.service.equalsCompanyId
import com.briolink.companyservice.common.jpa.read.repository.connection.service.fullTextSearchByConnectionServiceName
import com.briolink.companyservice.common.util.PageRequest
import org.springframework.data.domain.Page
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import java.util.*
import javax.persistence.EntityManager
import javax.persistence.Tuple
import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.CriteriaQuery
import javax.persistence.criteria.Root

@Service
class ConnectionService(
    private val connectionReadRepository: ConnectionReadRepository,
    private val connectionServiceReadRepository: ConnectionServiceReadRepository,
    private val entityManager: EntityManager
) {
//    fun getByCompanyId(id: UUID, limit: Int, offset: Int): Page<ConnectionReadEntity> =
//            connectionReadRepository.findByBuyerIdIs(id, PageRequest(offset, limit))


    fun findAll(companyId: UUID, limit: Int, offset: Int, sort: ConnectionSort?, filter: ConnectionFilter?): Page<ConnectionReadEntity> {
        var spec = getSpecification(filter)
        spec = when (filter?.role?.type) {
            ConnectionRoleType.Buyer -> spec.and(equalsBuyerId(companyId)).and(inBuyerRoleIds(listOf(UUID.fromString(filter.role.id))))
            ConnectionRoleType.Seller -> spec.and(equalsSellerId(companyId).and(inSellerRoleIds(listOf(UUID.fromString(filter.role.id)))))
            else -> spec.and(equalsSellerIdOrBuyerId(companyId))
        }
        val sortBy = if (sort != null) Sort.by(Sort.Direction.fromString(sort.direction.name), sort.sortBy.name) else Sort.unsorted()
        return connectionReadRepository.findAll(
                spec,
                PageRequest(offset, limit, sortBy),
        )
    }

    fun count(companyId: UUID, filter: ConnectionFilter?): Long {
        var spec = getSpecification(filter)
        spec = when (filter?.role?.type) {
            ConnectionRoleType.Buyer -> spec.and(equalsBuyerId(companyId))
            ConnectionRoleType.Seller -> spec.and(equalsSellerId(companyId))
            else -> spec.and(equalsSellerIdOrBuyerId(companyId))
        }
        return connectionReadRepository.count(spec)
    }

    fun getRolesAndCountForCompany(companyId: UUID, filter: ConnectionFilter? = null): Map<UUID, Int> {
        val resultRolesAndCount = mutableListOf<Tuple>()
        val resultMap: MutableMap<UUID, Int> = mutableMapOf()
        val cb: CriteriaBuilder = entityManager.criteriaBuilder
        val cq: CriteriaQuery<Tuple> = cb.createQuery(Tuple::class.java)
        val root: Root<ConnectionReadEntity> = cq.from(ConnectionReadEntity::class.java)
        cq.multiselect(
                root.get<UUID>("sellerRoleId"),
                root.get<ConnectionReadEntity.Role>("sellerRole").alias("role"),
                cb.count(root.get<UUID>("sellerRoleId")).alias("count"),
        )
        cq.where(getSpecification(filter).and(equalsSellerId(companyId)).toPredicate(root, cq, cb))
        cq.groupBy(root.get<UUID>("sellerRoleId"), root.get<ConnectionReadEntity.Role>("sellerRole"))
        resultRolesAndCount.addAll(entityManager.createQuery(cq).resultList)

        cq.multiselect(
                root.get<UUID>("buyerRoleId"),
                root.get<ConnectionReadEntity.Role>("buyerRole").alias("role"),
                cb.count(root.get<UUID>("buyerRoleId")).alias("count"),
        )
        cq.where(getSpecification(filter).and(equalsBuyerId(companyId)).toPredicate(root, cq, cb))
        cq.groupBy(root.get<UUID>("buyerRoleId"), root.get<UUID>("buyerRole"))
        resultRolesAndCount.addAll(entityManager.createQuery(cq).resultList)

        resultRolesAndCount.forEach {
            val role = it["role"] as ConnectionReadEntity.Role
            val count = (it["count"] as Long).toInt()
            resultMap[role.id] = count
        }

        return resultMap
    }

    fun getRolesForCompany(companyId: UUID): List<ConnectionReadEntity.Role> {
        val resultRoles = mutableListOf<Tuple>()
        val cb: CriteriaBuilder = entityManager.criteriaBuilder
        val cq: CriteriaQuery<Tuple> = cb.createQuery(Tuple::class.java)
        val root: Root<ConnectionReadEntity> = cq.from(ConnectionReadEntity::class.java)
        cq.multiselect(
                root.get<UUID>("sellerRoleId"),
                root.get<ConnectionReadEntity.Role>("sellerRole").alias("role"),
        )
        cq.where(equalsSellerId(companyId).toPredicate(root, cq, cb))
        cq.groupBy(root.get<UUID>("sellerRoleId"), root.get<ConnectionReadEntity.Role>("sellerRole"))
        resultRoles.addAll(entityManager.createQuery(cq).resultList)

        cq.multiselect(
                root.get<UUID>("buyerRoleId"),
                root.get<ConnectionReadEntity.Role>("buyerRole").alias("role"),
        )
        cq.where(equalsBuyerId(companyId).toPredicate(root, cq, cb))
        cq.groupBy(root.get<UUID>("buyerRoleId"), root.get<UUID>("buyerRole"))
        resultRoles.addAll(entityManager.createQuery(cq).resultList)

        return resultRoles.map {
            it["role"] as ConnectionReadEntity.Role
        }
    }

    fun getSpecification(filter: ConnectionFilter?) = Specification<ConnectionReadEntity> { _, _, _ -> null }
            .and(inServiceIds(filter?.serviceIds?.map { UUID.fromString(it) }))
            .and(inIndustryIds(filter?.industryIds?.map { UUID.fromString(it) }))
            .and(
                    inCollaboratorRoles(
                            filter?.collaboratorRoles.let { listCollaboratorRoles ->
                                val list = mutableMapOf<UUID, ConnectionRoleReadEntity.RoleType>().apply {
                                    listCollaboratorRoles?.forEach {
                                        if (it != null) {
                                            this[UUID.fromString(it.id)] = ConnectionRoleReadEntity.RoleType.valueOf(it.type.name)
                                        }
                                    }
                                }
                                list
                            },
                    ),
//                    when (filter?.collaboratorRoles?.type) {
//                        ConnectionRoleType.Buyer -> inSellerRoleIds(filter.collaboratorRoles?.map { UUID.fromString(it.id) })
//                        ConnectionRoleType.Seller -> inBuyerRoleIds(filter.collaboratorRoles?.map { UUID.fromString(it.id) })
//                        else -> inBuyerRoleIds(filter?.collaboratorRoles?.map { UUID.fromString(it.id) })?.or(
//                                inSellerRoleIds(filter?.collaboratorRoles?.map { UUID.fromString(it.id) }),
//                        )
//                    },
            )
            .and(
                    inCollaborators(
                            filter?.collaborators.let { listCollaborators ->
                                val list = mutableMapOf<UUID, ConnectionRoleReadEntity.RoleType>().apply {
                                    listCollaborators?.forEach {
                                        if (it != null) {
                                            this[UUID.fromString(it.id)] = ConnectionRoleReadEntity.RoleType.valueOf(it.type.name)
                                        }
                                    }
                                }
                                list
                            },
                    ),

//                    when (filter?.role?.type) {
//                        ConnectionRoleType.Buyer -> inSellerIds(filter.collaboratorIds?.map { UUID.fromString(it) })
//                        ConnectionRoleType.Seller -> inBuyerIds(filter.collaboratorIds?.map { UUID.fromString(it) })
//                        else -> inBuyerIds(filter?.collaboratorIds?.map { UUID.fromString(it) })?.or(
//                                inSellerIds(filter?.collaboratorIds?.map { UUID.fromString(it) }),
//                        )
//                    },
            )
            .and(betweenDateCollab(start = filter?.datesOfCollaborators?.start, end = filter?.datesOfCollaborators?.end))
            .and(inVerificationStage(filter?.verificationStages?.map { ConnectionReadEntity.ConnectionStatus.valueOf(it!!.name) }))
            .and(fullTextSearchByLocation(filter?.location))


    fun existsConnectionByCompany(companyId: UUID): Boolean {
        return connectionReadRepository.existsBySellerIdAndBuyerId(companyId, companyId)
    }

    fun getCollaboratorsUsedForCompany(companyId: UUID, query: String, limit: Int = 10): List<Collaborator> {
        return mutableListOf<Collaborator>().apply {
            addAll(
                    connectionReadRepository.getCollaboratorsBuyerUsedForCompany(
                            sellerCompanyId = companyId,
                            query = query,
                    ).map {
                        Collaborator(id = it.id.toString(), name = it.name, type = ConnectionRoleType.Buyer)
                    },
            )
            addAll(
                    connectionReadRepository.getCollaboratorsSellerUsedForCompany(
                            buyerCompanyId = companyId,
                            query = query,
                    ).map {
                        Collaborator(id = it.id.toString(), name = it.name, type = ConnectionRoleType.Seller)
                    },
            )

        }.distinct().take(limit).toList()
    }

    fun getConnectionRoleUsedForCompany(companyId: UUID, query: String, limit: Int = 10): List<ConnectionRoleReadEntity> {
        val mutableListCollaborator = mutableListOf<ConnectionRoleReadEntity>()
        mutableListCollaborator.addAll(
                connectionReadRepository.getCollaboratorsBuyerRolesUsedForCompany(
                        sellerCompanyId = companyId,
                        query = query,
                ).map {
                    ConnectionRoleReadEntity(it.id, it.name, ConnectionRoleReadEntity.RoleType.Buyer)
                },
        )
        mutableListCollaborator.addAll(
                connectionReadRepository.getCollaboratorsSellerRolesUsedForCompany(
                        buyerCompanyId = companyId,
                        query = query,
                ).map {
                    ConnectionRoleReadEntity(it.id, it.name, ConnectionRoleReadEntity.RoleType.Seller)
                },
        )
        return mutableListCollaborator.distinct().take(limit).toList()
    }

    fun getServicesProvided(companyId: UUID, query: String): List<ConnectionServiceReadEntity> {
        return if (query.isEmpty()) {
            connectionServiceReadRepository.findAll(equalsCompanyId(companyId)).distinct().take(10)
        } else {
            connectionServiceReadRepository.findAll(
                    fullTextSearchByConnectionServiceName(query).and(equalsCompanyId(companyId)),
            ).distinct().take(10)
        }
    }

    fun getIndustriesInConnectionFromCompany(companyId: UUID, query: String): List<IndustryReadEntity> =
            connectionReadRepository.getIndustriesUsesCompany(companyId = companyId, query = query)
                    .map { IndustryReadEntity(it.id, it.name) }
}
