package com.briolink.companyservice.api.config

import com.blazebit.persistence.Criteria
import com.blazebit.persistence.CriteriaBuilderFactory
import com.blazebit.persistence.spi.CriteriaBuilderConfiguration
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import org.springframework.context.annotation.Scope
import javax.persistence.EntityManagerFactory
import javax.persistence.PersistenceUnit

@Configuration
class BlazePersistenceConfig(@PersistenceUnit private val entityManagerFactory: EntityManagerFactory) {
    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    @Lazy(false)
    fun createCriteriaBuilderFactory(): CriteriaBuilderFactory? {
        val config: CriteriaBuilderConfiguration = Criteria.getDefault()
        return config.createCriteriaBuilderFactory(entityManagerFactory)
    }
}
