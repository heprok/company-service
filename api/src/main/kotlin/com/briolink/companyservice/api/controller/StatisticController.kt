package com.briolink.companyservice.api.controller

import com.briolink.companyservice.common.domain.v1_0.Statistic
import com.briolink.companyservice.common.event.v1_0.CompanyCreatedEvent
import com.briolink.companyservice.common.event.v1_0.StatisticRefreshEvent
import org.springframework.context.ApplicationEventPublisher
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1")
class StatisticController(
    private val applicationEventPublisher: ApplicationEventPublisher,
) {
    @GetMapping("/statistic/refresh")
    fun refreshStatistic(): ResponseEntity<Int> {
        applicationEventPublisher.publishEvent(
                StatisticRefreshEvent(Statistic("refresh")),
        )
        return ResponseEntity.ok(1)
    }
}
