package com.ganeshgfx.projectmanagement.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ganeshgfx.projectmanagement.Utils.ProjectListDiffUtil
import com.ganeshgfx.projectmanagement.Utils.UserListDiffUtil
import com.ganeshgfx.projectmanagement.Utils.log
import com.ganeshgfx.projectmanagement.databinding.ProjectListItemBinding
import com.ganeshgfx.projectmanagement.databinding.UserListItemBinding
import com.ganeshgfx.projectmanagement.models.ProjectWithTasks
import com.ganeshgfx.projectmanagement.models.Status
import com.ganeshgfx.projectmanagement.models.User

class UserListAdapter() :
    RecyclerView.Adapter<UserListAdapter.UserListViewHolder>() {

    private var users = emptyList<User>()

    class UserListViewHolder(val binding: UserListItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserListViewHolder =
        UserListViewHolder(
            UserListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun onBindViewHolder(holder: UserListViewHolder, position: Int) {
        holder.binding.user = users[position]
        //log(users[position])
    }

    override fun getItemCount(): Int = users.size

    fun setData(newList: List<User>) {
//        log(newList)
        val diffUtil = UserListDiffUtil(users, newList)
        val diffResult = DiffUtil.calculateDiff(diffUtil)
        users = newList
        diffResult.dispatchUpdatesTo(this)
    }

    fun clearList(){
        setData(emptyList<User>())
    }
}