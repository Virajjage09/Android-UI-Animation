package com.virajjage.abl_test_viraj_jage.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.virajjage.abl_test_viraj_jage.R
import com.virajjage.abl_test_viraj_jage.models.User

class UserListAdapter(private val userList: List<User>) :
    RecyclerView.Adapter<UserListAdapter.UserListHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserListHolder {
        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_layout_user_list, parent, false)
        return UserListHolder(itemView)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: UserListHolder, position: Int) {
        val user : User = userList[position]
        holder.bindUserData(user)
    }


    class UserListHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var imgProfileImage : ImageView
        private var tvDisplayName : TextView
        private var tvUserName : TextView

        init {
            imgProfileImage = itemView.findViewById(R.id.imgProfilePic)
            tvDisplayName = itemView.findViewById(R.id.tvDisplayName)
            tvUserName = itemView.findViewById(R.id.tvUserName)
        }

        fun bindUserData(user : User){
            tvDisplayName.text = user.display_name
            tvUserName.text = user.username

        }

    }
}