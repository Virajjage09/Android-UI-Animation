package com.virajjage.abl_test_viraj_jage.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.virajjage.abl_test_viraj_jage.models.UserResponseModel
import com.virajjage.abl_test_viraj_jage.network.ApiManager
import io.reactivex.disposables.CompositeDisposable

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {

    private var mCompositeDisposable: CompositeDisposable = CompositeDisposable()

    private lateinit var userListObservable: LiveData<UserResponseModel>

    fun callUserListAPI(): LiveData<UserResponseModel> {
        userListObservable = ApiManager.callGetUserAPI(mCompositeDisposable)
        return userListObservable
    }

    override fun onCleared() {
        super.onCleared()
        mCompositeDisposable.clear()
    }

}