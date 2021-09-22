package com.briolink.companyservice.api.graphql

import com.briolink.companyservice.api.types.Company
import com.briolink.companyservice.api.types.Image
import com.briolink.companyservice.api.types.Industry
import com.briolink.companyservice.api.types.Occupation
import com.briolink.companyservice.api.types.User
import com.briolink.companyservice.common.jpa.read.entity.CompanyReadEntity
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
        )

fun User.Companion.fromEntity(entity: UserReadEntity) =
        User(
                id = entity.id.toString(),
                firstName = entity.data.firstName,
                lastName = entity.data.lastName,
                jobPosition = entity.data.jobPosition,
                image = entity.data.image.let { Image(url = URL(entity.data.image)) },
        )
