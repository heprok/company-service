package com.briolink.companyservice.api.graphql

import com.briolink.companyservice.api.types.Company
import com.briolink.companyservice.api.types.Image
import com.briolink.companyservice.api.types.Industry
import com.briolink.companyservice.api.types.Keyword
import com.briolink.companyservice.api.types.Occupation
import com.briolink.companyservice.api.types.Service
import com.briolink.companyservice.api.types.Statistic
import com.briolink.companyservice.api.types.User
import com.briolink.companyservice.common.jpa.read.entity.CompanyReadEntity
import com.briolink.companyservice.common.jpa.read.entity.ServiceReadEntity
import com.briolink.companyservice.common.jpa.read.entity.UserReadEntity
import java.net.URL

fun Company.Companion.fromEntity(entity: CompanyReadEntity) =
        Company(
                id = entity.id.toString(),
                name = entity.data.name,
                website = URL(entity.data.website),
                logo = entity.data.logo.let { Image(url = URL(it)) },
                country = entity.data.country,
                state = entity.data.state,
                city = entity.data.city,
                slug = entity.slug,
                facebook = entity.data.facebook,
                twitter = entity.data.twitter,
                about = entity.data.about,
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
                                    id = it.id,
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

fun Service.Companion.fromEntity(entity: ServiceReadEntity) = Service(
        id = entity.id.toString(),
        name = entity.data.name,
        price = entity.data.price,
        verifiedUses = entity.data.verifiedUses,
        created = entity.data.created,
        image = entity.data.image.let { Image(url = it) },
        industry = entity.data.industry.name,
)
