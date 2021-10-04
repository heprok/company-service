package com.briolink.companyservice.api.config

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AwsS3Config {
    @Bean
    fun s3Client(): AmazonS3 = AmazonS3ClientBuilder.defaultClient()
}