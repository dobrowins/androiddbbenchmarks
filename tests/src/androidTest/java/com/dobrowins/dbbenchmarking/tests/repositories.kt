package com.dobrowins.dbbenchmarking.tests

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

class PaperRepository : BaseRepository()
class HawkRepository : BaseRepository()
class RealmRepository : BaseRepository()
class RoomRepository : BaseRepository()

@Dao
interface PersonsDao {
    @Query("SELECT * FROM roomperson")
    fun getAll(): List<RoomPerson>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(list: List<RoomPerson>)

    @Query("DELETE FROM roomperson")
    fun deleteAll()
}

@Database(entities = [RoomPerson::class], version = 1)
@TypeConverters(Converters::class)
abstract class PersonsDatabase : RoomDatabase() {
    abstract fun personsDao(): PersonsDao
}

open class BaseRepository {

    fun <T> store(list: List<T>, writeFunc: (List<T>) -> Unit) {
        writeFunc(list)
    }

    @Throws(RuntimeException::class)
    fun <T> read(getFunc: () -> List<T>): List<T> {
        val contacts = getFunc()
        if (contacts.isEmpty()) throw RuntimeException(exceptionMessage)
        else return contacts
    }

}