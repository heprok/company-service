package com.briolink.companyservice.common.jpa.dto.location

import com.briolink.companyservice.common.jpa.enumration.LocationTypeEnum
import com.fasterxml.jackson.annotation.JsonProperty

class LocationId(
    @JsonProperty
    var id: Int,
    @JsonProperty
    var type: LocationTypeEnum,
) {
    companion object {
        fun fromStringId(idAndType: String): LocationId {
            val attribute = idAndType.split(";")
            return LocationId(
                id = attribute[0].toInt(),
                type = LocationTypeEnum.valueOf(attribute[1]),
            )
        }
    }
}
