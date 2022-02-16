package com.briolink.companyservice.updater.handler.user

import com.briolink.companyservice.common.jpa.read.entity.UserReadEntity
import com.briolink.companyservice.common.jpa.read.repository.UserReadRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Transactional
@Service
class UserHandlerService(
    private val userReadRepository: UserReadRepository
) {
    fun createOrUpdate(userData: UserEventData): UserReadEntity =
        userReadRepository.findById(userData.id).orElse(
            UserReadEntity(
                id = userData.id,
            ),
        ).apply {
            data = UserReadEntity.Data(
                lastName = userData.lastName,
                firstName = userData.firstName,
                image = userData.image,
            ).apply {
                slug = userData.slug
            }
            userReadRepository.save(this)
        }

    fun findById(id: UUID) = userReadRepository.findByIdOrNull(id)
}
