
package uk.co.explose.schminder.android.model.mpp

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import uk.co.explose.schminder.android.network.ApiService
import uk.co.explose.schminder.android.network.RetrofitClient

class e_meds(private val context: Context) {

    fun addMedsNew() {

    }

    suspend fun doMedIndivLoad(): m_med_indiv_info? {
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

    suspend fun doMedSearch(med_search: med_search_tx): med_info? {
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

    suspend fun convertIndivToMeds(medsIndiv: List<c_med_indiv>): List<c_med> {
        return medsIndiv.map { indiv ->
            c_med(
                med_name = indiv.med_name,
                med_info = "",
                med_scheduled = false,
                med_pid = 0,
                med_id = 0
            )
        }
    }

    suspend fun insertMedsIndiv(medsIndiv: List<c_med_indiv>): List<c_med> {
        val meds = convertIndivToMeds(medsIndiv)
        val ids = dao.insertAll(meds)

        return meds.mapIndexed { index, med ->
            med.copy(med_id = ids[index])
        }
    }


    suspend fun insertMeds(meds: List<c_med>) {
        dao.insertAll(meds)
    }

    suspend fun editMedByName(med_name: String, newInfo: String) {
        dao.editOne(med_name, newInfo)
    }

    suspend fun deleteMedByName(med_name: String) {
        dao.deleteOne(med_name)
    }

    suspend fun getAllMeds(): List<c_med> {
        return dao.getAll()
    }

    suspend fun getAllMedsIndiv(): List<c_med_indiv> {
        return getAllMeds().map { cMed ->
            c_med_indiv(med_name = cMed.med_name)
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
    suspend fun getAll(): List<c_med>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(meds: List<c_med>): List<Long>

    @Query("DELETE FROM meds_tbl WHERE med_name = :med_name")
    suspend fun deleteOne(med_name: String)

    @Query("UPDATE meds_tbl SET med_info=:newInfo WHERE med_name = :med_name")
    suspend fun editOne(med_name: String, newInfo: String)
}

@Database(entities = [c_med::class], version = 6)
abstract class AppDatabase : RoomDatabase() {
    abstract fun medsIndivDao(): MedsIndivDao
}

