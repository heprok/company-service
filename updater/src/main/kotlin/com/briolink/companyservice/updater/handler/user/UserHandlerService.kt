package com.briolink.companyservice.updater.handler.user

import com.briolink.companyservice.common.jpa.read.entity.UserReadEntity
import com.briolink.companyservice.common.jpa.read.repository.UserReadRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional
@Service
class UserHandlerService(
    private val userReadRepository: UserReadRepository
) {
    fun createOrUpdate(user: User): UserReadEntity =
            userReadRepository.findById(user.id).orElse(
                    UserReadEntity(
                            id = user.id,
                    ),
            ).apply {
                data = UserReadEntity.Data(
                        lastName = user.lastName,
                        firstName = user.firstName,
                        image = user.image,
                ).apply {
                    slug = user.slug
                }
                userReadRepository.save(this)
            }
}
