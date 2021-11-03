package com.briolink.companyservice.api.graphql

import com.briolink.companyservice.api.types.ChartCompany
import com.briolink.companyservice.api.types.Company
import com.briolink.companyservice.api.types.Connection
import com.briolink.companyservice.api.types.ConnectionRole
import com.briolink.companyservice.api.types.ConnectionRoleType
import com.briolink.companyservice.api.types.ConnectionService
import com.briolink.companyservice.api.types.Image
import com.briolink.companyservice.api.types.Industry
import com.briolink.companyservice.api.types.Keyword
import com.briolink.companyservice.api.types.Occupation
import com.briolink.companyservice.api.types.Participant
import com.briolink.companyservice.api.types.Service
import com.briolink.companyservice.api.types.Statistic
import com.briolink.companyservice.api.types.User
import com.briolink.companyservice.api.types.VerificationStage
import com.briolink.companyservice.common.jpa.read.entity.CompanyReadEntity
import com.briolink.companyservice.common.jpa.read.entity.ConnectionReadEntity
import com.briolink.companyservice.common.jpa.read.entity.ConnectionRoleReadEntity
import com.briolink.companyservice.common.jpa.read.entity.IndustryReadEntity
import com.briolink.companyservice.common.jpa.read.entity.KeywordReadEntity
import com.briolink.companyservice.common.jpa.read.entity.OccupationReadEntity
import com.briolink.companyservice.common.jpa.read.entity.ServiceReadEntity
import com.briolink.companyservice.common.jpa.read.entity.StatisticReadEntity
import com.briolink.companyservice.common.jpa.read.entity.UserJobPositionReadEntity

fun Company.Companion.fromEntity(entity: CompanyReadEntity) =
        Company(
                id = entity.id.toString(),
                name = entity.data.name,
                website = entity.data.website,
                logo = Image(entity.data.logo),
//                country = entity.data.country,
//                state = entity.data.state,
//                city = entity.data.city,
                slug = entity.slug,
                location = entity.data.location,
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
                            id = it.id,
                            name = it.name,
                    )
                },
                statistic = entity.data.statistic.let {
                    Statistic(
                            serviceProvidedCount = it.serviceProvidedCount,
                            collaboratingCompanyCount = it.collaboratingCompanyCount,
                            collaboratingPeopleCount = it.collaboratingPeopleCount,
                            totalConnectionCount = it.totalConnectionCount,
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


fun User.Companion.fromEntity(entity: UserJobPositionReadEntity) = User(
        id = entity.userId.toString(),
        firstName = entity.data.user.firstName,
        lastName = entity.data.user.lastName,
        jobPosition = entity.data.title,
        slug = entity.data.user.slug,
        image = entity.data.user.image?.let { Image(url = it) },
)

fun Industry.Companion.fromEntity(entity: IndustryReadEntity) = Industry(
        id = entity.id.toString(),
        name = entity.name,
)

fun Keyword.Companion.fromEntity(entity: KeywordReadEntity) = Keyword(
        id = entity.id!!.toString(),
        name = entity.name,
)

fun Occupation.Companion.fromEntity(entity: OccupationReadEntity) = Occupation(
        id = entity.id!!.toString(),
        name = entity.name,
)

fun Service.Companion.fromEntity(entity: ServiceReadEntity) = Service(
        id = entity.id.toString(),
        name = entity.name,
        price = entity.price,
        companyId = entity.companyId.toString(),
        verifiedUses = entity.verifiedUses,
        lastUsed = entity.lastUsed,
        isHide = entity.isHide,
        image = entity.data.logo.let { Image(url = it) },
        slug = entity.data.slug
)


fun ChartCompany.Companion.fromEntity(entity: StatisticReadEntity.Company) =
        ChartCompany(
                name = entity.name,
                countService = entity.countService,
                logo = Image(entity.logo),
                slug = entity.slug,
                role = entity.role.map { ConnectionRole.fromEntity(it) },
                industry = entity.industry,
                location = entity.location,
                id = entity.id.toString(),
        )


fun ConnectionRole.Companion.fromEntity(entity: ConnectionRoleReadEntity) = ConnectionRole(
        id = entity.id.toString(),
        name = entity.name,
        type = ConnectionRoleType.values()[entity.type.ordinal],
)

fun ConnectionRole.Companion.fromEntity(entity: StatisticReadEntity.Role) = ConnectionRole(
        id = entity.id.toString(),
        name = entity.name,
        type = ConnectionRoleType.values()[entity.type.ordinal],
)

fun ConnectionRole.Companion.fromEntity(entity: ConnectionReadEntity.Role) = ConnectionRole(
        id = entity.id.toString(),
        name = entity.name,
        type = ConnectionRoleType.values()[entity.type.ordinal],
)

fun Connection.Companion.fromEntity(entity: ConnectionReadEntity) = Connection(
        id = entity.id.toString(),
        buyer = Participant(
                id = entity.data.sellerCompany.id.toString(),
                name = entity.data.sellerCompany.name,
                slug = entity.data.sellerCompany.slug,
                logo = entity.data.sellerCompany.logo?.let {
                    Image(url = it)
                },
                verifyUser = User(
                        id = entity.data.sellerCompany.verifyUser.id.toString(),
                        lastName = entity.data.sellerCompany.verifyUser.lastName,
                        firstName = entity.data.sellerCompany.verifyUser.firstName,
                        slug = entity.data.sellerCompany.verifyUser.slug,
                        image = entity.data.sellerCompany.verifyUser.image?.let {
                            Image(url = it)
                        },
                ),
                role = ConnectionRole.fromEntity(entity.data.sellerCompany.role),
        ),
        seller = Participant(
                id = entity.data.buyerCompany.id.toString(),
                name = entity.data.buyerCompany.name,
                slug = entity.data.buyerCompany.slug,
                logo = entity.data.buyerCompany.logo?.let {
                    Image(url = it)
                },
                verifyUser = User(
                        id = entity.data.buyerCompany.verifyUser.id.toString(),
                        lastName = entity.data.buyerCompany.verifyUser.lastName,
                        firstName = entity.data.buyerCompany.verifyUser.firstName,
                        slug = entity.data.buyerCompany.verifyUser.slug,
                        image = entity.data.buyerCompany.verifyUser.image?.let {
                            Image(url = it)
                        },
                ),
                role = ConnectionRole.fromEntity(entity.data.buyerCompany.role),
        ),
        services = entity.data.services.map {
            ConnectionService(
                    id = it.id.toString(),
                    name = it.name!!,
                    endDate = it.endDate,
                    startDate = it.startDate,
            )
        },
        industry = entity.data.industry.let {
            Industry(id = it.id.toString(), name = it.name)
        },
        verificationStage = VerificationStage.valueOf(entity.verificationStage.name)
)
