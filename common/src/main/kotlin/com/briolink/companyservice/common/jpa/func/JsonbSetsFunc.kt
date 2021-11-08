package com.briolink.companyservice.common.jpa.func

import org.hibernate.QueryException
import org.hibernate.dialect.function.SQLFunction
import org.hibernate.engine.spi.Mapping
import org.hibernate.engine.spi.SessionFactoryImplementor
import org.hibernate.type.BooleanType
import org.hibernate.type.Type

class JsonbSetsFunc : SQLFunction {
    @Throws(QueryException::class)
    override fun render(type: Type?, args: List<*>, sessionFactoryImplementor: SessionFactoryImplementor?): String {
        if ((args.count() % 3) == 0) throw RuntimeException("jsonb_sets has invalid arguments")

        val field = args.first()
        val arguments = args.toMutableList().apply { removeFirst() }

        return "${"jsonb_set(".repeat(arguments.size / 3)}$field," +
                arguments.chunked(3).joinToString(",") { it ->
                    if (it[1] == "null") {
                        "${it[0]}, 'null', true)"
                    } else {
                        "${it[0]}, coalesce(to_jsonb(cast(${it[1]} as ${it[2]})), 'null'), true)"
                    }
                }
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
