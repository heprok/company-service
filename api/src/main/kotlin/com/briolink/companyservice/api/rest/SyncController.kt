package com.briolink.companyservice.api.rest

import com.briolink.companyservice.api.service.CompanyService
import com.briolink.companyservice.api.service.IndustryService
import com.briolink.companyservice.api.service.KeywordService
import com.briolink.companyservice.api.service.OccupationService
import com.briolink.lib.sync.model.PeriodDateTime
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.scheduling.annotation.Async
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime
import javax.validation.constraints.NotNull

@RestController
@RequestMapping("/api/v1")
class SyncController(
    private val keywordService: KeywordService,
    private val occupationService: OccupationService,
    private val industryService: IndustryService,
    private val companyService: CompanyService,
) {
    @PostMapping("sync")
    @Async
    fun sync(
        @RequestParam startLocalDateTime: String? = null,
        @RequestParam endLocalDateTime: String? = null,
        @NotNull @RequestParam syncId: Int?
    ): ResponseEntity<Any> {
        val periodLocalDateTime = if (startLocalDateTime != null && endLocalDateTime != null) PeriodDateTime(
            startDateTime = LocalDateTime.parse(startLocalDateTime), endDateTime = LocalDateTime.parse(endLocalDateTime)
        ) else null
        keywordService.publishSyncEvent(syncId!!, periodLocalDateTime)
        occupationService.publishSyncEvent(syncId, periodLocalDateTime)
        industryService.publishSyncEvent(syncId, periodLocalDateTime)
        companyService.publishSyncEvent(syncId, periodLocalDateTime)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }
}
