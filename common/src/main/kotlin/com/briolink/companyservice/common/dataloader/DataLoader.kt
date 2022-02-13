package com.briolink.companyservice.common.dataloader

import org.joda.time.DateTime
import org.springframework.boot.CommandLineRunner
import java.time.Instant
import java.time.LocalDate
import kotlin.random.Random

abstract class DataLoader : CommandLineRunner {

    @Throws(Exception::class)
    override fun run(vararg args: String?) {
        if (System.getenv("load_data") == "true") loadData()
    }

    abstract fun loadData()

    fun randomDate(startYear: Int, endYear: Int, minDate: LocalDate? = null): LocalDate {
        val day: Int = Random.nextInt(minDate?.dayOfMonth ?: 1, 28)
        val month: Int = Random.nextInt(minDate?.month?.value ?: 1, 12)
        val year: Int = Random.nextInt(minDate?.year ?: startYear, endYear)
        return LocalDate.of(year, month, day)
    }

    fun randomInstant(startYear: Int, endYear: Int): Instant {
        val date = randomDate(startYear, endYear)
        val datetime = DateTime(
            date.year,
            date.month.value,
            date.dayOfMonth,
            Random.nextInt(0, 23),
            Random.nextInt(0, 59),
        )

        return Instant.ofEpochMilli(datetime.millis)
    }
}
