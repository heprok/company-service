package com.briolink.companyservice.common.jpa.read.entity.cte

import com.blazebit.persistence.CTE
import javax.persistence.Entity
import javax.persistence.Id

@CTE
@Entity
class RoleProjectionCte {
    @Id
    var id: String? = null
    lateinit var name: String
    var type: Int? = null
}
