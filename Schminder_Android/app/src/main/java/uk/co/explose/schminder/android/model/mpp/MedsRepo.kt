
package uk.co.explose.schminder.android.model.mpp

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Transaction
import uk.co.explose.schminder.android.network.RetrofitClient
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.text.insert
import kotlin.toString


class MedsRepo(private val context: Context) {

    fun addMedsNew() {

    }

    suspend fun doMedIndivLoad(): MedIndivInfo? {
        return try {
            val response = RetrofitClient.api.getMedsIndivList()
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
    suspend fun doMedIndivActionTx(medIndivInfo: MedIndivActionTx): MedIndivActionRx? {
        return try {
            val response = RetrofitClient.api.doMedIndivActionTx(medIndivInfo)
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
            val response = RetrofitClient.api.doMedsSearch(med_search)
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


    private val daoMedsIndiv: MedsIndivDao by lazy {
        RoomInstance.getDatabase(context).medsIndivDao()
    }

    private val daoMeds: MedsDao by lazy {
        RoomInstance.getDatabase(context).medsDao()
    }

    private val daoSettings: SettingsDao by lazy {
        RoomInstance.getDatabase(context).settingsDao()
    }

    suspend fun convertIndivToMeds(medsIndiv: List<MedIndiv>): List<Med> {
        return medsIndiv.map { indiv ->
            Med(
                medName = indiv.medName,
                medInfo = "",
                medScheduled = false,
                medPid = 0,
                medId = 0,
                medDateStart = LocalDate.now()
            )
        }
    }

    suspend fun convertIndivDtoToIndiv(medsIndiv: List<MedIndivDto>): List<MedIndiv> {
        return medsIndiv.map { indiv ->
            MedIndiv(
                medName = indiv.medName,
                medDateEntered = LocalDate.now()
            )
        }
    }

    suspend fun medIndivMedListAll(): List<MedIndivMed> {
        return daoMedsIndiv.MedIndivMedListAll()
    }

    suspend fun medIndivListAll(): List<MedIndiv> {
        return daoMedsIndiv.medIndivList()
    }

    suspend fun medIndivDtoListAll(): List<MedIndivDto> {
        return daoMedsIndiv.medIndivList()
            .map { MedIndivDto(medName = it.medName) }
    }

    suspend fun medIndivDtoInsertAll(medsIndiv: List<MedIndivDto>) : List<Long> {
        val meds = convertIndivDtoToIndiv(medsIndiv)
        return daoMedsIndiv.medIndivInsertAll(meds)
    }

    suspend fun medIndivInsertAll(meds: List<MedIndiv>) : List<Long> {
        return daoMedsIndiv.medIndivInsertAll(meds)
    }

    suspend fun medIndivDeleteByName(medName: String)  {
        return daoMedsIndiv.medDeleteByName(medName)
    }

    suspend fun medListAll() : List<Med> {
        return daoMeds.medListAll()
    }

    suspend fun medInsert(med: Med) : Long {
        return daoMeds.medInsert(med)
    }

    suspend fun medReadById(id: Int): Med? {
        return daoMeds.getMedById(id)
    }


    suspend fun insertSettings(key: String,value: String) {
        daoSettings.insert(ScheduleSetting(key, value))
    }

    suspend fun loadSettings(): Map<String,String?> {
        val list = daoSettings.getAll()
        val settings = list.associate { it.setKey to it.setValue }
        return settings
    }



    object RoomInstance {
        @Volatile private var INSTANCE: AppDatabase? = null
        val MIGRATION_16_17 = object : Migration(16, 17) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
            CREATE TABLE IF NOT EXISTS settingsTbl (
        setKey TEXT NOT NULL PRIMARY KEY,
        setValue TEXT NOT NULL
    )
            """.trimIndent()
                )
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "schminder_db"
                )
                    .fallbackToDestructiveMigration(true)
                    //.addMigrations(MIGRATION_16_17)
                    .build().also { INSTANCE = it }
            }
        }
    }
}

// 1. Define the Room Entity
@Entity(tableName = "settingsTbl")
data class ScheduleSetting(
    @PrimaryKey val setKey: String,
    val setValue: String
)

@Dao
interface SettingsDao {
    @Query("SELECT * FROM settingsTbl")
    suspend fun getAll(): List<ScheduleSetting>

    @Query("SELECT setValue FROM settingsTbl WHERE setKey = :key")
    suspend fun getValue(key: String): String

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(setting: ScheduleSetting)
}


@Dao
interface MedsIndivDao {
    @Query("SELECT * FROM MedsIndivTbl")
    suspend fun medIndivList(): List<MedIndiv>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun medIndivInsertAll(meds: List<MedIndiv>): List<Long>

    @Query("DELETE FROM MedsIndivTbl WHERE medName = :medName")
    suspend fun medDeleteByName(medName: String)

    @Transaction
    @Query("SELECT * FROM MedsIndivTbl")
    suspend fun MedIndivMedListAll(): List<MedIndivMed>
}

@Dao
interface MedsDao {
    @Query("SELECT * FROM MedsTbl")
    suspend fun medListAll(): List<Med>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun medInsert(med: Med): Long

    @Query("DELETE FROM MedsTbl WHERE medId = :medId")
    suspend fun medDeleteById(medId: Int)

    @Query("SELECT * FROM MedsTbl WHERE medId = :id LIMIT 1")
    suspend fun getMedById(id: Int): Med?
}


@Database(
    entities = [
        Med::class,
        MedIndiv::class,
        ScheduleSetting::class
    ],
    version = 18
)

@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun medsIndivDao(): MedsIndivDao
    abstract fun medsDao(): MedsDao
    abstract fun settingsDao(): SettingsDao
}


class Converters {
    @TypeConverter
    fun fromLocalTime(time: LocalTime): String = time.toString()

    @TypeConverter
    fun toLocalTime(value: String): LocalTime = LocalTime.parse(value)

    @TypeConverter
    fun fromLocalDate(date: LocalDate): String = date.toString()

    @TypeConverter
    fun toLocalDate(value: String): LocalDate = LocalDate.parse(value)

    @TypeConverter
    fun fromMedRecurIntervalEnum(value: MedRepeatIntervalEnum): String = value.name

    @TypeConverter
    fun toMedRepeatIntervalEnum(value: String): MedRepeatIntervalEnum = MedRepeatIntervalEnum.valueOf(value)

    @TypeConverter
    fun fromMedRepeatTypeEnum(value: MedRepeatTypeEnum): String = value.name

    @TypeConverter
    fun toMedRecurTypeEnum(value: String): MedRepeatTypeEnum = MedRepeatTypeEnum.valueOf(value)

    @TypeConverter
    fun fromLocalDateTime(dateTime: LocalDateTime?): String? {
        return dateTime?.toString()
    }

    @TypeConverter
    fun toLocalDateTime(value: String?): LocalDateTime? {
        return value?.let { LocalDateTime.parse(it) }
    }
}

