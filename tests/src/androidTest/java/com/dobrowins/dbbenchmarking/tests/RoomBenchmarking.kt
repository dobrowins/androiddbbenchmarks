@file:Suppress("MoveLambdaOutsideParentheses")

package com.dobrowins.dbbenchmarking.tests

import androidx.benchmark.junit4.BenchmarkRule
import androidx.benchmark.junit4.measureRepeated
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class RoomBenchmarking {

    @get:Rule
    val benchmarkRule = BenchmarkRule()

    private val persons by lazy {
        benchmarkRule.scope.runWithTimingDisabled { TestFixture.roomPersons }
    }
    private val repository by lazy {
        benchmarkRule.scope.runWithTimingDisabled { RoomRepository() }
    }

    private val database: PersonsDatabase = Room.databaseBuilder(
        getApplicationContext(),
        PersonsDatabase::class.java,
        "persons"
    ).build()

    @Test
    fun roomInitTest() = benchmarkRule.measureRepeated {
        Room.databaseBuilder(
            getApplicationContext(),
            PersonsDatabase::class.java,
            "persons"
        ).build()
    }

    @Test
    fun roomInsertReadTest() = benchmarkRule.measureRepeated {
        benchmarkRule.scope.runWithTimingDisabled {
            database.personsDao().deleteAll()
            if (database.personsDao().getAll().isNotEmpty()) throw RuntimeException()
        }
        repository.store(persons, { list -> database.personsDao().insertAll(list) })
        val persons = repository.read { database.personsDao().getAll() }
    }

}