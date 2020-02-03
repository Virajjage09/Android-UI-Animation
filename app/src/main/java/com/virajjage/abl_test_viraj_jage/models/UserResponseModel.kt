package com.virajjage.abl_test_viraj_jage.models

data class UserResponseModel(
    val ok:Boolean,
    val users : List<User> = ArrayList<User>()
)