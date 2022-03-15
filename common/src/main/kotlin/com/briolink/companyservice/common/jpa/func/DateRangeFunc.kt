package com.briolink.companyservice.common.jpa.func

import org.hibernate.QueryException
import org.hibernate.dialect.function.SQLFunction
import org.hibernate.engine.spi.Mapping
import org.hibernate.engine.spi.SessionFactoryImplementor
import org.hibernate.type.BooleanType
import org.hibernate.type.Type

class DateRangeFunc : SQLFunction {
    @Throws(QueryException::class)
    override fun render(type: Type?, args: List<*>, sessionFactoryImplementor: SessionFactoryImplementor?): String {
        if (args.count() != 3) throw RuntimeException("daterange_cross has invalid arguments")

        val field = args.first()
        val arguments = args.toMutableList().apply { removeFirst() }
        return if (arguments.last() == "null")
            """$field && daterange(${arguments.first()}::date, null)"""
        else if (arguments.first() == "null")
            """$field && daterange(null, ${arguments.last()}::date)"""
        else
            """$field && daterange(${arguments.first()}::date, ${arguments.last()}::date)"""
    }

    @Throws(QueryException::class)
    override fun getReturnType(columnType: Type?, mapping: Mapping?): Type {
        return BooleanType()
    }

    override fun hasArguments(): Boolean {
        return true
    }

    override fun hasParenthesesIfNoArguments(): Boolean {
        return false
    }
}
