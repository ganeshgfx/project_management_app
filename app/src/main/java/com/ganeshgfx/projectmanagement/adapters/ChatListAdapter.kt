package com.ganeshgfx.projectmanagement.adapters

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ganeshgfx.projectmanagement.Utils.ChatListDiffUtil
import com.ganeshgfx.projectmanagement.Utils.log
import com.ganeshgfx.projectmanagement.databinding.ChatListItemBinding
import com.ganeshgfx.projectmanagement.models.Chat

class ChatListAdapter : RecyclerView.Adapter<ChatListAdapter.ChatList>() {

    private var list = mutableListOf<Chat>()
    private lateinit var projectOnClickListener: ProjectOnClickListener

    class ChatList(val binding: ChatListItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatList =
        ChatList(
            ChatListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun onBindViewHolder(holder: ChatList, position: Int) {
        val item = list[position]
        holder.binding.chat = item
        val bubble = holder.binding.bubble
        val params = FrameLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            gravity = if (item.isMe) Gravity.END else Gravity.START
        }
        holder.binding.ai.apply {
            visibility = if (item.isMe) View.GONE else View.VISIBLE
        }
        holder.binding.me.apply {
            visibility = if (item.isMe) View.VISIBLE else View.GONE
        }
        bubble.layoutParams = params
    }

    override fun getItemCount(): Int = list.size

    fun setListener(projectOnClickListener: ProjectOnClickListener) {
        this.projectOnClickListener = projectOnClickListener
    }

    fun setData(newList: List<Chat>) {
        val diffUtil = ChatListDiffUtil(list, newList)
        val diffResult = DiffUtil.calculateDiff(diffUtil)
        list.clear()
        list.addAll(newList)
        diffResult.dispatchUpdatesTo(this)
    }

    fun addData(chat: List<Chat>) {
        val newList = mutableListOf<Chat>()
        newList.addAll(list)
        newList.addAll(chat)
//        log("new",newList.size)
//        log("old",list.size)
        setData(newList)
    }

    fun clearList() {
        setData(emptyList())
    }
}