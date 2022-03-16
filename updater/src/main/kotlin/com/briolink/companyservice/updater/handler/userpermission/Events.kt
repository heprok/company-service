package com.briolink.companyservice.updater.handler.userpermission

import com.briolink.lib.event.Event
import com.briolink.lib.permission.enumeration.AccessObjectTypeEnum
import com.briolink.lib.permission.enumeration.PermissionRightEnum
import com.briolink.lib.permission.enumeration.PermissionRoleEnum
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.UUID

data class PermissionRoleEventData(
    @JsonProperty
    val role: PermissionRoleEnum,
    @JsonProperty
    val level: Int
)

data class UserPermissionEventData(
    @JsonProperty
    val id: UUID,
    @JsonProperty
    val userId: UUID,
    @JsonProperty
    val accessObjectId: UUID,
    @JsonProperty
    val permissionRole: PermissionRoleEventData,
    @JsonProperty
    val accessObjectType: AccessObjectTypeEnum,
    @JsonProperty
    val enablePermissionRights: ArrayList<PermissionRightEnum>
)

data class UserPermissionDeletedEventData(
    @JsonProperty
    val id: UUID,
    @JsonProperty
    val userId: UUID,
    @JsonProperty
    val accessObjectId: UUID,
    @JsonProperty
    val accessObjectType: AccessObjectTypeEnum,
)

data class UserPermissionCreatedEvent(override val data: UserPermissionEventData) :
    Event<UserPermissionEventData>("1.0")

data class UserPermissionDeletedEvent(override val data: UserPermissionDeletedEventData) :
    Event<UserPermissionDeletedEventData>("1.0")
