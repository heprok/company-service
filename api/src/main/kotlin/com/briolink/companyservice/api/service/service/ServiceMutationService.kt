//package com.briolink.companyservice.api.service.service
//
//import com.briolink.companyservice.common.jpa.write.entity.ServiceWriteEntity
//import com.briolink.companyservice.common.jpa.write.repository.ServiceWriteRepository
//import org.springframework.stereotype.Service
//import org.springframework.transaction.annotation.Transactional
//import java.util.UUID
//
//@Service
//@Transactional
//class ServiceMutationService(private val serviceRepository: ServiceWriteRepository) {
//    fun createService(service: ServiceWriteEntity): ServiceWriteEntity = serviceRepository.save(service)
//    fun updateService(service: ServiceWriteEntity) = serviceRepository.save(service)
//    fun deleteService(id: UUID) = serviceRepository.findById(id)
//}
