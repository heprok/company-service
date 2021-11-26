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

    fun randomDate(startYear: Int, endYear: Int): LocalDate {
        val day: Int = Random.nextInt(1, 28)
        val month: Int = Random.nextInt(1, 12)
        val year: Int = Random.nextInt(startYear, endYear)
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
