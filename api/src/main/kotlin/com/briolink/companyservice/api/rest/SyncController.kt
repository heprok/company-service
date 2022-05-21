package com.briolink.companyservice.api.rest

import com.briolink.companyservice.api.service.CompanyService
import com.briolink.companyservice.api.service.IndustryService
import com.briolink.companyservice.api.service.KeywordService
import com.briolink.companyservice.api.service.OccupationService
import com.briolink.lib.sync.AbstractSyncController
import com.briolink.lib.sync.model.PeriodDateTime
import org.springframework.http.ResponseEntity
import org.springframework.scheduling.annotation.Async
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import javax.validation.constraints.NotNull

@RestController
@RequestMapping("/api/v1")
class SyncController(
    private val keywordService: KeywordService,
    private val occupationService: OccupationService,
    private val industryService: IndustryService,
    private val companyService: CompanyService,
) : AbstractSyncController() {
    // @PreAuthorize("@blServletUtils.isIntranet()")
    @Async
    @PostMapping("sync")
    fun postSync(
        @RequestParam startLocalDateTime: String? = null,
        @RequestParam endLocalDateTime: String? = null,
        @NotNull @RequestParam syncId: Int
    ): ResponseEntity<Any> {
        return super.sync(startLocalDateTime, endLocalDateTime, syncId)
    }

    override fun publishSyncEvent(syncId: Int, period: PeriodDateTime?) {
        keywordService.publishSyncEvent(syncId, period)
        occupationService.publishSyncEvent(syncId, period)
        industryService.publishSyncEvent(syncId, period)
        companyService.publishSyncEvent(syncId, period)
    }
}
