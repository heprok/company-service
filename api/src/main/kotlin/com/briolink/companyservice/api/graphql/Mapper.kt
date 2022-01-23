package com.briolink.companyservice.api.graphql

import com.briolink.companyservice.api.types.Company
import com.briolink.companyservice.api.types.CompanyInfoByServiceItem
import com.briolink.companyservice.api.types.CompanyInfoItem
import com.briolink.companyservice.api.types.Connection
import com.briolink.companyservice.api.types.ConnectionCompanyRole
import com.briolink.companyservice.api.types.ConnectionCompanyRoleType
import com.briolink.companyservice.api.types.ConnectionParticipant
import com.briolink.companyservice.api.types.ConnectionService
import com.briolink.companyservice.api.types.ConnectionStatus
import com.briolink.companyservice.api.types.Image
import com.briolink.companyservice.api.types.Industry
import com.briolink.companyservice.api.types.Keyword
import com.briolink.companyservice.api.types.Occupation
import com.briolink.companyservice.api.types.PermissionRight
import com.briolink.companyservice.api.types.PermissionRole
import com.briolink.companyservice.api.types.Service
import com.briolink.companyservice.api.types.User
import com.briolink.companyservice.api.types.UserJobPosition
import com.briolink.companyservice.api.types.UserPermission
import com.briolink.companyservice.common.jpa.read.entity.CompanyReadEntity
import com.briolink.companyservice.common.jpa.read.entity.ConnectionReadEntity
import com.briolink.companyservice.common.jpa.read.entity.ConnectionServiceReadEntity
import com.briolink.companyservice.common.jpa.read.entity.EmployeeReadEntity
import com.briolink.companyservice.common.jpa.read.entity.IndustryReadEntity
import com.briolink.companyservice.common.jpa.read.entity.KeywordReadEntity
import com.briolink.companyservice.common.jpa.read.entity.OccupationReadEntity
import com.briolink.companyservice.common.jpa.read.entity.ServiceReadEntity
import com.briolink.companyservice.common.jpa.write.entity.CompanyWriteEntity
import com.briolink.permission.model.UserPermissionRights

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
    jobPosition = entity.data.jobPosition,
    slug = entity.data.user.slug,
    image = entity.data.user.image?.let { Image(url = it) },
)

fun UserJobPosition.Companion.fromEntity(entity:)
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

fun UserPermission.Companion.fromModel(model: UserPermissionRights) = UserPermission(
    role = PermissionRole.valueOf(model.permissionRole.name),
    rights = model.permissionRights.map { PermissionRight.valueOf(it.name) }
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
)

fun CompanyInfoItem.Companion.fromEntity(entity: ConnectionServiceReadEntity.Company) = CompanyInfoItem(
    id = entity.id.toString(),
    name = entity.name,
    slug = entity.slug,
    logo = entity.logo?.let { Image(it) },
    location = entity.location,
)

fun CompanyInfoByServiceItem.Companion.fromEntity(entity: ConnectionServiceReadEntity) = CompanyInfoByServiceItem(
    company = CompanyInfoItem.fromEntity(entity.data.company),
    industry = entity.data.company.industryName,
    companyRole = entity.data.roleName,
    periodUsedStart = entity.data.periodUsedStart,
    periodUsedEnd = entity.data.periodUsedEnd,
)

fun Connection.Companion.fromEntity(entity: ConnectionReadEntity) = Connection(
    id = entity.id.toString(),
    participantFrom = ConnectionParticipant(
        user = User(
            id = entity.data.participantFrom.user.id.toString(),
            slug = entity.data.participantFrom.user.slug,
            image = entity.data.participantFrom.user.image?.let { image -> Image(image) },
            firstName = entity.data.participantFrom.user.firstName,
            lastName = entity.data.participantFrom.user.lastName,
        ),
        userJobPositionTitle = null,
        company = CompanyInfoItem(
            id = entity.data.participantFrom.company.id.toString(),
            slug = entity.data.participantFrom.company.slug,
            logo = entity.data.participantFrom.company.logo?.let { logo -> Image(logo) },
            name = entity.data.participantFrom.company.name,
            occupation = entity.data.participantFrom.company.occupation,
        ),
        companyRole = ConnectionCompanyRole(
            id = entity.participantFromRoleId.toString(),
            name = entity.participantFromRoleName,
            type = ConnectionCompanyRoleType.valueOf(entity.participantFromRoleType.name),
        ),
    ),
    participantTo = ConnectionParticipant(
        user = User(
            id = entity.data.participantTo.user.id.toString(),
            slug = entity.data.participantTo.user.slug,
            image = entity.data.participantTo.user.image?.let { image -> Image(image) },
            firstName = entity.data.participantTo.user.firstName,
            lastName = entity.data.participantTo.user.lastName,
        ),
        userJobPositionTitle = null,
        company = CompanyInfoItem(
            id = entity.data.participantTo.company.id.toString(),
            slug = entity.data.participantTo.company.slug,
            logo = entity.data.participantTo.company.logo?.let { logo -> Image(logo) },
            name = entity.data.participantTo.company.name,
            occupation = entity.data.participantTo.company.occupation,
        ),
        companyRole = ConnectionCompanyRole(
            id = entity.participantToRoleId.toString(),
            name = entity.participantToRoleName,
            type = ConnectionCompanyRoleType.valueOf(entity.participantToRoleType.name),
        ),
    ),
    services = entity.data.services.map { service ->
        ConnectionService(
            id = service.id.toString(),
            serviceId = service.serviceId?.toString(),
            name = service.serviceName,
            startDate = service.startDate,
            endDate = service.endDate,
            slug = service.slug,
        )
    },
    status = ConnectionStatus.valueOf(entity.status.name),
    industry = entity.data.industry,
)
