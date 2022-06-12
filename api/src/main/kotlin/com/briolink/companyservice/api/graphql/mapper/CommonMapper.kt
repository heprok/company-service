package com.briolink.companyservice.api.graphql.mapper

import com.briolink.companyservice.api.types.IdOrNameWithCountInput
import com.briolink.companyservice.api.types.MoneyInput
import com.briolink.companyservice.api.types.MoneyRangeInput
import com.briolink.lib.common.type.basic.Money
import com.briolink.lib.common.type.basic.MoneyRange
import com.briolink.lib.common.type.basic.ValueWithCount
import com.briolink.lib.common.validation.ValidUUID
import java.util.Currency
import java.util.UUID

fun MoneyInput.toMoney() = Money(value, Currency.getInstance(currencyIsoCode.name))
fun MoneyRangeInput.toRangeMoney() = MoneyRange(start.toMoney(), end.toMoney())

fun IdOrNameWithCountInput.idIsUUID() = id.matches(ValidUUID.pattern.toRegex())

fun List<IdOrNameWithCountInput>.toFilterMutableSetStr(): MutableSet<ValueWithCount<String, Double>> =
    this.filter { !it.idIsUUID() }
        .map { ValueWithCount(it.id, it.count) }
        .toMutableSet()

fun List<IdOrNameWithCountInput>.toFilterMutableSetWithUUID(): MutableSet<ValueWithCount<UUID, Double>> =
    this.filter { it.idIsUUID() }
        .map { ValueWithCount(UUID.fromString(it.id), it.count) }
        .toMutableSet()

@JvmName("toSetWithUUIDString")
fun List<String>.toFilterMutableSetWithUUID(): MutableSet<UUID> =
    this.filter { it.matches(ValidUUID.pattern.toRegex()) }
        .map { UUID.fromString(it) }
        .toMutableSet()

@JvmName("toSetWithStrString")
fun List<String>.toFilterMutableSetStr(): MutableSet<String> =
    this.filter { it.matches(ValidUUID.pattern.toRegex()) }
        .map { it }
        .toMutableSet()
