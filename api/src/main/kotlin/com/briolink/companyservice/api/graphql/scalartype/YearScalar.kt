package com.briolink.companyservice.api.graphql.scalartype

import com.netflix.graphql.dgs.DgsScalar
import graphql.language.StringValue
import graphql.schema.Coercing
import graphql.schema.CoercingParseLiteralException
import graphql.schema.CoercingParseValueException
import graphql.schema.CoercingSerializeException
import java.time.Year

@DgsScalar(name = "Year")
class YearScalar : Coercing<Year, String> {
    @Throws(CoercingSerializeException::class)
    override fun serialize(dataFetcherResult: Any): String =
        if (dataFetcherResult is Year) dataFetcherResult.toString() else throw CoercingSerializeException("Not a valid Year")

    @Throws(CoercingParseValueException::class)
    override fun parseValue(input: Any): Year = Year.parse(input.toString())

    @Throws(CoercingParseLiteralException::class)
    override fun parseLiteral(input: Any): Year =
        if (input is StringValue) Year.parse(input.value) else throw CoercingParseLiteralException("Value is not a valid ISO year")
}
