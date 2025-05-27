package uk.co.explose.schminder.android.model.settings

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "settingsTbl")
data class AppSetting(
    @PrimaryKey(autoGenerate = true)
    val setId: Int = 0,
    val setKey: String,
    val setName: String,
    val setType: String,
    val setValue: String,
    val setDesc: String
)

