package com.briolink.companyservice.updater.dataloader

import com.briolink.companyservice.common.jpa.read.entity.ConnectionRoleReadEntity
import com.briolink.companyservice.common.jpa.read.repository.connection.ConnectionRoleReadRepository
import com.briolink.companyservice.updater.dto.ConnectionRole
import com.briolink.companyservice.updater.dto.ConnectionCompanyRoleType
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import java.util.*

@Component
@Order(1)
class ConnectionRoleDataLoader(
    var connectionRoleReadRepository: ConnectionRoleReadRepository,
) : DataLoader() {

    override fun loadData() {
        if (connectionRoleReadRepository.count().toInt() == 0) {
            val roleList = mutableListOf<ConnectionRole>()
            roleList.add(ConnectionRole(UUID.randomUUID(), "Customer", ConnectionCompanyRoleType.Seller))
            roleList.add(ConnectionRole(UUID.randomUUID(), "Supplier", ConnectionCompanyRoleType.Buyer))
            roleList.add(ConnectionRole(UUID.randomUUID(), "Investor", ConnectionCompanyRoleType.Seller))
            roleList.add(ConnectionRole(UUID.randomUUID(), "Investor", ConnectionCompanyRoleType.Buyer))
            roleList.add(ConnectionRole(UUID.randomUUID(), "Client", ConnectionCompanyRoleType.Buyer))
            roleList.add(ConnectionRole(UUID.randomUUID(), "Vendor", ConnectionCompanyRoleType.Seller))

            roleList.forEach {
                connectionRoleReadRepository.save(
                        ConnectionRoleReadEntity(
                                id = it.id,
                                name = it.name,
                                type = ConnectionRoleReadEntity.RoleType.values()[it.type.ordinal],
                        ),
                )
            }
        }
    }

}
