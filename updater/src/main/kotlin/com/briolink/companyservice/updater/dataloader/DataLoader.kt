package com.briolink.companyservice.updater.dataloader

import org.springframework.boot.CommandLineRunner
import java.time.LocalDate
import java.util.*
import kotlin.random.Random


abstract class DataLoader : CommandLineRunner {

    @Throws(Exception::class)
    override fun run(vararg args: String?) {
        loadData()
    }

    abstract fun loadData()

    fun randomDate(startYear: Int, endYear: Int): LocalDate {
        val day: Int = Random.nextInt(1, 28)
        val month: Int = Random.nextInt(1, 12)
        val year: Int = Random.nextInt(startYear, endYear)
        return LocalDate.of(year, month, day)
    }
}
