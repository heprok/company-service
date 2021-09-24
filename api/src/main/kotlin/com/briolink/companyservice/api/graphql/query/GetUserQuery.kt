package com.briolink.companyservice.api.graphql.query

import com.briolink.companyservice.api.service.user.UserQueryService
import com.briolink.companyservice.api.graphql.fromEntity
import com.briolink.companyservice.api.types.User
import com.briolink.companyservice.api.types.UserList
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsQuery
import com.netflix.graphql.dgs.InputArgument
import org.springframework.security.access.prepost.PreAuthorize
import java.util.UUID

@DgsComponent
class GetUserQuery(private val userQueryService: UserQueryService ) {
    @DgsQuery
//    @PreAuthorize("isAuthenticated()")
    fun getUsers(
        @InputArgument("limit") limit: Int,
        @InputArgument("offset") offset: Int,
        @InputArgument("companyId") companyId: String
    ): UserList {
        val page = userQueryService.getByCompanyId(UUID.fromString(companyId), limit, offset)
        return UserList(
                items = page.content.map {
                    User.fromEntity(it)
                },
                totalItems = page.totalElements.toInt()
        )
    }
}
