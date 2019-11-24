@file:Suppress("MoveLambdaOutsideParentheses")

package com.dobrowins.dbbenchmarking.tests

import androidx.benchmark.junit4.BenchmarkRule
import androidx.benchmark.junit4.measureRepeated
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.orhanobut.hawk.Hawk
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class HawkBenchmarking {

    init {
        Hawk.init(getApplicationContext()).build()
    }

    @get:Rule
    val benchmarkRule = BenchmarkRule()

    private val persons by lazy {
        benchmarkRule.scope.runWithTimingDisabled { TestFixture.persons }
    }
    private val repository by lazy {
        benchmarkRule.scope.runWithTimingDisabled { HawkRepository() }
    }

    @Test
    fun hawkInitTest() = benchmarkRule.measureRepeated {
        Hawk.init(getApplicationContext()).build()
    }

    @Test
    fun hawkInsertReadTest() = benchmarkRule.measureRepeated {
        benchmarkRule.scope.runWithTimingDisabled {
            Hawk.deleteAll()
            if (Hawk.get<List<Person>>(KEY_CONTACTS) != null) throw RuntimeException()
        }
        repository.store(persons, { list -> Hawk.put(KEY_CONTACTS, list) })
        val persons = repository.read { Hawk.get<List<Person>>(KEY_CONTACTS) }
    }

}