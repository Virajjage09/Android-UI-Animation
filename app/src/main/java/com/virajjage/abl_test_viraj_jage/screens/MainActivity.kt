package com.virajjage.abl_test_viraj_jage.screens

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.virajjage.abl_test_viraj_jage.R
import com.virajjage.abl_test_viraj_jage.adapters.UserListAdapter
import com.virajjage.abl_test_viraj_jage.helper.MyButton
import com.virajjage.abl_test_viraj_jage.helper.SwipeHelper
import com.virajjage.abl_test_viraj_jage.interfaces.SwipeButtonClickListener
import com.virajjage.abl_test_viraj_jage.models.BlackListedItem
import com.virajjage.abl_test_viraj_jage.models.User
import com.virajjage.abl_test_viraj_jage.models.UserResponseModel
import com.virajjage.abl_test_viraj_jage.roomdb.UserDatabase
import com.virajjage.abl_test_viraj_jage.viewmodels.MainActivityViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var userAdapter: UserListAdapter
    private lateinit var mainActivityViewModel: MainActivityViewModel
    private var searchedText = ""
    private lateinit var database: UserDatabase
    private lateinit var defaultUserList: ArrayList<User>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        initListener()
        initValues()
    }

    private fun initViews() {
        setSupportActionBar(toolbar)
        recUserList.layoutManager = LinearLayoutManager(this)
        this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)

        val title = resources.getString(R.string.txt_title_friends)
        val wordToSpan = SpannableString(title)
        wordToSpan.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(this, R.color.colorBlack)),
            0,
            title.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        collapsing_toolbar.title = wordToSpan
        collapsing_toolbar.setCollapsedTitleTextAppearance(R.style.coll_toolbar_title)
        collapsing_toolbar.setExpandedTitleTextAppearance(R.style.exp_toolbar_title)

    }

    private fun initValues() {
        mainActivityViewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)
        database = UserDatabase(this)
        setupRecyclerView()
        /*val userResponseModel: UserResponseModel =
            Gson().fromJson(TestConstant.apiResponse, UserResponseModel::class.java)*/
        callUserListAPI()

    }


    private fun initListener() {
        edtSearch.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                if (s.length > 1) {
                    searchedText = s.toString()
                    CoroutineScope(IO).launch {
                        getBlackListedItem()
                    }
                } else if (s.isEmpty()) {
                    setAdapter(defaultUserList)
                }
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
            }
        })

    }


    private suspend fun getBlackListedItem() {
        var list = database.userDBAccess().getBlackListedItem(searchedText)
        Log.d("BlackListedItem", "BlackListedItem Size ${list.size}")
        getUsersList(list)
    }

    private suspend fun getUsersList(list: List<BlackListedItem>) {
        withContext(Default) {
            if (list.isEmpty()) {
                callUserListAPI()
            } else {
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(
                        applicationContext,
                        resources.getString(R.string.msg_query_blacklisted),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun callUserListAPI() {
        try {
            Handler(Looper.getMainLooper()).post {
                shimmer_view_container.visibility = View.VISIBLE
                shimmer_view_container.startShimmer()

                mainActivityViewModel.callUserListAPI()
                    .observe(this, userListObserver)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private val userListObserver = Observer<UserResponseModel> { userResponseModel ->
        try {
            if (shimmer_view_container.isShimmerStarted) {
                shimmer_view_container.visibility = View.INVISIBLE
                shimmer_view_container.stopShimmer()
            }
            if (userResponseModel != null) {
                if (userResponseModel.ok) {
                    var response = Gson().toJson(userResponseModel)
                    Log.d("API Response", "API Response :$response")
                    defaultUserList = userResponseModel.users
                    if (defaultUserList.isNotEmpty()) {
                        tvNoRecords.visibility = View.GONE
                        if (searchedText.isEmpty()) {
                            setAdapter(userResponseModel.users)
                        } else {
                            filterUsers(userResponseModel.users)
                        }
                    } else {
                        tvNoRecords.visibility = View.VISIBLE
                    }
                }
            } else {
                Toast.makeText(
                    applicationContext,
                    resources.getString(R.string.msg_request_failed),
                    Toast.LENGTH_LONG
                ).show()
            }

        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    private fun filterUsers(userList: ArrayList<User>) {
        val filteredList: ArrayList<User> = ArrayList<User>()
        for (row in userList) {
            if (row.display_name.toLowerCase().contains(searchedText.toLowerCase())) {
                filteredList.add(row)
            }
        }

        if (filteredList.isNotEmpty()) {
            setAdapter(filteredList)
        } else {
            tvNoRecords.visibility = View.VISIBLE
            recUserList.visibility = View.GONE
            val blackListedItem = BlackListedItem()
            blackListedItem.blackListedItem = searchedText

            CoroutineScope(IO).launch {
                Log.d("Item BlackListed", "Item BlackListed $searchedText")
                database.userDBAccess().insertBlackListedItem(blackListedItem)
            }
        }
    }

    private fun setAdapter(userList: ArrayList<User>) {
        recUserList.visibility = View.VISIBLE
        userAdapter = UserListAdapter(this, userList)
        recUserList.recycledViewPool.clear()
        recUserList.adapter = userAdapter
    }

    private fun setupRecyclerView() {

        val swipe = object : SwipeHelper(this, recUserList, 200) {
            override fun instantiateMyButton(
                viewHolder: RecyclerView.ViewHolder,
                buffer: MutableList<MyButton>
            ) {

                buffer.add(
                    MyButton(this@MainActivity,
                        "Delete",
                        30,
                        R.drawable.ic_action_delete,
                        ContextCompat.getColor(this@MainActivity, R.color.colorBlack),
                        object : SwipeButtonClickListener {
                            override fun onClick(pos: Int) {
                                Toast.makeText(
                                    this@MainActivity,
                                    "Delete Clicked $pos",
                                    Toast.LENGTH_LONG
                                ).show()
                            }

                        }
                    ))

                buffer.add(
                    MyButton(this@MainActivity,
                        "Done",
                        30,
                        R.drawable.ic_action_done,
                        ContextCompat.getColor(this@MainActivity, R.color.colorYellow),
                        object : SwipeButtonClickListener {
                            override fun onClick(pos: Int) {
                                Toast.makeText(
                                    this@MainActivity,
                                    "Done Clicked $pos",
                                    Toast.LENGTH_LONG
                                ).show()
                            }

                        }
                    ))
            }


        }
    }
}
