package com.briolink.companyservice.common.dialect

import org.hibernate.dialect.MySQL57Dialect
import org.hibernate.dialect.function.SQLFunctionTemplate
import org.hibernate.type.StandardBasicTypes

class MySQLCustomDialect : MySQL57Dialect() {
    init {
        registerFunction(
                "match_bool_mode",
                SQLFunctionTemplate(StandardBasicTypes.DOUBLE, "match(?1) against (?2 in boolean mode)")
        )
    }
}
