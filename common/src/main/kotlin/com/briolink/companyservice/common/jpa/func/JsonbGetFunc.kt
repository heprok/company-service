package com.briolink.companyservice.common.jpa.func

import org.hibernate.QueryException
import org.hibernate.dialect.function.SQLFunction
import org.hibernate.engine.spi.Mapping
import org.hibernate.engine.spi.SessionFactoryImplementor
import org.hibernate.type.StringType
import org.hibernate.type.Type

class JsonbGetFunc : SQLFunction {
    @Throws(QueryException::class)
    override fun render(type: Type?, args: List<*>, sessionFactoryImplementor: SessionFactoryImplementor?): String {
        val field = args.first()
        val arguments = args.toMutableList().apply { removeFirst() }

        return "($field -> ${arguments.joinToString(" -> ")})"
    }

    @Throws(QueryException::class)
    override fun getReturnType(columnType: Type?, mapping: Mapping?): Type {
        return StringType()
    }

    override fun hasArguments(): Boolean {
        return true
    }

    override fun hasParenthesesIfNoArguments(): Boolean {
        return false
    }
}
