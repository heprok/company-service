package com.briolink.companyservice.api.graphql.query

import com.blazebit.persistence.CriteriaBuilderFactory
import com.briolink.companyservice.api.graphql.fromEntity
import com.briolink.companyservice.api.types.ChartByCountry
import com.briolink.companyservice.api.types.ChartByCountryItem
import com.briolink.companyservice.api.types.ChartByIndustry
import com.briolink.companyservice.api.types.ChartByIndustryItem
import com.briolink.companyservice.api.types.ChartByServicesProvided
import com.briolink.companyservice.api.types.ChartByServicesProvidedItem
import com.briolink.companyservice.api.types.ChartConnectionCountByYear
import com.briolink.companyservice.api.types.ChartConnectionCountByYearItem
import com.briolink.companyservice.api.types.ChartItemHint
import com.briolink.companyservice.api.types.ChartItemWithHint
import com.briolink.companyservice.api.types.ChartTabItem
import com.briolink.companyservice.api.types.CompanyInfoItem
import com.briolink.companyservice.api.types.CompanyStatistic
import com.briolink.companyservice.api.types.CompanyStatisticCharts
import com.briolink.companyservice.api.types.CompanyStatisticTotal
import com.briolink.companyservice.api.types.ConnectionCompanyRoleType
import com.briolink.companyservice.api.types.Image
import com.briolink.companyservice.common.jpa.read.entity.CompanyReadEntity
import com.briolink.companyservice.common.jpa.read.entity.statistic.Chart
import com.briolink.companyservice.common.jpa.read.entity.statistic.ChartListItem
import com.briolink.companyservice.common.jpa.read.entity.statistic.ChartListItemWithRoles
import com.briolink.companyservice.common.jpa.read.entity.statistic.ChartListItemWithServicesCount
import com.briolink.companyservice.common.jpa.read.entity.statistic.ChartListItemWithUsesCount
import com.briolink.companyservice.common.jpa.read.entity.statistic.StatisticReadEntity
import com.briolink.companyservice.common.jpa.read.repository.CompanyReadRepository
import com.fasterxml.jackson.core.type.TypeReference
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsQuery
import com.netflix.graphql.dgs.InputArgument
import com.vladmihalcea.hibernate.type.util.ObjectMapperWrapper
import graphql.schema.DataFetchingEnvironment
import java.util.Objects
import java.util.UUID
import java.util.function.Function
import java.util.stream.Collectors
import javax.persistence.EntityManager
import javax.persistence.Tuple

inline fun <reified T> Tuple.getOrNull(alias: String): T? =
    try {
        this.get(alias, T::class.java)
    } catch (e: IllegalArgumentException) {
        null
    }

@DgsComponent
class CompanyStatisticQuery(
    private val entityManager: EntityManager,
    private val criteriaBuilderFactory: CriteriaBuilderFactory,
    private val companyReadRepository: CompanyReadRepository
) {
    fun collectCompanyIds(vararg values: Any?): MutableSet<UUID> {
        val companyIds = mutableSetOf<UUID>()
        values.forEach { v ->
            when (v) {
                is Chart -> {
                    companyIds.addAll(v.items.map { it.companyIds }.flatten())
                }
                is List<*> -> {
                    companyIds.addAll(
                        v.mapNotNull {
                            if (it is ChartListItem) it.companyId else null
                        },
                    )
                }
            }
        }
        return companyIds
    }

    fun <T> mapRawData(raw: String?, type: TypeReference<T>) =
        if (raw != null) ObjectMapperWrapper.INSTANCE.objectMapper.readValue(
            raw, type,
        ) else null

    @DgsQuery
    fun getCompanyStatistic(@InputArgument companyId: String, dfe: DataFetchingEnvironment): CompanyStatistic {
        val cbf = criteriaBuilderFactory.create(entityManager, Tuple::class.java)
        val cb = cbf.from(StatisticReadEntity::class.java)

        cb.where("companyId").eq(UUID.fromString(companyId))

        if (dfe.selectionSet.contains("total/connectionsNumber")) cb.select("totalConnections", "totalConnections")
        if (dfe.selectionSet.contains("total/servicesProvidedNumber")) cb.select(
            "totalServicesProvided",
            "totalServicesProvided"
        )
        if (dfe.selectionSet.contains("total/numberOfCollaborationCompanies"))
            cb.select("totalCollaborationCompanies", "totalCollaborationCompanies")

        if (dfe.selectionSet.containsAnyOf("charts/connectionCountByYear/data", "charts/connectionCountByYear/tabs"))
            cb.select("chartConnectionCountByYear", "chartConnectionCountByYear")
        if (dfe.selectionSet.containsAnyOf("charts/byCountry/data", "charts/byCountry/tabs"))
            cb.select("chartByCountry", "chartByCountry")
        if (dfe.selectionSet.containsAnyOf("charts/byIndustry/data", "charts/byIndustry/tabs"))
            cb.select("chartByIndustry", "chartByIndustry")
        if (dfe.selectionSet.containsAnyOf("charts/byServicesProvided/data", "charts/byServicesProvided/tabs"))
            cb.select("chartByServicesProvided", "chartByServicesProvided")

        if (dfe.selectionSet.contains("charts/connectionCountByYear/listByTab"))
            cb
                .select(
                    "jsonb_get(chartConnectionCountByYearData, 'data', :dk1, 'items')",
                    "chartConnectionCountByYearData"
                )
                .setParameter(
                    "dk1",
                    dfe.selectionSet.getFields("charts/connectionCountByYear/listByTab")[0].arguments["id"]
                )

        if (dfe.selectionSet.contains("charts/byCountry/listByTab"))
            cb
                .select("jsonb_get(chartByCountryData, 'data', :dk2, 'items')", "chartByCountryData")
                .setParameter("dk2", dfe.selectionSet.getFields("charts/byCountry/listByTab")[0].arguments["id"])

        if (dfe.selectionSet.contains("charts/byIndustry/listByTab"))
            cb
                .select("jsonb_get(chartByIndustryData, 'data', :dk3, 'items')", "chartByIndustryData")
                .setParameter("dk3", dfe.selectionSet.getFields("charts/byIndustry/listByTab")[0].arguments["id"])

        if (dfe.selectionSet.contains("charts/byServicesProvided/listByService"))
            cb
                .select("jsonb_get(chartByServicesProvidedData, 'data', :dk4, 'items')", "chartByServicesProvidedData")
                .setParameter(
                    "dk4",
                    dfe.selectionSet.getFields("charts/byServicesProvided/listByService")[0].arguments["id"]
                )

        val result = cb.resultList.firstOrNull()

        val chartConnectionCountByYear = result?.getOrNull<Chart>("chartConnectionCountByYear")
        val chartByCountry = result?.getOrNull<Chart>("chartByCountry")
        val chartByIndustry = result?.getOrNull<Chart>("chartByIndustry")
        val chartByServicesProvided = result?.getOrNull<Chart>("chartByServicesProvided")
        val chartConnectionCountByYearData =
            mapRawData(
                result?.getOrNull("chartConnectionCountByYearData"),
                object : TypeReference<List<ChartListItemWithServicesCount>>() {},
            )
        val chartByCountryData =
            mapRawData(
                result?.getOrNull("chartByCountryData"),
                object : TypeReference<List<ChartListItemWithRoles>>() {}
            )
        val chartByIndustryData =
            mapRawData(
                result?.getOrNull("chartByIndustryData"),
                object : TypeReference<List<ChartListItemWithRoles>>() {}
            )
        val chartByServicesProvidedData =
            mapRawData(
                result?.getOrNull("chartByServicesProvidedData"),
                object : TypeReference<List<ChartListItemWithUsesCount>>() {}
            )

        val companyIds = collectCompanyIds(
            chartConnectionCountByYear,
            chartByCountry,
            chartByIndustry,
            chartByServicesProvided,
            chartConnectionCountByYearData,
            chartByCountryData,
            chartByIndustryData,
            chartByServicesProvidedData,
        )

        val companies: Map<UUID, CompanyReadEntity> =
            companyReadRepository
                .findByIdIsIn(companyIds.toList())
                .parallelStream()
                .collect(Collectors.toMap(CompanyReadEntity::id, Function.identity()))

        val mapTabs = { chart: Chart? ->
            chart?.tabs?.map {
                ChartTabItem(
                    id = it.id,
                    name = it.name,
                    total = it.total,
                )
            }.orEmpty()
        }

        val mapData = { chart: Chart? ->
            chart?.items?.map {
                ChartItemWithHint(
                    it.key,
                    it.name,
                    it.value,
                    hints = it.companyIds
                        .stream().map(companies::get)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList()).map { hint ->
                            ChartItemHint(
                                name = hint!!.name,
                                image = hint.data.logo?.let { logo -> Image(logo) },
                            )
                        },
                )
            }.orEmpty()
        }

        return CompanyStatistic(
            total = CompanyStatisticTotal(
                result?.getOrNull<Int>("totalConnections") ?: 0,
                result?.getOrNull<Int>("totalServicesProvided") ?: 0,
                result?.getOrNull<Int>("totalCollaborationCompanies") ?: 0,
            ),
            charts = CompanyStatisticCharts(
                ChartConnectionCountByYear(
                    data = mapData(chartConnectionCountByYear),
                    tabs = mapTabs(chartConnectionCountByYear),
                    listByTab = chartConnectionCountByYearData?.map {
                        ChartConnectionCountByYearItem(
                            company = CompanyInfoItem.fromEntity(companies[it.companyId]!!),
                            companyRole = it.companyRole,
                            companyRoleType = ConnectionCompanyRoleType.valueOf(it.companyRoleType.name),
                            servicesCount = it.servicesCount,
                        )
                    }.orEmpty(),
                ),
                ChartByCountry(
                    data = mapData(chartByCountry),
                    tabs = mapTabs(chartByCountry),
                    listByTab = chartByCountryData?.map {
                        println(it.companyId)
                        ChartByCountryItem(
                            company = CompanyInfoItem.fromEntity(companies[it.companyId]!!),
                            companyRoles = it.roles.toList(),
                        )
                    }.orEmpty(),
                ),
                ChartByIndustry(
                    data = mapData(chartByIndustry),
                    tabs = mapTabs(chartByIndustry),
                    listByTab = chartByIndustryData?.map {
                        ChartByIndustryItem(
                            company = CompanyInfoItem.fromEntity(companies[it.companyId]!!),
                            companyRoles = it.roles.toList(),
                        )
                    }.orEmpty(),
                ),
                ChartByServicesProvided(
                    data = mapData(chartByServicesProvided),
                    listByService = chartByServicesProvidedData?.map {
                        ChartByServicesProvidedItem(
                            company = CompanyInfoItem.fromEntity(companies[it.companyId]!!),
                            usesCount = it.usesCount,
                        )
                    }.orEmpty(),
                ),
            ),
        )
    }
}
