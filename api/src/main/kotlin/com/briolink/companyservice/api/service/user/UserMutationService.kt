package com.briolink.companyservice.api.service.user

import com.briolink.companyservice.common.jpa.write.entity.UserWriteEntity
import com.briolink.companyservice.common.jpa.write.repository.UserWriteRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional
class UserMutationService(private val userRepository: UserWriteRepository) {
    fun createUser(user: UserWriteEntity): UserWriteEntity = userRepository.save(user)
    fun updateUser(user: UserWriteEntity) = userRepository.save(user)
    fun deleteUser(id: UUID) = userRepository.findById(id)
}
