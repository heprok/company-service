package com.briolink.companyservice.api.exception

import com.briolink.companyservice.api.util.LocaleMessage
import com.netflix.graphql.dgs.exceptions.DefaultDataFetcherExceptionHandler
import com.netflix.graphql.types.errors.ErrorType
import com.netflix.graphql.types.errors.TypedGraphQLError
import graphql.execution.DataFetcherExceptionHandler
import graphql.execution.DataFetcherExceptionHandlerParameters
import graphql.execution.DataFetcherExceptionHandlerResult
import org.springframework.stereotype.Component
import javax.validation.ConstraintViolationException

@Component
class CustomDataFetcherExceptionHandler(localeMessage: LocaleMessage) : DataFetcherExceptionHandler {
    init {
        lm = localeMessage
    }

    private val defaultHandler = DefaultDataFetcherExceptionHandler()

    override fun onException(handlerParameters: DataFetcherExceptionHandlerParameters): DataFetcherExceptionHandlerResult {
        val exception = handlerParameters.exception
        val path = handlerParameters.path

        val error: TypedGraphQLError = when (exception) {
            is UnavailableException ->
                TypedGraphQLError.newBuilder().errorType(ErrorType.UNAVAILABLE).message(exception.message).path(path).build()
            is BadRequestException ->
                TypedGraphQLError.newBuilder().errorType(ErrorType.UNAVAILABLE).message(exception.message).path(path).build()
            else ->
                return defaultHandler.handleException(handlerParameters).get()
        }

        return DataFetcherExceptionHandlerResult.newResult().error(error).build()
    }

    companion object {
        lateinit var lm: LocaleMessage

        fun mapUserErrors(cve: ConstraintViolationException): List<Error> {
            val errors: MutableList<Error> = mutableListOf()

            for (violation in cve.constraintViolations) {
                errors.add(Error(lm.getMessage(violation.message)))
            }

            return errors
        }

        private fun getParam(s: String): String {
            val splits = s.split("\\.".toRegex()).toTypedArray()
            return if (splits.size == 1) s else java.lang.String.join(".", splits.copyOfRange(2, splits.size).toList())
        }
    }
}
