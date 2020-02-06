package com.virajjage.abl_test_viraj_jage.network

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.virajjage.abl_test_viraj_jage.models.UserResponseModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers


object ApiManager {
    private var apiService: ApiService = ApiClient.getRetrofit().create(ApiService::class.java)
    var data: MutableLiveData<UserResponseModel> = MutableLiveData()


    fun callGetUserAPI(compositeDisposable: CompositeDisposable): MutableLiveData<UserResponseModel> {
        compositeDisposable.add(
            apiService.getUsers()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::onResponse, this::onError)
        )
        return data

    }


    private fun onResponse(userResponseModel: UserResponseModel) {
        data.value = userResponseModel
    }

    private fun onError(error: Throwable) {
        Log.d("Error",error.message.toString())
        data.value = null
    }

}

