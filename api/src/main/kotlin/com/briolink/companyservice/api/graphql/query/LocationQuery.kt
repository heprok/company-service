package com.briolink.companyservice.api.graphql.query

import com.briolink.companyservice.api.types.Location
import com.briolink.lib.location.service.LocationService
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsQuery
import com.netflix.graphql.dgs.InputArgument

@DgsComponent
class LocationQuery(
    private val locationService: LocationService
) {
    @DgsQuery
    fun getLocations(@InputArgument query: String?): List<Location> {
        return locationService.getSuggestionLocation(query)?.map {
            Location(id = it.locationId.toString(), name = it.name)
        } ?: listOf()
    }
}
