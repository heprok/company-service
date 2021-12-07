package com.briolink.companyservice.api.rest

import com.briolink.companyservice.api.dataloader.CompanyDataLoader
import com.briolink.companyservice.api.dataloader.IndustryDataLoader
import com.briolink.companyservice.api.dataloader.KeywordDataLoader
import com.briolink.companyservice.api.dataloader.OccupationDataLoader
import com.briolink.companyservice.common.domain.v1_0.*
import com.briolink.companyservice.common.event.v1_0.CompanyConnectionEvent
import com.briolink.companyservice.common.event.v1_0.StatisticRefreshEvent
import com.briolink.companyservice.common.jpa.read.repository.ConnectionReadRepository
import com.briolink.event.publisher.EventPublisher
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1")
class ApiRest(
    private val eventPublisher: EventPublisher,
    private val companyDataLoader: CompanyDataLoader,
    private val industryDataLoader: IndustryDataLoader,
    private val occupationDataLoader: OccupationDataLoader,
    private val keywordDataLoader: KeywordDataLoader,
    private val connectionReadRepository: ConnectionReadRepository,
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

    @GetMapping("/connection")
    fun loadDataConnection(): ResponseEntity<Int> {
        connectionReadRepository.findAll().forEach {
            eventPublisher.publish(
                CompanyConnectionEvent(
                    Connection(
                        id = it.id,
                        participantFrom = ConnectionParticipant(
                            userId = it.participantFromUserId,
                            userJobPositionTitle = it.data.participantFrom.userJobPositionTitle ?: "",
                            companyId = it.participantFromCompanyId,
                            companyRole = ConnectionCompanyRole(
                                id = it.data.participantFrom.companyRole.id,
                                name = it.data.participantFrom.companyRole.name,
                                type = it.data.participantFrom.companyRole.type.let { if (it.value == 0) ConnectionCompanyRoleType.Buyer else ConnectionCompanyRoleType.Seller }
                            )
                        ),
                        participantTo = ConnectionParticipant(
                            userId = it.participantToUserId,
                            userJobPositionTitle = it.data.participantTo.userJobPositionTitle ?: "",
                            companyId = it.participantToCompanyId,
                            companyRole = ConnectionCompanyRole(
                                id = it.data.participantTo.companyRole.id,
                                name = it.data.participantTo.companyRole.name,
                                type = it.data.participantTo.companyRole.type.let { if (it.value == 0) ConnectionCompanyRoleType.Buyer else ConnectionCompanyRoleType.Seller }
                            )
                        ),
                        services = it.data.services.map {
                            ConnectionService(
                                id = it.id,
                                serviceId = it.serviceId,
                                serviceName = it.serviceName,
                                startDate = it.startDate,
                                endDate = it.endDate
                            )
                        } as ArrayList<ConnectionService>,
                        status = ConnectionStatus.valueOf(it.status.name),
                        created = it.created
                    )
                )
            )
        }
        return ResponseEntity.ok(1)
    }
}
