@file:Suppress("MoveLambdaOutsideParentheses")

package com.dobrowins.dbbenchmarking.tests

import androidx.benchmark.junit4.BenchmarkRule
import androidx.benchmark.junit4.measureRepeated
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.realm.Realm
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class RealmBenchmarking {

    init {
        Realm.init(getApplicationContext())
    }

    @get:Rule
    val benchmarkRule = BenchmarkRule()

    private val persons by lazy {
        benchmarkRule.scope.runWithTimingDisabled { TestFixture.realmPersons }
    }
    private val repository by lazy {
        benchmarkRule.scope.runWithTimingDisabled { RealmRepository() }
    }

    @Test
    fun realmInitTest() = benchmarkRule.measureRepeated {
        Realm.init(getApplicationContext())
    }

    @Test
    fun realmInsertReadTest() = benchmarkRule.measureRepeated {
        val realm = benchmarkRule.scope.runWithTimingDisabled { Realm.getDefaultInstance() }
        // fixme: clear realm right here
        repository.store(
            persons,
            { list ->
                realm.beginTransaction()
                realm.insertOrUpdate(list)
                realm.commitTransaction()
            }
        )
        val persons = repository.read { realm.where(RealmPerson::class.java).findAll() }
    }

}