package com.ganeshgfx.projectmanagement.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ganeshgfx.projectmanagement.Utils.DateNavListDiffUtil
import com.ganeshgfx.projectmanagement.databinding.CalenderNavChipListItemBinding

class DatesNavListAdapter : RecyclerView.Adapter<DatesNavListAdapter.DateList>() {

    private var list = mutableListOf<String>()

    class DateList(val binding: CalenderNavChipListItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DateList =
        DateList(
            CalenderNavChipListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun onBindViewHolder(holder: DateList, position: Int) {
        val item = list[position]
        val binding = holder.binding
        binding.date.text = item
        binding.date.setOnClickListener {
            onPressListener?.let {
                it(item)
            }
        }
    }

    private var onPressListener: ((String) -> Unit)? = null
    fun setOnPressListener(listener: (String) -> Unit) {
        onPressListener = listener
    }

    override fun getItemCount(): Int = list.size

    fun setData(newList: List<String>) {
        val diffUtil = DateNavListDiffUtil(list, newList)
        val diffResult = DiffUtil.calculateDiff(diffUtil)
        list.clear()
        list.addAll(newList)
        diffResult.dispatchUpdatesTo(this)
    }

    fun addData(dates: List<String>) {
        val newList = mutableListOf<String>()
        newList.addAll(list)
        newList.addAll(dates)
//        log("new",newList.size)
//        log("old",list.size)
        setData(newList)
    }

    fun clearList() {
        setData(emptyList())
    }
}