package com.briolink.companyservice.updater.dataloader

import com.briolink.companyservice.common.jpa.read.entity.ConnectionRoleReadEntity
import com.briolink.companyservice.common.jpa.read.repository.ConnectionRoleReadRepository
import com.briolink.companyservice.updater.dto.ConnectionRole
import com.briolink.companyservice.updater.dto.RoleType
import com.briolink.companyservice.updater.event.ConnectionRoleCreatedEvent
import org.springframework.context.ApplicationEventPublisher
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import java.util.*

@Component
@Order(1)
class ConnectionRoleDataLoader(
    var readRepository: ConnectionRoleReadRepository,
    private val applicationEventPublisher: ApplicationEventPublisher,
) : DataLoader() {

    override fun loadData() {
        if (readRepository.count().toInt() == 0) {
            val roleList = mutableListOf<ConnectionRole>()
            roleList.add(ConnectionRole(UUID.randomUUID(), "Customer", RoleType.Seller))
            roleList.add(ConnectionRole(UUID.randomUUID(), "Supplier", RoleType.Buyer))
            roleList.add(ConnectionRole(UUID.randomUUID(), "Investor", RoleType.Seller))
            roleList.add(ConnectionRole(UUID.randomUUID(), "Investor", RoleType.Buyer))
            roleList.add(ConnectionRole(UUID.randomUUID(), "Client", RoleType.Buyer))
            roleList.add(ConnectionRole(UUID.randomUUID(), "Vendor", RoleType.Seller))

            roleList.forEach {
                readRepository.save(
                        ConnectionRoleReadEntity(
                                id = it.id,
                                name = it.name,
                                type = ConnectionRoleReadEntity.RoleType.values()[it.type.ordinal],
//                            applicationEventPublisher.publishEvent(
//                        ConnectionRoleCreatedEvent(ConnectionRole(
//                                id = it.id,
//                                name = it.name,
//                                type = RoleType.valueOf(it.type.name)
//                        )),
                        ),
                )
            }
        }
    }

}
