package com.briolink.companyservice.updater.handler.userpermission

import com.briolink.companyservice.updater.handler.userjobposition.UserJobPositionHandlerService
import com.briolink.event.IEventHandler
import com.briolink.event.annotation.EventHandler
import com.briolink.event.annotation.EventHandlers
import com.briolink.lib.permission.enumeration.AccessObjectTypeEnum

@EventHandlers(
    EventHandler("UserPermissionCreatedEvent", "1.0"),
    EventHandler("UserPermissionUpdatedEvent", "1.0")
)
class UserPermissionCreatedEventHandler(
    private val userPermissionHandlerService: UserPermissionHandlerService,
    private val userJobPositionHandlerService: UserJobPositionHandlerService,
) : IEventHandler<UserPermissionCreatedEvent> {
    override fun handle(event: UserPermissionCreatedEvent) {
        if (event.data.accessObjectType == AccessObjectTypeEnum.Company)
            userPermissionHandlerService.createOrUpdateCompanyPermission(event.data).also {
                userJobPositionHandlerService.addUserPermission(it)
            }
    }
}

@EventHandler("UserPermissionDeletedEvent", "1.0")
class UserPermissionDeletedEventHandler(
    private val userJobPositionHandlerService: UserJobPositionHandlerService,
    private val userPermissionHandlerService: UserPermissionHandlerService,
) : IEventHandler<UserPermissionDeletedEvent> {
    override fun handle(event: UserPermissionDeletedEvent) {
        if (event.data.accessObjectType == AccessObjectTypeEnum.Company) {
            userPermissionHandlerService.deletePermission(event.data.id)
            userJobPositionHandlerService.deleteUserPermission(
                userId = event.data.userId,
                companyId = event.data.accessObjectId
            )
        }
    }
}
