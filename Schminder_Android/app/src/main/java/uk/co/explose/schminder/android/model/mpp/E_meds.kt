
package uk.co.explose.schminder.android.model.mpp

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import uk.co.explose.schminder.android.network.RetrofitClient
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import java.time.LocalTime


class E_meds(private val context: Context) {

    fun addMedsNew() {

    }

    suspend fun doMedIndivLoad(): m_medIndivInfo? {
        return try {
            val response = RetrofitClient.instance.getMedsIndivList()
            if (response.isSuccessful) {
                response.body()
            } else {
                null // handle error (optional: log or throw)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun doMedSearch(med_search: med_search_tx): medInfo? {
        return try {
            val response = RetrofitClient.instance.doMedsSearch(med_search)
            if (response.isSuccessful) {
                response.body()
            } else {
                null // handle error (optional: log or throw)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private val dao: MedsIndivDao by lazy {
        RoomInstance.getDatabase(context).medsIndivDao()
    }

    suspend fun convertIndivToMeds(medsIndiv: List<C_med_indiv>): List<C_med> {
        return medsIndiv.map { indiv ->
            C_med(
                medName = indiv.medName,
                medInfo = "",
                medScheduled = false,
                medPid = 0,
                medId = 0
            )
        }
    }

    suspend fun insertMedsIndiv(medsIndiv: List<C_med_indiv>): List<C_med> {
        val meds = convertIndivToMeds(medsIndiv)
        val ids = dao.insertAll(meds)

        return meds.mapIndexed { index, med ->
            med.copy(medId = ids[index])
        }
    }


    suspend fun insertMeds(meds: List<C_med>) {
        dao.insertAll(meds)
    }

    suspend fun editMedByName(medName: String, newInfo: String) {
        dao.editOne(medName, newInfo)
    }

    suspend fun deleteMedByName(medName: String) {
        dao.deleteOne(medName)
    }

    suspend fun getAllMeds(): List<C_med> {
        return dao.getAll()
    }

    suspend fun getAllMedsIndiv(): List<C_med_indiv> {
        return getAllMeds().map { cMed ->
            C_med_indiv(medName = cMed.medName)
        }
    }
    object RoomInstance {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "schminder_db"
                )
                    .fallbackToDestructiveMigration()
                    .build().also { INSTANCE = it }
            }
        }
    }
}

@Dao
interface MedsIndivDao {
    @Query("SELECT * FROM meds_tbl")
    suspend fun getAll(): List<C_med>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(meds: List<C_med>): List<Long>

    @Query("DELETE FROM meds_tbl WHERE medName = :medName")
    suspend fun deleteOne(medName: String)

    @Query("UPDATE meds_tbl SET medInfo=:newInfo WHERE medName = :medName")
    suspend fun editOne(medName: String, newInfo: String)
}

@Database(entities = [C_med::class], version = 7)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun medsIndivDao(): MedsIndivDao
}


class Converters {
    @TypeConverter
    fun fromLocalTime(time: LocalTime): String = time.toString()

    @TypeConverter
    fun toLocalTime(value: String): LocalTime = LocalTime.parse(value)

    @TypeConverter
    fun fromMedRecurIntervalEnum(value: MedRecurIntervalEnum): String = value.name

    @TypeConverter
    fun toMedRecurIntervalEnum(value: String): MedRecurIntervalEnum = MedRecurIntervalEnum.valueOf(value)

}

