package com.briolink.companyservice.common.jpa

import org.springframework.data.jpa.domain.Specification
import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.Expression

fun <T> initSpec(): Specification<T> {
    return Specification { _, _, _ -> null }
}

fun <Root, Relation> fetchRelation(relationName: String): Specification<Root> {
    return Specification { root, query, criteriaBuilder ->
        query.distinct(true)
        root.fetch<Root, Relation>(relationName)
        criteriaBuilder.conjunction()
    }
}

fun <Root> fetchRelations(relationNames: List<String>): Specification<Root> {
    return Specification { root, query, criteriaBuilder ->
        query.distinct(true)
        for (relation in relationNames) {
            root.fetch<Root, Any>(relation)
        }
        criteriaBuilder.conjunction()
    }
}

fun <Root> distinctRootEntity(): Specification<Root> {
    return Specification { _, query, _ ->
        query.distinct(true)
        null
    }
}

fun CriteriaBuilder.matchBoolMode(firstParam: Expression<*>, secondParam: Expression<*>): Expression<out Double> {
    return function("match_bool_mode", Double::class.java, firstParam, secondParam)
}
