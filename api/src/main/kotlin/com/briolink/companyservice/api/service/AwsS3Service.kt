package com.briolink.companyservice.api.service

import com.amazonaws.AmazonClientException
import com.amazonaws.AmazonServiceException
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.DeleteObjectRequest
import com.amazonaws.services.s3.model.ObjectMetadata
import com.briolink.companyservice.api.exception.FileTypeException
import mu.KLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.net.URL
import java.util.UUID

@Service
class AwsS3Service(private val s3Client: AmazonS3) {
    companion object : KLogging()

    val DEFAULT_HEADER_ACL_KEY: String = "x-amz-acl"
    val DEFAULT_HEADER_ACL_VALUE: String = "public-read"
    val IMAGE_FILE_TYPE: Map<String, String> = mapOf(
        MediaType.IMAGE_JPEG_VALUE to "jpg",
        MediaType.IMAGE_PNG_VALUE to "png",
    )

    @Value("\${app.aws.s3.name}")
    private val bucketName: String? = null

    fun uploadFile(key: String, file: MultipartFile): URL {
        try {
            val metadata = ObjectMetadata()
            metadata.contentLength = file.size
            metadata.setHeader(DEFAULT_HEADER_ACL_KEY, DEFAULT_HEADER_ACL_VALUE)

            s3Client.putObject(bucketName, key, file.inputStream, metadata)

            return s3Client.getUrl(bucketName, key)
        } catch (ioe: IOException) {
            logger.error { "IOException: ${ioe.message}" }
            throw ioe
        } catch (serviceException: AmazonServiceException) {
            logger.error { "AmazonServiceException: ${serviceException.message}" }
            throw serviceException
        } catch (clientException: AmazonClientException) {
            logger.error { "AmazonClientException: ${clientException.message}" }
            throw clientException
        }
    }

    fun uploadImage(path: String, file: MultipartFile): URL {
        return if (IMAGE_FILE_TYPE.containsKey(file.contentType)) {
            uploadFile("$path/${generateFileName()}.${IMAGE_FILE_TYPE[file.contentType]}", file)
        } else {
            throw FileTypeException()
        }
    }

    fun deleteFile(key: String) {
        return try {
            s3Client.deleteObject(DeleteObjectRequest(bucketName, key))
        } catch (clientException: AmazonClientException) {
            logger.error { "AmazonClientException: ${clientException.message}" }
        }
    }

    private fun generateFileName(): String = UUID.randomUUID().toString().replace("-", "")
}
