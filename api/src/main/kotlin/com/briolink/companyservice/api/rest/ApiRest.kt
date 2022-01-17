package com.briolink.companyservice.api.rest

import com.briolink.companyservice.api.dataloader.CompanyDataLoader
import com.briolink.companyservice.api.dataloader.IndustryDataLoader
import com.briolink.companyservice.api.dataloader.KeywordDataLoader
import com.briolink.companyservice.api.dataloader.OccupationDataLoader
import com.briolink.companyservice.api.service.CompanyService
import com.briolink.companyservice.common.domain.v1_0.Statistic
import com.briolink.companyservice.common.event.v1_0.StatisticRefreshEvent
import com.briolink.event.publisher.EventPublisher
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1")
class ApiRest(
    private val eventPublisher: EventPublisher,
    private val companyService: CompanyService,
    private val companyDataLoader: CompanyDataLoader,
    private val industryDataLoader: IndustryDataLoader,
    private val occupationDataLoader: OccupationDataLoader,
    private val keywordDataLoader: KeywordDataLoader,
) {
    @GetMapping("/statistic/refresh")
    fun refreshStatistic(): ResponseEntity<Int> {

        eventPublisher.publishAsync(
            StatisticRefreshEvent(Statistic(null)),
        )
        return ResponseEntity.ok(1)
    }

    @GetMapping("/generator/data")
    fun loadData(): ResponseEntity<Int> {
        industryDataLoader.loadData()
        occupationDataLoader.loadData()
        keywordDataLoader.loadData()
        companyDataLoader.loadData()
        return ResponseEntity.ok(1)
    }

//    @GetMapping("/sync/search-service")
//    fun syncSearchService(): ResponseEntity<Int> {
//        companyService.publishSyncEvent()
//        return ResponseEntity.ok(1)
//    }

    @GetMapping("/load-logo/{pathCsv}")
    fun loadLogoCompany(@PathVariable pathCsv: String): ResponseEntity<Int> {
        companyService.importLogoFromCSV(pathCsv)
        return ResponseEntity.ok(1)
    }
}
