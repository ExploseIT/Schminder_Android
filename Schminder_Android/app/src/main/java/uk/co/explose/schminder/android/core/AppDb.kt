
package uk.co.explose.schminder.android.core

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Transaction
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import uk.co.explose.schminder.android.model.mpp.Med
import uk.co.explose.schminder.android.model.mpp.MedIndiv
import uk.co.explose.schminder.android.model.mpp.MedIndivMed
import uk.co.explose.schminder.android.model.mpp.MedRepeatIntervalEnum
import uk.co.explose.schminder.android.model.mpp.MedRepeatTypeEnum
import uk.co.explose.schminder.android.model.mpp.MedScheduled
import uk.co.explose.schminder.android.model.mpp.MedScheduledWithMed
import uk.co.explose.schminder.android.model.settings.AppSetting
import uk.co.explose.schminder.android.model.settings.SettingsObj
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime



class App_Db(private val context: Context) {


    private val daoMedsIndiv: MedsIndivDao by lazy {
        AppDb.getInstance(context).medsIndivDao()
    }

    private val daoMeds: MedsDao by lazy {
        AppDb.getInstance(context).medsDao()
    }

    private val daoMedsScheduled: MedsScheduledDao by lazy {
        AppDb.getInstance(context).medsScheduledDao()
    }

    private val daoSettings: SettingsDao by lazy {
        AppDb.getInstance(context).settingsDao()
    }

    suspend fun insertSettings(context: Context, key: String, name: String, type: String,  value: String, desc: String) {
        val setting = AppSetting(setKey = key, setName = name, setType = type, setValue = value, setDesc = desc)
        AppDb.getInstance(context).settingsDao().insert(setting)
    }


    suspend fun loadSettings(): Map<String, String?> {
        val list = AppDb.getInstance(context).settingsDao().readAll()
        val settings = list.associate { it.setKey to it.setValue }
        return settings
    }

    suspend fun getSettingsObj() : SettingsObj {
        var ret = SettingsObj()
        val dao = AppDb.getInstance(context).settingsDao()
        var lSettings = dao.readAll()
        if (lSettings.size >= 3) {
            ret.soonMinutes = lSettings.first { it.setKey == "setSoonMinutes" }.setValue.toLong()
            ret.missedMinutes = lSettings.first { it.setKey == "setMissedMinutes" }.setValue.toLong()
            ret.windowMinutes = lSettings.first { it.setKey == "setWindowMinutes" }.setValue.toLong()
        }
        return ret
    }
}

@Dao
interface SettingsDao {
    @Query("SELECT * FROM settingsTbl")
    suspend fun readAll(): List<AppSetting>

    @Query("SELECT setValue FROM settingsTbl WHERE setKey = :key")
    suspend fun readValueByKey(key: String): String

    @Query("SELECT * FROM settingsTbl WHERE setKey = :key")
    suspend fun readByKey(key: String): AppSetting

    @Query("SELECT setDesc FROM settingsTbl WHERE setKey = :key")
    suspend fun getDesc(key: String): String

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(setting: AppSetting)

    @Query("UPDATE settingsTbl SET setValue = :newValue WHERE setId = :setId")
    suspend fun updateValue(setId: Int, newValue: String)

    @Query("UPDATE settingsTbl SET setDesc = :newDesc WHERE setKey = :setKey")
    suspend fun updateDescFromKey(setKey: String, newDesc: String)

    @Query("UPDATE settingsTbl SET setName = :name, setType = :type, setDesc = :desc WHERE setKey = :key")
    suspend fun updateNameTypeDescByKey(key: String, name: String, type: String, desc: String)
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

@Dao
interface MedsScheduledDao {
    suspend fun insertAndReturn(medScheduled: MedScheduled): MedScheduled {
        val id = insert(medScheduled)
        return medScheduled.copy(medId = id.toInt())
    }

    @Query("""
    SELECT * FROM MedsScheduledTbl
    WHERE medIdSchedule = :medId AND medDTSchedule = :medDTSchedule
    LIMIT 1
""")
    suspend fun exists(medId: Int, medDTSchedule: LocalDateTime): MedScheduled?

    @Query("""
    SELECT * FROM MedsScheduledTbl
    WHERE medDTSchedule > :after
    AND medId != :excludeId
    AND (medDTTaken IS NULL OR medDTTaken > medDTSchedule)
    ORDER BY medDTSchedule ASC
    LIMIT 1
""")
    suspend fun getNextScheduledAfterExcluding(after: LocalDateTime, excludeId: Int): MedScheduled?


    @Insert
    suspend fun insert(medScheduled: MedScheduled) : Long

    @Transaction
    @Query("SELECT * FROM MedsScheduledTbl")
    suspend fun listAll(): List<MedScheduledWithMed>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertById(med: MedScheduled): Long

    @Query("DELETE FROM MedsScheduledTbl WHERE medId = :medId")
    suspend fun deleteById(medId: Int)

    @Query("SELECT * FROM MedsScheduledTbl WHERE medId = :id LIMIT 1")
    suspend fun readById(id: Int): MedScheduled?

    @Query("Update medsscheduledtbl set medDTNotifyLast = :medDTNotifyLast where medId = :medId")
    suspend fun updateNotifyLastById(medId: Int, medDTNotifyLast: LocalDateTime)
}

@Database(
    entities = [
        Med::class,
        MedIndiv::class,
        MedScheduled::class,
        AppSetting::class
    ],
    version = 23
)

@TypeConverters(Converters::class)
abstract class AppDb : RoomDatabase() {
    abstract fun medsIndivDao(): MedsIndivDao
    abstract fun medsDao(): MedsDao
    abstract fun medsScheduledDao(): MedsScheduledDao
    abstract fun settingsDao(): SettingsDao


    companion object {
        @Volatile
        private var INSTANCE: AppDb? = null
        val MIGRATION_18_19 = object : Migration(18, 19) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Step 1: Rename the old table
                database.execSQL("ALTER TABLE settingsTbl RENAME TO settingsTbl_old")

                // Step 2: Create new table with updated schema
                database.execSQL("""
            CREATE TABLE IF NOT EXISTS settingsTbl (
                setId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                setKey TEXT NOT NULL,
                setValue TEXT NOT NULL,
                setDesc TEXT NOT NULL
            )
        """.trimIndent())

                // Step 3: Copy data from old table into new table
                database.execSQL("""
            INSERT INTO settingsTbl (setKey, setValue, setDesc)
            SELECT setKey, setValue, '' FROM settingsTbl_old
        """.trimIndent())

                // Step 4: Drop old table
                database.execSQL("DROP TABLE settingsTbl_old")
            }
        }
        val MIGRATION_19_20 = object : Migration(19, 20) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Rename old table
                database.execSQL("ALTER TABLE settingsTbl RENAME TO settingsTbl_old")

                // Create new table with new columns
                database.execSQL("""
            CREATE TABLE IF NOT EXISTS settingsTbl (
                setId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                setKey TEXT NOT NULL,
                setName TEXT NOT NULL,
                setType TEXT NOT NULL,
                setValue TEXT NOT NULL,
                setDesc TEXT NOT NULL
            )
        """.trimIndent())

                // Copy old data, defaulting new columns
                database.execSQL("""
            INSERT INTO settingsTbl (setKey, setName, setType, setValue, setDesc)
            SELECT setKey, setKey, 'text', setValue, setDesc FROM settingsTbl_old
        """.trimIndent())

                // Drop old table
                database.execSQL("DROP TABLE settingsTbl_old")
            }
        }

        val MIGRATION_20_21 = object : Migration(20, 21) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE MedsIndivTbl RENAME TO MedsIndivTbl_old")

                // Step 2: Create new table with updated schema
                database.execSQL("""
            CREATE TABLE IF NOT EXISTS MedsIndivTbl (
                medName TEXT NOT NULL PRIMARY KEY,
                medDTEntered TEXT NOT NULL
            )
        """.trimIndent())

                // Step 3: Migrate data with a default timestamp conversion (or as-is if compatible)
                database.execSQL("""
            INSERT INTO MedsIndivTbl (medName, medDTEntered)
            SELECT medName, medDateEntered FROM MedsIndivTbl_old
        """.trimIndent())

                // Step 4: Drop old table
                database.execSQL("DROP TABLE MedsIndivTbl_old")

                // Create the new MedsScheduledTbl table
                database.execSQL("""
            CREATE TABLE IF NOT EXISTS MedsScheduledTbl (
                medId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                medPid INTEGER NOT NULL,
                medIdSchedule INTEGER NOT NULL,
                medName TEXT NOT NULL,
                medInfo TEXT NOT NULL,
                medDTSchedule TEXT NOT NULL,
                medDTTaken TEXT,
                medDTNotifyLast TEXT,
                FOREIGN KEY(medIdSchedule) REFERENCES MedsTbl(medId) ON DELETE CASCADE
            )
        """.trimIndent())

            }
        }

        val MIGRATION_21_22 = object : Migration(21, 22) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("""
                    UPDATE MedsIndivTbl
                        SET medDTEntered = medDTEntered || 'T00:00:00'
                        WHERE length(medDTEntered) = 10")
                      """.trimIndent())
            }
        }

        val MIGRATION_22_23 = object : Migration(22, 23) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("""
                    UPDATE MedsIndivTbl
                        SET medDTEntered = medDTEntered || 'T00:00:00'
                        WHERE length(medDTEntered) = 10
                      """.trimIndent())
            }
        }

        fun getInstance(context: Context): AppDb {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDb::class.java,
                    "schminder_db"
                )
                    .addMigrations(MIGRATION_18_19, MIGRATION_19_20, MIGRATION_20_21, MIGRATION_21_22, MIGRATION_22_23)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
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
