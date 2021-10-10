package com.briolink.companyservice.api.graphql.query

import com.briolink.companyservice.api.graphql.SecurityUtil
import com.briolink.companyservice.api.graphql.fromEntity
import com.briolink.companyservice.api.service.UserService
import com.briolink.companyservice.api.types.Role
import com.briolink.companyservice.api.types.User
import com.briolink.companyservice.api.types.UserList
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsQuery
import com.netflix.graphql.dgs.InputArgument
import org.springframework.security.access.prepost.PreAuthorize
import java.util.UUID

@DgsComponent
class EmployeesQuery(private val userService: UserService) {
    @DgsQuery
    @PreAuthorize("isAuthenticated()")
    fun getEmployees(
        @InputArgument("limit") limit: Int,
        @InputArgument("offset") offset: Int,
        @InputArgument("companyId") companyId: String
    ): UserList {
        val page = userService.getByCompanyId(UUID.fromString(companyId), limit, offset)
        return UserList(
                items = page.content.map {
                    User.fromEntity(it)
                },
                totalItems = page.totalElements.toInt(),
        )
    }
}
