package com.briolink.companyservice.common.jpa.read.entity.statistic

import com.briolink.companyservice.common.jpa.read.entity.BaseReadEntity
import org.hibernate.annotations.Type
import java.util.UUID
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Table(name = "statistic", schema = "read")
@Entity
class StatisticReadEntity(
    @Id
    @Type(type = "pg-uuid")
    @Column(name = "company_id", nullable = false)
    val companyId: UUID,
) : BaseReadEntity() {
    @Column(name = "total_connections", nullable = false)
    var totalConnections: Int = 0

    @Column(name = "total_services_provided", nullable = false)
    var totalServicesProvided: Int = 0

    @Column(name = "total_collaboration_companies", nullable = false)
    var totalCollaborationCompanies: Int = 0

    @Type(type = "jsonb")
    @Column(name = "chart_by_country", nullable = false)
    var chartByCountry: Chart = Chart()

    @Type(type = "jsonb")
    @Column(name = "chart_by_industry", nullable = false)
    var chartByIndustry: Chart = Chart()

    @Type(type = "jsonb")
    @Column(name = "chart_connection_count_by_year", nullable = false)
    var chartConnectionCountByYear: Chart = Chart()

    @Type(type = "jsonb")
    @Column(name = "chart_by_services_provided", nullable = false)
    var chartByServicesProvided: Chart = Chart()

    @Type(type = "jsonb")
    @Column(name = "chart_by_country_data", nullable = false)
    var chartByCountryData: ChartList<ChartListItemWithRoles> = ChartList()

    @Type(type = "jsonb")
    @Column(name = "chart_by_industry_data", nullable = false)
    var chartByIndustryData: ChartList<ChartListItemWithRoles> = ChartList()

    @Type(type = "jsonb")
    @Column(name = "chart_connection_count_by_year_data", nullable = false)
    var chartConnectionCountByYearData: ChartList<ChartListItemWithServicesCount> = ChartList()

    @Type(type = "jsonb")
    @Column(name = "chart_by_services_provided_data", nullable = false)
    var chartByServicesProvidedData: ChartList<ChartListItemWithUsesCount> = ChartList()
}
