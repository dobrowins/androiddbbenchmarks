@file:Suppress("MoveLambdaOutsideParentheses")

package com.dobrowins.dbbenchmarking.tests

import androidx.benchmark.junit4.BenchmarkRule
import androidx.benchmark.junit4.measureRepeated
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RoomBenchmarking {

    @get:Rule
    val benchmarkRule = BenchmarkRule()

    private val database: PersonsDatabase = Room.databaseBuilder(
        getApplicationContext(),
        PersonsDatabase::class.java,
        "persons"
    ).build()

    // fixme: age is not unique somehow
    private val persons by lazy { TestFixture.roomPersons }

    @Before
    fun purge() {
        database.personsDao().deleteAll()
        if (database.personsDao().getAll().isNotEmpty()) throw RuntimeException("fuck me")
    }

    @Test
    fun roomInitTest() = benchmarkRule.measureRepeated {
        Room.databaseBuilder(
            getApplicationContext(),
            PersonsDatabase::class.java,
            "persons"
        )
            .build()
    }

    @Test
    fun roomInsertReadTest() = benchmarkRule.measureRepeated {
        val repository = RoomRepository()
        repository.store(persons, { list -> database.personsDao().insertAll(list) })
        val persons = repository.read { database.personsDao().getAll() }
        assert(persons.all { it.name == TESTNAME })
    }

}