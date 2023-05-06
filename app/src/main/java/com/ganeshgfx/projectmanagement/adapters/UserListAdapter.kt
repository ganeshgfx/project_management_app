package com.ganeshgfx.projectmanagement.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ganeshgfx.projectmanagement.Utils.ProjectListDiffUtil
import com.ganeshgfx.projectmanagement.Utils.UserListDiffUtil
import com.ganeshgfx.projectmanagement.Utils.log
import com.ganeshgfx.projectmanagement.databinding.ProjectListItemBinding
import com.ganeshgfx.projectmanagement.databinding.UserListItemBinding
import com.ganeshgfx.projectmanagement.models.ProjectMember
import com.ganeshgfx.projectmanagement.models.ProjectWithTasks
import com.ganeshgfx.projectmanagement.models.Status
import com.ganeshgfx.projectmanagement.models.User

class UserListAdapter(val viewControls : Boolean = true) :
    RecyclerView.Adapter<UserListAdapter.UserListViewHolder>() {

    private var users = emptyList<ProjectMember>()

    class UserListViewHolder(val binding: UserListItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, type: Int): UserListViewHolder =
        UserListViewHolder(
            UserListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun onBindViewHolder(holder: UserListViewHolder, position: Int) {
        with(holder.binding) {
            //log(users[position])
            val item = users[position]
            if (!viewControls){
                removeButton.visibility = View.GONE
                addButton.visibility = View.GONE
            }else{
                if(item.member!=null){
                    addButton.visibility = View.GONE
                    removeButton.visibility = View.VISIBLE
                }else{
                    addButton.visibility = View.VISIBLE
                    removeButton.visibility = View.GONE
                }
            }
            data = item
            addButton.setOnClickListener {
                onClickListener?.let { it(item.user, Event.ADD) }
            }
            removeButton.setOnClickListener {
                onClickListener?.let { it(item.user, Event.DELETE) }
            }
        }
    }

    override fun getItemCount(): Int = users.size

    fun setData(newList: List<ProjectMember>) {
        val diffUtil = UserListDiffUtil(users, newList)
        val diffResult = DiffUtil.calculateDiff(diffUtil)
        users = newList
        diffResult.dispatchUpdatesTo(this)
    }

    fun setSearchData(searchList: List<ProjectMember>) {
        //logic for not including already added users
        val tempList = searchList.filter { foundUser ->
            users.find { foundUser.user.uid == it.user.uid } == null
        }
        setData(tempList)
    }

    fun clearList() {
        setData(emptyList())
    }

    private var onClickListener: ((User, Event) -> Unit)? = null
    fun setOnClickListener(listener: (User, Event) -> Unit) {
        onClickListener = listener
    }

    enum class Event {
        ADD,
        DELETE
    }

}