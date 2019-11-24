@file:Suppress("SameParameterValue", "MoveLambdaOutsideParentheses")

package com.dobrowins.dbbenchmarking.tests

import io.realm.RealmList

object TestFixture {

    val persons = generatePersonList<Person>(1000)

    val realmPersons = generatePersonList<RealmPerson>(1000)

    val roomPersons = generatePersonList<RoomPerson>(1000)

    private inline fun <reified T : TestPerson> generatePersonList(size: Int): List<T> =
        List(size, { index -> generatePerson<T>(index + 1) })

    private inline fun <reified T : TestPerson> generatePerson(i: Int): T =
        when (T::class.java) {
            Person::class.java -> {
                Person(
                    age = i,
                    bikes = listOf(
                        "Kellys gen#$i",
                        "Trek gen#$i"
                    ),
                    phoneNumbers = listOf(
                        PhoneNumber("0-KEEP-CALM$i"),
                        PhoneNumber("0-USE-PAPER$i")
                    )
                ) as T
            }
            RealmPerson::class.java -> {
                RealmPerson(
                    age = i,
                    bikes = RealmList(
                        "Kellys gen#$i",
                        "Trek gen#$i"
                    ),
                    phoneNumbers = RealmList(
                        RealmPhoneNumber("0-KEEP-CALM$i"),
                        RealmPhoneNumber("0-USE-PAPER$i")
                    )
                ) as T
            }
            RoomPerson::class.java -> {
                RoomPerson(
                    age = i,
                    bikes = listOf(
                        "Kellys gen#$i",
                        "Trek gen#$i"
                    ),
                    phoneNumbers = listOf(
                        RoomPhoneNumber("0-KEEP-CALM$i"),
                        RoomPhoneNumber("0-USE-PAPER$i")
                    )
                ) as T
            }
            else -> throw RuntimeException()
        }
}