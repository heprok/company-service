package com.briolink.companyservice.updater.handler

import com.briolink.companyservice.common.event.v1_0.CompanyUpdatedEvent
import com.briolink.event.IEventHandler
import com.briolink.event.annotation.EventHandler
import com.briolink.companyservice.common.jpa.read.entity.CompanyReadEntity
import com.briolink.companyservice.common.jpa.read.repository.CompanyReadRepository
import javax.persistence.EntityNotFoundException

@EventHandler("CompanyUpdatedEvent", "1.0")
class CompanyUpdatedEventHandler(private val companyReadRepository: CompanyReadRepository) : IEventHandler<CompanyUpdatedEvent> {
    override fun handle(event: CompanyUpdatedEvent) {
        val data = event.data

        val entity: CompanyReadEntity = try {
            companyReadRepository.getById(data.id!!)
        } catch (e: EntityNotFoundException) {
            throw EntityNotFoundException(e.message)
        }

        entity.slug = data.slug!!
        entity.data.name = data.name
        entity.data.website = data.website
        entity.data.description = data.description
        entity.data.logo = data.logo?.toString()
        entity.data.isTypePublic = data.isTypePublic
//        entity.data.country = data.country ?: entity.data.country
//        entity.data.state = data.state ?: entity.data.state
//        entity.data.city = data.city ?: entity.data.city
        entity.data.location = data.location
        entity.data.facebook = data.facebook
        entity.data.twitter = data.twitter
        entity.data.occupation = data.occupation?.let {
            CompanyReadEntity.Occupation(it.id.toString(), it.name)
        }
        entity.data.industry = data.industry?.let {
            CompanyReadEntity.Industry(it.id.toString(), it.name)
        }
        entity.data.keywords = data.keywords?.let { list ->
            list.map {
                CompanyReadEntity.Keyword(
                        it.id,
                        it.name,
                )
            }
        } ?: entity.data.keywords
        companyReadRepository.save(entity)
    }
}
