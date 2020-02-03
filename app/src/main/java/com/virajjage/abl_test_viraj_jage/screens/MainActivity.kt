package com.virajjage.abl_test_viraj_jage.screens

import android.graphics.Canvas
import android.os.Bundle
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.gson.Gson
import com.virajjage.abl_test_viraj_jage.R
import com.virajjage.abl_test_viraj_jage.adapters.UserListAdapter
import com.virajjage.abl_test_viraj_jage.models.User
import com.virajjage.abl_test_viraj_jage.models.UserResponseModel
import com.virajjage.abl_test_viraj_jage.utils.SwipeController
import com.virajjage.abl_test_viraj_jage.utils.SwipeControllerActions
import com.virajjage.abl_test_viraj_jage.viewmodels.MainActivityViewModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var collapsingToolbarLayout: CollapsingToolbarLayout
    private lateinit var recUserList: RecyclerView
    private lateinit var userAdapter: UserListAdapter
    private lateinit var mainActivityViewModel: MainActivityViewModel
    private lateinit var edtSearch: EditText
    private var searchedText = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //https://github.com/codepath/android_guides/wiki/Handling-Scrolls-with-CoordinatorLayout

        //https://codeburst.io/android-swipe-menu-with-recyclerview-8f28a235ff28

        initViews()
        initListener()
        initValues()
    }

    private fun initViews() {
        recUserList = findViewById(R.id.recUserList)
        edtSearch = findViewById(R.id.edtSearch)
        recUserList.layoutManager = LinearLayoutManager(this)

        collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar)
        collapsingToolbarLayout.isTitleEnabled = false
        //collapsingToolbarLayout.title = resources.getString(R.string.txt_title_friends)
        val title = resources.getString(R.string.txt_title_friends)
        val wordToSpan = SpannableString(title)
        wordToSpan.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(this, R.color.colorBlack)),
            0,
            title.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        collapsingToolbarLayout.title = wordToSpan
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.coll_toolbar_title)
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.exp_toolbar_title)
        setupRecyclerView()

    }

    private fun initListener() {
        edtSearch.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                if (s.length > 1) {
                    searchedText = s.toString()
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

    private fun initValues() {
        mainActivityViewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)


        /*val userResponseModel: UserResponseModel =
            Gson().fromJson(TestConstant.apiResponse, UserResponseModel::class.java)*/
        shimmer_view_container.startShimmer()
        shimmer_view_container.visibility = View.VISIBLE
        mainActivityViewModel.callUserListAPI().observe(this, userListObserver)

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
                    setAdapter(userResponseModel.users)
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

    private fun setAdapter(userList: List<User>) {
        userAdapter = UserListAdapter(this, userList)
        recUserList.adapter = userAdapter
    }

    private fun setupRecyclerView() {
        var swipeController = SwipeController(object : SwipeControllerActions() {
            override fun onRightAddClicked(position: Int) {

            }

            override fun onRightDeleteClicked(position: Int) {

            }
        })
        val itemTouchHelper = ItemTouchHelper(swipeController)
        itemTouchHelper.attachToRecyclerView(recUserList)

        recUserList.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
                swipeController.onDraw(c)
            }
        })
    }
}
