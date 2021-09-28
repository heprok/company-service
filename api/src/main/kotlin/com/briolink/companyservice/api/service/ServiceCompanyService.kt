package com.briolink.companyservice.api.service

import com.briolink.companyservice.common.jpa.read.entity.ServiceReadEntity
import com.briolink.companyservice.common.jpa.read.repository.ServiceReadRepository
import com.briolink.companyservice.common.jpa.write.entity.UserWriteEntity
import com.briolink.companyservice.common.jpa.write.repository.UserWriteRepository
import com.briolink.companyservice.common.util.PageRequest
import org.springframework.data.domain.Page
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class ServiceCompanyService(
    private val serviceReadRepository: ServiceReadRepository,
    private val userRepository: UserWriteRepository
) {
    fun createUser(user: UserWriteEntity): UserWriteEntity = userRepository.save(user)
    fun updateUser(user: UserWriteEntity) = userRepository.save(user)
    fun deleteUser(id: UUID) = userRepository.findById(id)
    fun getByCompanyId(id: UUID, limit: Int, offset: Int): Page<ServiceReadEntity> =
            serviceReadRepository.findByCompanyIdIs(id, PageRequest(offset, limit))
}
