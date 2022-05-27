package com.briolink.companyservice.api.graphql

import com.briolink.companyservice.api.types.Company
import com.briolink.companyservice.api.types.CompanyInfoItem
import com.briolink.companyservice.api.types.Employee
import com.briolink.companyservice.api.types.Image
import com.briolink.companyservice.api.types.Industry
import com.briolink.companyservice.api.types.Keyword
import com.briolink.companyservice.api.types.Occupation
import com.briolink.companyservice.api.types.PermissionRole
import com.briolink.companyservice.api.types.Service
import com.briolink.companyservice.api.types.User
import com.briolink.companyservice.api.types.UserJobPosition
import com.briolink.companyservice.api.types.UserPermission
import com.briolink.companyservice.common.jpa.read.entity.CompanyReadEntity
import com.briolink.companyservice.common.jpa.read.entity.EmployeeReadEntity
import com.briolink.companyservice.common.jpa.read.entity.IndustryReadEntity
import com.briolink.companyservice.common.jpa.read.entity.KeywordReadEntity
import com.briolink.companyservice.common.jpa.read.entity.OccupationReadEntity
import com.briolink.companyservice.common.jpa.read.entity.ServiceReadEntity
import com.briolink.companyservice.common.jpa.read.entity.UserJobPositionReadEntity
import com.briolink.companyservice.common.jpa.write.entity.CompanyWriteEntity
import com.briolink.lib.permission.model.UserPermissionRights

fun Company.Companion.fromEntity(entity: CompanyReadEntity) =
    Company(
        id = entity.id.toString(),
        name = entity.data.name,
        website = entity.data.website,
        logo = Image(entity.data.logo),
        slug = entity.slug,
        location = entity.data.location?.toString(),
        facebook = entity.data.facebook,
        twitter = entity.data.twitter,
        shortDescription = entity.data.shortDescription,
        description = entity.data.description,
        isTypePublic = entity.data.isTypePublic,
        industry = entity.data.industry?.let {
            Industry(
                id = it.id.toString(),
                name = it.name,
            )
        },
        occupation = entity.data.occupation?.let {
            Occupation(
                id = it.id.toString(),
                name = it.name,
            )
        },
        keywords = entity.data.keywords.let { list ->
            list.map { keyword ->
                keyword?.let {
                    Keyword(
                        id = it.id.toString(),
                        name = it.name,
                    )
                }
            }
        },
    )

fun Company.Companion.fromEntity(entity: CompanyWriteEntity) =
    Company(
        id = entity.id.toString(),
        name = entity.name,
        website = entity.websiteUrl,
        logo = Image(entity.logo),
        slug = entity.slug,
        shortDescription = entity.shortDescription,
        location = entity.getLocationId()?.toString(),
        facebook = entity.facebook,
        twitter = entity.twitter,
        description = entity.description,
        isTypePublic = entity.isTypePublic,
        industry = entity.industry?.let {
            Industry(
                id = it.id.toString(),
                name = it.name,
            )
        },
        occupation = entity.occupation?.let {
            Occupation(
                id = it.id.toString(),
                name = it.name,
            )
        },
        keywords = entity.keywords.let { list ->
            list.map { keyword ->
                keyword.let {
                    Keyword(
                        id = it.id.toString(),
                        name = it.name,
                    )
                }
            }
        },
    )

fun User.Companion.fromEntity(entity: EmployeeReadEntity) = User(
    id = entity.userId.toString(),
    firstName = entity.data.user.firstName,
    lastName = entity.data.user.lastName,
    slug = entity.data.user.slug,
    jobPosition = entity.data.jobPosition.title,
    image = entity.data.user.image?.let { Image(url = it) },
)

fun Employee.Companion.fromEntity(entity: EmployeeReadEntity) = Employee(
    user = User(
        id = entity.userId.toString(),
        firstName = entity.data.user.firstName,
        lastName = entity.data.user.lastName,
        slug = entity.data.user.slug,
        image = entity.data.user.image?.let { Image(url = it) },
    ),
    jobPositions = listOf(
        UserJobPosition(
            id = entity.data.jobPosition.id.toString(),
            title = entity.data.jobPosition.title,
            startDate = entity.data.jobPosition.startDate,
            endDate = entity.data.jobPosition.endDate,
        ),
    ),
    permission = entity.data.userPermission?.let { UserPermission.fromModel(it, false) },

)

fun UserJobPosition.Companion.fromEntity(entity: UserJobPositionReadEntity) = UserJobPosition(
    id = entity.id.toString(),
    title = entity.jobTitle,
    startDate = entity.dates.lower(),
    endDate = if (entity.dates.hasUpperBound()) entity.dates.upper() else null,

)

fun Employee.Companion.fromEntity(userJobPosition: UserJobPositionReadEntity) = Employee(
    user = User(
        id = userJobPosition.userId.toString(),
        firstName = userJobPosition.data.user.firstName,
        lastName = userJobPosition.data.user.lastName,
        slug = userJobPosition.data.user.slug,
        image = userJobPosition.data.user.image?.let { Image(url = it) },
        jobPosition = userJobPosition.jobTitle,
    ),
    jobPositions = listOf(
        UserJobPosition.fromEntity(userJobPosition),
    ),
    permission = userJobPosition.data.userPermission?.let { UserPermission.fromModel(it, true) },

)

fun Industry.Companion.fromEntity(entity: IndustryReadEntity) = Industry(
    id = entity.id.toString(),
    name = entity.name,
)

fun Keyword.Companion.fromEntity(entity: KeywordReadEntity) = Keyword(
    id = entity.id.toString(),
    name = entity.name,
)

fun Occupation.Companion.fromEntity(entity: OccupationReadEntity) = Occupation(
    id = entity.id.toString(),
    name = entity.name,
)

fun UserPermission.Companion.fromModel(model: UserPermissionRights, withRights: Boolean = true) = UserPermission(
    role = PermissionRole.valueOf(model.permissionRole.name),
    rights = if (withRights) model.permissionRights.map { it.toString() } else listOf(),
)

fun Service.Companion.fromEntity(entity: ServiceReadEntity) = Service(
    id = entity.id.toString(),
    name = entity.name,
    price = entity.price,
    companyId = entity.companyId.toString(),
    verifiedUses = entity.verifiedUses,
    lastUsed = entity.lastUsed,
    isHide = entity.hidden,
    image = entity.data.logo.let { Image(url = it) },
    slug = entity.data.slug,
)

fun CompanyInfoItem.Companion.fromEntity(entity: CompanyReadEntity) = CompanyInfoItem(
    id = entity.id.toString(),
    name = entity.data.name,
    slug = entity.slug,
    logo = entity.data.logo?.let { Image(it) },
    location = entity.data.location?.toString(),
    occupation = entity.data.occupation?.name
)
