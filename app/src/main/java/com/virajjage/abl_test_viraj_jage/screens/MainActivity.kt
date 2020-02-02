package com.virajjage.abl_test_viraj_jage.screens

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.gson.Gson
import com.virajjage.abl_test_viraj_jage.R
import com.virajjage.abl_test_viraj_jage.TestConstant
import com.virajjage.abl_test_viraj_jage.adapters.UserListAdapter
import com.virajjage.abl_test_viraj_jage.models.UserResponseModel

class MainActivity : AppCompatActivity() {

    private lateinit var collapsingToolbarLayout : CollapsingToolbarLayout
    private lateinit var recUserList : RecyclerView
    private lateinit var userAdapter : UserListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //https://github.com/codepath/android_guides/wiki/Handling-Scrolls-with-CoordinatorLayout

        //https://codeburst.io/android-swipe-menu-with-recyclerview-8f28a235ff28

        initViews()
        initListner()
        initValues()
    }

    private fun initViews(){
        recUserList = findViewById(R.id.recUserList)
        recUserList.layoutManager = LinearLayoutManager(this)

        collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar)
        collapsingToolbarLayout.title = resources.getString(R.string.txt_title_friends)

    }

    private fun initListner(){


    }

    private fun initValues(){
        var userResponseModel : UserResponseModel = Gson().fromJson(TestConstant.apiResponse,UserResponseModel::class.java)
        userAdapter = UserListAdapter(userResponseModel.users)
        recUserList.adapter = userAdapter
    }
}
