package uk.co.explose.schminder.android.model.mpp

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query

@Entity(tableName = "meds_indiv_tbl")
data class MedsIndivEntity(
    @PrimaryKey val med_id: Long,
    val med_pid: Long,
    val med_name: String
)

@Dao
interface MedsDao {
    @Query("SELECT * FROM meds_indiv_tbl")
    suspend fun getAll(): List<MedsIndivEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(meds: List<MedsIndivEntity>)
}
