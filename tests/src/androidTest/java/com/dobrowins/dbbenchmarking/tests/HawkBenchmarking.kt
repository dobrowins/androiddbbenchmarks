@file:Suppress("MoveLambdaOutsideParentheses")

package com.dobrowins.dbbenchmarking.tests

import androidx.benchmark.junit4.BenchmarkRule
import androidx.benchmark.junit4.measureRepeated
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.orhanobut.hawk.Hawk
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HawkBenchmarking {

    init {
        Hawk.init(getApplicationContext()).build()
    }

    @get:Rule
    val benchmarkRule = BenchmarkRule()

    private val persons by lazy { TestFixture.persons }

    @Before
    fun purge() {
        Hawk.deleteAll()
    }

    @Test
    fun hawkInitTest() = benchmarkRule.measureRepeated {
        Hawk.init(getApplicationContext()).build()
    }

    @Test
    fun hawkInsertReadTest() = benchmarkRule.measureRepeated {
        val repository = HawkRepository()
        repository.store(persons, { list -> Hawk.put(KEY_CONTACTS, list) })
        val persons = repository.read { Hawk.get<List<Person>>(KEY_CONTACTS) }
        assert(persons.all { it.name == TESTNAME })
    }

}