package com.ganeshgfx.projectmanagement.adapters

import android.graphics.Color
import android.util.DisplayMetrics
import android.util.TypedValue.*
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ganeshgfx.projectmanagement.Utils.*
import com.ganeshgfx.projectmanagement.databinding.CalenderListItemBinding
import com.ganeshgfx.projectmanagement.models.Day
import com.ganeshgfx.projectmanagement.models.Task
import com.ganeshgfx.projectmanagement.models.User
import com.ganeshgfx.projectmanagement.views.custom.WeekView
import com.google.android.material.shape.ShapeAppearanceModel

class CalenderAdapter(private var list: MutableList<List<Day>> = mutableListOf()) :
    RecyclerView.Adapter<CalenderAdapter.CalenderViewHolder>() {

    class CalenderViewHolder(val binding: CalenderListItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalenderViewHolder {
        return CalenderViewHolder(
            CalenderListItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: CalenderViewHolder, position: Int) {
        val binding = holder.binding
        val week = list[position]
        val last = week.last()
        binding.month.text = "${last.monthText} | ${last.year.toString()[2]}${last.year.toString()[3]}"
        binding.week.setWeek(week)
        binding.week.setOnClickListener { day ->
            onClickListener?.let { it -> it(day) }
        }
        onLastItemLoad?.let { day ->
            val temp = list.size - 1
            val condition = position == temp
            //log(position,temp,condition)
            day(if (condition) last else null)
        }
       // log(position)
        onFirstItemLoad?.let { day ->
            val condition = position == 0
            day(if (condition) week.first() else null)
        }

    }

    private var onClickListener: ((Day?) -> Unit)? = null
    fun setOnClickListener(listener: (Day?) -> Unit) {
        onClickListener = listener
    }

    private var onLastItemLoad: ((Day?) -> Unit)? = null
    fun setOnLastItemLoad(listener: (Day?) -> Unit) {
        onLastItemLoad = listener
    }

    private var onFirstItemLoad: ((Day?) -> Unit)? = null
    fun setonFirstItemLoad(listener: (Day?) -> Unit) {
        onFirstItemLoad = listener
    }

    fun setData(newList: List<Day>) {

        val newList = newList.chunked(7)

        val diffUtil = CalenderListDiffUtil(list, newList)
        val diffResult = DiffUtil.calculateDiff(diffUtil)
        list.clear()
        list.addAll(newList)
        diffResult.dispatchUpdatesTo(this)
    }

    fun addData(newData: List<Day>) {
        val oldList = list.flatten().toMutableList()
        oldList.addAll(newData)
        setData(oldList)
    }
    fun addDataOnTop(newData: List<Day>) {
        val oldList = list.flatten().toMutableList()
        oldList.addAll(0,newData)
        setData(oldList)
    }
    fun addDataTop(newData: List<Day>) {
        list.addAll(0,newData.chunked(7))
    }

}