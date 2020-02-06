package com.virajjage.abl_test_viraj_jage.roomdb

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.virajjage.abl_test_viraj_jage.models.BlackListedItem

@Dao
interface UserDBAccess {

    @Insert
    fun insertBlackListedItem(blackListedItem: BlackListedItem)

    @Query("SELECT * FROM BlackListedItem WHERE blacklisted_item =:searchedQuery")
    fun getBlackListedItem(searchedQuery: String): List<BlackListedItem>
}