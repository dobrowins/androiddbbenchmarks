@file:Suppress("MoveLambdaOutsideParentheses")

package com.dobrowins.dbbenchmarking.tests

import androidx.benchmark.junit4.BenchmarkRule
import androidx.benchmark.junit4.measureRepeated
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.paperdb.Paper
import junit.framework.Assert.assertTrue
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class PaperDbBenchmarking {

    init {
        Paper.init(getApplicationContext())
    }

    @get:Rule
    val benchmarkRule = BenchmarkRule()

    private val persons by lazy {
        benchmarkRule.scope.runWithTimingDisabled { TestFixture.persons }
    }
    private val repository by lazy {
        benchmarkRule.scope.runWithTimingDisabled { PaperRepository() }
    }

    @Test
    fun paperdbInitTest() = benchmarkRule.measureRepeated {
        Paper.init(getApplicationContext())
    }

    @Test
    fun paperdbInsertReadTest() = benchmarkRule.measureRepeated {
        benchmarkRule.scope.runWithTimingDisabled {
            Paper.book().destroy()
            if (Paper.book().allKeys.isNotEmpty()) throw RuntimeException()
        }
        repository.store(persons, { list -> Paper.book().write(KEY_CONTACTS, list) })
        val persons = repository.read { Paper.book().read<List<Person>>(KEY_CONTACTS, emptyList()) }
    }


}