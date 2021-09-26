package com.briolink.companyservice.api.graphql

import com.briolink.companyservice.api.types.Company
import com.briolink.companyservice.api.types.Image
import com.briolink.companyservice.api.types.Industry
import com.briolink.companyservice.api.types.KeyWord
import com.briolink.companyservice.api.types.Occupation
import com.briolink.companyservice.api.types.Service
import com.briolink.companyservice.api.types.SocialNetworkType
import com.briolink.companyservice.api.types.SocialProfile
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
                logo = entity.data.logo.let { Image(url = URL(entity.data.logo)) },
                country = entity.data.country,
                state = entity.data.state,
                city = entity.data.city,
                about = entity.data.about,
                isTypePublic = entity.data.isTypePublic ?: true,
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
                    )
                },
                keyWord = entity.data.keyWords.let { list ->
                    list.map { keyWord ->
                        keyWord?.let {
                            KeyWord(
                                    id = it.id,
                                    name = it.name,
                            )
                        }
                    }
                },
                socialProfile = entity.data.socialProfiles.let { list ->
                    list.map { profile ->
                        profile?.let {
                            SocialProfile(
                                    value = it.value,
                                    socialNetworkType = it.socialNetworkType.let { type ->
                                        SocialNetworkType(
                                                id = type.id,
                                                name = type.name,
                                                url = URL(type.url),
                                                logo = type.logo.let { Image(url = URL(entity.data.logo)) },
                                        )
                                    },
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
        image = entity.data.image.let { Image(url = URL(entity.data.image)) },
)

fun Service.Companion.fromEntity(entity: ServiceReadEntity) = Service(
        id = entity.id.toString(),
        name = entity.data.name,
        price = entity.data.price,
        verifiedUses = entity.data.verified_uses,
        created = entity.data.created,
        image = entity.data.image.let { Image(url = URL(entity.data.image)) },
        industriesUsed = entity.data.industries_used.let { list ->
            list.map {
                it?.let { industry ->
                    Industry(
                            id = industry.id,
                            name = industry.name,
                    )
                }
            }
        },

        )
