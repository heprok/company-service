package com.briolink.companyservice.api.exception

class FileTypeException(message: String = "Invalid file type: allowed JPEG or PNG images.") : Exception(message)
