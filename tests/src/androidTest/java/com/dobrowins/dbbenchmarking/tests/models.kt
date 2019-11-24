package com.dobrowins.dbbenchmarking.tests

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.gson.Gson
import io.realm.RealmList
import io.realm.RealmObject

const val KEY_CONTACTS = "contacts"
const val TESTNAME = "Aleksey"
const val exceptionMessage = "no benchmarking is possible is no data has been stored, bruh!"

interface TestPerson

// ordinary models

data class PhoneNumber(val phoneNumber: String)

data class Person(
    val name: String = TESTNAME,
    val age: Int,
    val phoneNumbers: List<PhoneNumber>,
    val bikes: List<String>
) : TestPerson

// models for realm

open class RealmPhoneNumber constructor() : RealmObject() {
    private var phoneNumber: String = ""

    constructor(phoneNumber: String) : this() {
        this.phoneNumber = phoneNumber
    }
}

open class RealmPerson constructor() : RealmObject(), TestPerson {
    var name: String = TESTNAME
    private var age: Int = 0
    private var phoneNumbers: RealmList<RealmPhoneNumber> = RealmList()
    private var bikes: RealmList<String> = RealmList()

    constructor(
        age: Int, phoneNumbers: RealmList<RealmPhoneNumber>, bikes: RealmList<String>
    ) : this() {
        this.age = age
        this.phoneNumbers = phoneNumbers
        this.bikes = bikes
    }
}

// room models and converters

@Entity
data class RoomPhoneNumber(@PrimaryKey val phoneNumber: String)

@Entity
data class RoomPerson(
    @PrimaryKey val age: Int,
    @ColumnInfo(name = "name") val name: String = TESTNAME,
    @ColumnInfo(name = "phoneNumbers") val phoneNumbers: List<RoomPhoneNumber>,
    @ColumnInfo(name = "bikes") val bikes: List<String>
) : TestPerson

class Converters {

    @TypeConverter
    fun listToJson(value: List<RoomPhoneNumber>?): String = Gson().toJson(value)

    @TypeConverter
    fun jsonToList(value: String): List<RoomPhoneNumber>? =
        (Gson().fromJson(value, Array<RoomPhoneNumber>::class.java)
            as Array<RoomPhoneNumber>)
            .toList()

    @TypeConverter
    fun stringToJson(value: List<String>?): String = Gson().toJson(value)

    @TypeConverter
    fun jsonToString(value: String): List<String>? =
        (Gson().fromJson(value, Array<String>::class.java) as Array<String>)
            .toList()

}