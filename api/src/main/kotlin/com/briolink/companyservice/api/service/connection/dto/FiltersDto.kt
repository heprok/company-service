package com.briolink.companyservice.api.service.connection.dto

import com.briolink.companyservice.common.jpa.enumeration.ConnectionStatusEnum
import com.briolink.lib.location.model.LocationId
import java.time.Year
import java.util.UUID

data class FiltersDto(
    val collaboratorIds: List<UUID>? = null,
    val collaborationStartDate: Year? = null,
    val collaborationEndDate: Year? = null,
    val industryIds: List<UUID>? = null,
    val serviceIds: List<UUID>? = null,
    val locationId: LocationId? = null,
    val status: List<ConnectionStatusEnum>? = null
)
