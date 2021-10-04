package com.briolink.companyservice.common.jpa.inspector

import org.hibernate.resource.jdbc.spi.StatementInspector
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class HibernateStatementInspector : StatementInspector {
    @Value("\${app.db.schema-prefix}")
    lateinit var writeSchema: String

    /**
     * Inspect the given SQL
     *
     * @param sql
     * @return sql or modified sql
     */
    override fun inspect(sql: String): String = if (sql.contains("schema_")) sql.replace("schema_", "${writeSchema}_") else sql
}
