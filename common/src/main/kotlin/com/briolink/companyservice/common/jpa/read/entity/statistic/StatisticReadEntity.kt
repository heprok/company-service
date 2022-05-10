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
    @Column(name = "total_projects", nullable = false)
    var totalProjects: Int = 0

    @Column(name = "total_services_provided", nullable = false)
    var totalServicesProvided: Int = 0

    @Column(name = "total_connected_companies", nullable = false)
    var totalConnectedCompanies: Int = 0

    @Column(name = "total_connected_peoples", nullable = false)
    var totalConnectedPeoples: Int = 0

    val totalConnections: Int
        get() = totalConnectedCompanies + totalConnectedPeoples

    @Type(type = "jsonb")
    @Column(name = "chart_by_country", nullable = false)
    var chartByCountry: Chart = Chart()

    @Type(type = "jsonb")
    @Column(name = "chart_by_industry", nullable = false)
    var chartByIndustry: Chart = Chart()

    @Type(type = "jsonb")
    @Column(name = "chart_active_project_by_year", nullable = false)
    var chartActiveProjectByYear: Chart = Chart()

    @Type(type = "jsonb")
    @Column(name = "chart_new_project_by_year", nullable = false)
    var chartNewProjectByYear: Chart = Chart()

    @Type(type = "jsonb")
    @Column(name = "chart_by_services_provided", nullable = false)
    var chartByServicesProvided: Chart = Chart()

    @Type(type = "jsonb")
    @Column(name = "chart_by_country_data", nullable = false)
    var chartByCountryData: ChartList<ChartListItemWithVerifiedProjects> = ChartList()

    @Type(type = "jsonb")
    @Column(name = "chart_by_industry_data", nullable = false)
    var chartByIndustryData: ChartList<ChartListItemWithVerifiedProjects> = ChartList()

    @Type(type = "jsonb")
    @Column(name = "chart_active_project_by_year_data", nullable = false)
    var chartActiveProjectByYearData: ChartList<ChartListItemWithRoleAndProject> = ChartList()

    @Type(type = "jsonb")
    @Column(name = "chart_new_project_by_year_data", nullable = false)
    var chartNewProjectByYearData: ChartList<ChartListItemWithRoleAndProject> = ChartList()

    @Type(type = "jsonb")
    @Column(name = "chart_by_services_provided_data", nullable = false)
    var chartByServicesProvidedData: ChartList<ChartListItemWithVerifyUsed> = ChartList()
}
