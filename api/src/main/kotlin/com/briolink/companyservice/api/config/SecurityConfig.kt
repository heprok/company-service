package com.briolink.companyservice.api.config

import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
@Configuration
class SecurityConfig : WebSecurityConfigurerAdapter() {
    override fun configure(http: HttpSecurity) {
        http
                .csrf {
                    it.disable()
                }
                .authorizeRequests { reg ->
                    reg
                            .antMatchers("/", "/actuator/**", "/graphiql", "/mock-upload", "/api/v1/**/*").permitAll()
                            .anyRequest().authenticated()
                }
                .oauth2ResourceServer { configurer ->
                    configurer
                            .jwt()
                }
    }
}
