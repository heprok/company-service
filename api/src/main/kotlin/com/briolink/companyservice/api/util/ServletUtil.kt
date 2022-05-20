package com.briolink.companyservice.api.util

import com.briolink.lib.common.util.BlServletUtil
import org.springframework.stereotype.Component
import javax.servlet.http.HttpServletRequest

@Component
class ServletUtil(private val request: HttpServletRequest) : BlServletUtil(request)
