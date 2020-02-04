package com.virajjage.abl_test_viraj_jage.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class BlackListedItem(
    @PrimaryKey(autoGenerate = true)
    var id : Int? = null,
    @ColumnInfo(name = "blacklisted_item")
    var blackListedItem : String? = null

)