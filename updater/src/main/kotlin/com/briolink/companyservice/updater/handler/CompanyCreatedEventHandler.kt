package com.briolink.companyservice.updater.handler

import com.briolink.companyservice.common.event.v1_0.CompanyCreatedEvent
import com.briolink.companyservice.common.jpa.read.entity.CompanyReadEntity
import com.briolink.companyservice.common.jpa.read.repository.CompanyReadRepository
import com.briolink.event.IEventHandler
import com.briolink.event.annotation.EventHandler
import com.briolink.companyservice.common.jpa.write.repository.CompanyWriteRepository
import java.util.*

@EventHandler("CompanyCreatedEvent", "1.0")
class CompanyCreatedEventHandler(
    private val companyReadRepository: CompanyReadRepository,
) : IEventHandler<CompanyCreatedEvent> {
    override fun handle(event: CompanyCreatedEvent) {
        val company = event.data
        companyReadRepository.save(
                CompanyReadEntity(
                        id = company.id!!,
                        slug = company.slug!!,
                ).apply {
                    data = CompanyReadEntity.Data(
                            name = company.name,
                            website = company.website.toString(),
                            location = company.location,
                            facebook = company.facebook,
                            twitter = company.twitter,
                            isTypePublic = company.isTypePublic,
                            logo = company.logo.toString(),
                            description = company.description,
                            statistic = CompanyReadEntity.Statistic(),
                            industry = company.industry?.let { CompanyReadEntity.Industry(it.id.toString(), it.name) },
                            keywords = company.keywords?.let { list ->
                                list.map {
                                    CompanyReadEntity.Keyword(
                                            it.id,
                                            it.name,
                                    )
                                }
                            } ?: mutableListOf(),
                            occupation = company.occupation?.let { CompanyReadEntity.Occupation(it.id.toString(), it.name) },
                            )
                },
        )
    }
}
