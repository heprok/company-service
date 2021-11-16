package com.briolink.companyservice.api.service.connection.dto


data class SortDto<T>(
    val key: T,
    val direction: SortDirection
) {
    enum class ConnectionSortKeys {
        Id, Created
    }

    enum class SortDirection {
        ASC, DESC
    }
}
