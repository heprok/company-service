package com.briolink.companyservice.api.graphql

import com.briolink.companyservice.api.types.Company
import com.briolink.companyservice.api.types.Connection
import com.briolink.companyservice.api.types.ConnectionService
import com.briolink.companyservice.api.types.GraphCompany
import com.briolink.companyservice.api.types.GraphService
import com.briolink.companyservice.api.types.GraphicValueCompany
import com.briolink.companyservice.api.types.GraphicValueService
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
import com.briolink.companyservice.common.jpa.read.entity.IndustryReadEntity
import com.briolink.companyservice.common.jpa.read.entity.KeywordReadEntity
import com.briolink.companyservice.common.jpa.read.entity.OccupationReadEntity
import com.briolink.companyservice.common.jpa.read.entity.ServiceReadEntity
import com.briolink.companyservice.common.jpa.read.entity.StatisticReadEntity
import com.briolink.companyservice.common.jpa.read.entity.UserReadEntity
import java.net.URL

fun Company.Companion.fromEntity(entity: CompanyReadEntity) =
        Company(
                id = entity.id.toString(),
                name = entity.data.name,
                website = URL(entity.data.website),

                logo = if (entity.data.logo == "null") null else {
                    entity.data.logo?.let { Image(url = URL(it)) }
                },
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
                            id = it.id,
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


fun User.Companion.fromEntity(entity: UserReadEntity) = User(
        id = entity.id.toString(),
        firstName = entity.data.firstName,
        lastName = entity.data.lastName,
        jobPosition = entity.data.jobPosition,
        slug = entity.data.slug,
        image = entity.data.image?.let { Image(url = it) },
)

fun Industry.Companion.fromEntity(entity: IndustryReadEntity) = Industry(
        id = entity.id!!.toString(),
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
        name = entity.data.name,
        price = entity.data.price,
        verifiedUses = entity.data.verifiedUses,
//        created = entity.data.created,
        image = entity.data.image.let { Image(url = it) },
        industry = entity.data.industry,
)

fun GraphicValueCompany.Companion.fromCompaniesStats(name: String, companiesStats: StatisticReadEntity.CompaniesStats, limit: Int = 3) =
        GraphicValueCompany(
                name = name,
                value = companiesStats.totalCount.values.sum(),
                companies = companiesStats.listCompanies.sortedBy { (_, name) -> name }.take(limit).map {
                    GraphCompany.fromEntity(it)
                },
        )

fun GraphCompany.Companion.fromEntity(entity: StatisticReadEntity.Company) = GraphCompany(
        name = entity.name,
        id = entity.id.toString(),
        slug = entity.slug,
        logo = Image(entity.logo),
)

fun GraphicValueService.Companion.fromEntity(entity: StatisticReadEntity.ServiceStats) = GraphicValueService(
        service = GraphService.fromEntity(entity.service),
        value = entity.totalCount,
)

fun GraphService.Companion.fromEntity(entity: StatisticReadEntity.Service) = GraphService(
        name = entity.name,
        slug = entity.slug,
        id = entity.id.toString(),
)

fun Connection.Companion.fromEntity(entity: ConnectionReadEntity) = Connection(
        id = entity.id.toString(),
        participantFrom = Participant(
                id = entity.data.participantFromCompany.id.toString(),
                name = entity.data.participantFromCompany.name,
                slug = entity.data.participantFromCompany.slug,
                logo = entity.data.participantFromCompany.logo?.let {
                    Image(url = it)
                },
                verifyUser = User(
                        id = entity.data.participantFromCompany.verifyUser.id.toString(),
                        lastName = entity.data.participantFromCompany.verifyUser.lastName,
                        firstName = entity.data.participantFromCompany.verifyUser.firstName,
                        slug = entity.data.participantFromCompany.verifyUser.slug,
                        image = entity.data.participantFromCompany.verifyUser.image?.let {
                            Image(url = it)
                        },
                ),
                role = entity.data.participantFromCompany.role.name,
        ),
        participantTo = Participant(
                id = entity.data.participantToCompany.id.toString(),
                name = entity.data.participantToCompany.name,
                slug = entity.data.participantToCompany.slug,
                logo = entity.data.participantToCompany.logo?.let {
                    Image(url = it)
                },
                verifyUser = User(
                        id = entity.data.participantToCompany.verifyUser.id.toString(),
                        lastName = entity.data.participantToCompany.verifyUser.lastName,
                        firstName = entity.data.participantToCompany.verifyUser.firstName,
                        slug = entity.data.participantToCompany.verifyUser.slug,
                        image = entity.data.participantToCompany.verifyUser.image?.let {
                            Image(url = it)
                        },
                ),
                role = entity.data.participantToCompany.role.name,
        ),
        services = entity.data.services.map {
            ConnectionService(
                    id = it.id.toString(),
                    name = it.name,
                    endDate = it.endDate.year,
                    startDate = it.startDate.year,
                    industry = it.industry.name,
            )
        },

        verificationStage = when (entity.verificationStage) {
            0 -> VerificationStage.PENDING
            1 -> VerificationStage.PROGRESS
            2 -> VerificationStage.VERIFIED
            else -> VerificationStage.REJECTED
        },
)
