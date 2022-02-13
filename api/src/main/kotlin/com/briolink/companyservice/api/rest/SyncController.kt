package com.briolink.companyservice.api.rest

import com.briolink.companyservice.common.event.v1_0.CompanySyncEvent
import com.briolink.companyservice.common.jpa.write.repository.CompanyWriteRepository
import com.briolink.event.publisher.EventPublisher
import com.briolink.lib.sync.enumeration.ServiceEnum
import org.springframework.data.domain.PageRequest
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime
import java.time.ZoneOffset

@RestController
@RequestMapping("/api/v1")
class SyncController(
    private val eventPublisher: EventPublisher,
    private val companyWriteRepository: CompanyWriteRepository
) {
    @PostMapping("sync")
    fun sync(
        @RequestParam startLocalDateTime: String? = null,
        @RequestParam endLocalDateTime: String? = null,
        @RequestParam syncId: Int?
    ) {

        val startInstant = startLocalDateTime?.let { LocalDateTime.parse(startLocalDateTime).toInstant(ZoneOffset.UTC) }
        val endInstant = endLocalDateTime?.let { LocalDateTime.parse(endLocalDateTime).toInstant(ZoneOffset.UTC) }
        var pageRequest = PageRequest.of(0, 200)
        var pageCompany = if (startInstant == null || endInstant == null) companyWriteRepository.findAll(pageRequest)
        else companyWriteRepository.findByCreatedOrChangedBetween(startInstant, endInstant, pageRequest)
        var indexElement = 0
        while (!pageCompany.isEmpty) {
            pageRequest = pageRequest.next()
            pageCompany.content.forEach {
                indexElement += 1
                println(indexElement)
                eventPublisher.publish(
                    CompanySyncEvent(
                        rowId = indexElement,
                        service = ServiceEnum.Company,
                        totalRow = pageCompany.totalElements.toInt(),
                        syncServiceId = syncId ?: 0,
                        data = it.toDomain()
                    )
                )
            }
            pageCompany = if (startInstant == null || endInstant == null) companyWriteRepository.findAll(pageRequest)
            else companyWriteRepository.findByCreatedOrChangedBetween(startInstant, endInstant, pageRequest)
        }
    }
}
