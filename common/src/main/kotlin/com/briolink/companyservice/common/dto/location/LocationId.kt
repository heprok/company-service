package com.briolink.companyservice.common.dto.location

import com.briolink.companyservice.common.jpa.enumeration.LocationTypeEnum
import com.fasterxml.jackson.annotation.JsonProperty

data class LocationId(
    @JsonProperty
    var id: Int,
    @JsonProperty
    var type: LocationTypeEnum,
) {
    companion object {
        fun fromString(typeAndId: String): LocationId {
            val attribute = typeAndId.split(";")
            return LocationId(
                id = attribute[1].toInt(),
                type = LocationTypeEnum.valueOf(attribute[0]),
            )
        }
    }
}
