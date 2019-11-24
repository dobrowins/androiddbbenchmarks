@file:Suppress("MoveLambdaOutsideParentheses")

package com.dobrowins.dbbenchmarking.tests

import androidx.benchmark.junit4.BenchmarkRule
import androidx.benchmark.junit4.measureRepeated
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.realm.Realm
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RealmBenchmarking {

    init {
        Realm.init(getApplicationContext())
    }

    @get:Rule
    val benchmarkRule = BenchmarkRule()

    private val persons by lazy { TestFixture.realmPersons }

    @Before
    fun purge() {
        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()
        realm.deleteAll()
        realm.commitTransaction()
    }

    @Test
    fun realmInitTest() = benchmarkRule.measureRepeated {
        Realm.init(getApplicationContext())
    }

    @Test
    fun realmInsertReadTest() = benchmarkRule.measureRepeated {
        val repository = RealmRepository()
        repository.store(
            persons,
            { list ->
                val realm = Realm.getDefaultInstance()
                realm.beginTransaction()
                realm.insert(list)
                realm.commitTransaction()
            }
        )
        val persons = repository.read {
            Realm.getDefaultInstance().where(RealmPerson::class.java).findAll()
        }
        assert(persons.all { it.name == TESTNAME })
    }

}