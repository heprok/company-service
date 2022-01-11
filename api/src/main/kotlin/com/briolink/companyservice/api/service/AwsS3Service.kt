package com.briolink.companyservice.api.service

import com.amazonaws.AmazonClientException
import com.amazonaws.AmazonServiceException
import com.amazonaws.SdkClientException
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.AccessControlList
import com.amazonaws.services.s3.model.CopyObjectRequest
import com.amazonaws.services.s3.model.DeleteObjectRequest
import com.amazonaws.services.s3.model.GroupGrantee
import com.amazonaws.services.s3.model.ObjectMetadata
import com.amazonaws.services.s3.model.Permission
import com.briolink.companyservice.api.exception.FileTypeException
import mu.KLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.io.InputStream
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
    val bucketName: String? = null
    val tempDirPath: String = "uploads/temp"

    fun uploadFile(key: String, contentType: String, file: MultipartFile, acl: String? = null): URL =
        try {
            val metadata = ObjectMetadata()
            metadata.contentType = contentType
            metadata.contentLength = file.size
            metadata.setHeader(DEFAULT_HEADER_ACL_KEY, acl ?: DEFAULT_HEADER_ACL_VALUE)

            s3Client.putObject(bucketName, key, file.inputStream, metadata)
            s3Client.getUrl(bucketName, key)
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

    fun uploadFile(key: String, inputStream: InputStream, metadata: ObjectMetadata, acl: String? = null): URL =
        try {
            metadata.setHeader(DEFAULT_HEADER_ACL_KEY, acl ?: DEFAULT_HEADER_ACL_VALUE)
            s3Client.putObject(bucketName, key, inputStream, metadata)
            s3Client.getUrl(bucketName, key)
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

    fun uploadTempImage(file: MultipartFile): String =
        if (isImageFile(file.contentType)) {
            val objectName = generateObjectName()
            uploadFile("$tempDirPath/$objectName", file.contentType!!, file, "private")
            objectName
        } else {
            throw FileTypeException()
        }

    fun uploadImage(path: String, file: MultipartFile): URL =
        if (isImageFile(file.contentType)) {
            uploadFile("$path/${generateObjectName()}.${IMAGE_FILE_TYPE[file.contentType]}", file.contentType!!, file)
        } else {
            throw FileTypeException()
        }

    fun uploadImage(path: String, imageUrl: URL): URL? {
        val urlConnection = imageUrl.openConnection()

        if (isImageFile(urlConnection.contentType)) {
            val inputStream = imageUrl.openStream()
            val metadata = ObjectMetadata()
            metadata.contentType = urlConnection.contentType
//            metadata.contentLength = urlConnection.contentLengthLong

            val url = uploadFile(
                key = "$path/${generateObjectName()}.${IMAGE_FILE_TYPE[urlConnection.contentType]}",
                inputStream = inputStream,
                metadata = metadata
            )
            inputStream.close()
            return url
        } else {
            return null
//            throw FileTypeException()
        }
    }

    fun deleteFile(key: String) =
        try {
            s3Client.deleteObject(DeleteObjectRequest(bucketName, key))
        } catch (clientException: AmazonClientException) {
            logger.error { "AmazonClientException: ${clientException.message}" }
        }

    fun moveFromTemp(key: String, path: String): URL {
        try {
            val sourceKey = "$tempDirPath/$key"
            val objectInfo = s3Client.getObjectMetadata(bucketName, sourceKey)
            val destinationKey = "$path/$key.${IMAGE_FILE_TYPE[objectInfo.contentType]}"
            s3Client.copyObject(
                CopyObjectRequest(bucketName, sourceKey, bucketName, destinationKey).apply {
                    accessControlList = AccessControlList().apply {
                        grantPermission(GroupGrantee.AllUsers, Permission.Read)
                    }
                },
            )
            return s3Client.getUrl(bucketName, destinationKey)
        } catch (serviceException: AmazonServiceException) {
            logger.error { "AmazonServiceException: ${serviceException.message}" }
            throw serviceException
        } catch (clientException: SdkClientException) {
            logger.error { "AmazonClientException: ${clientException.message}" }
            throw clientException
        }
    }

    private fun generateObjectName(): String = UUID.randomUUID().toString().replace("-", "")
    private fun isImageFile(contentType: String?): Boolean =
        contentType != null && IMAGE_FILE_TYPE.containsKey(contentType)
}
