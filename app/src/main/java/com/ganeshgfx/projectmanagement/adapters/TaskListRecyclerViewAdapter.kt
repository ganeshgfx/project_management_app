package com.ganeshgfx.projectmanagement.adapters

import android.content.res.ColorStateList
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ganeshgfx.projectmanagement.R
import com.ganeshgfx.projectmanagement.Utils.TaskListDiffUtil
import com.ganeshgfx.projectmanagement.databinding.TaskListItemBinding
import com.ganeshgfx.projectmanagement.models.Status
import com.ganeshgfx.projectmanagement.models.Task
import com.google.android.material.button.MaterialButton
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.ShapeAppearanceModel

class TaskListRecyclerViewAdapter(private var taskList: MutableList<Task> = mutableListOf()) :
    RecyclerView.Adapter<TaskListRecyclerViewAdapter.TaskListViewHolder>() {
    private lateinit var clickListener: TaskOnClickListener

    class TaskListViewHolder(val binding: TaskListItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    private var topRadius = 20F
    private var bottomRadius = 20F
    private var normalRadius = 20F
    private lateinit var focusCardShape:ShapeAppearanceModel
    private lateinit var normalCardShape:ShapeAppearanceModel

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskListViewHolder {
       topRadius = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            20.toFloat(),
           parent.context.resources.displayMetrics
        )
        bottomRadius = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            10.toFloat(),
            parent.context.resources.displayMetrics
        )
        normalRadius = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            3.toFloat(),
            parent.context.resources.displayMetrics
        )
        focusCardShape = ShapeAppearanceModel()
            .toBuilder()
            .setTopLeftCorner(CornerFamily.ROUNDED, topRadius)
            .setTopRightCorner(CornerFamily.ROUNDED, topRadius)
            .setBottomLeftCorner(CornerFamily.ROUNDED, normalRadius)
            .setBottomRightCorner(CornerFamily.ROUNDED, normalRadius)
            .build()
       normalCardShape = ShapeAppearanceModel()
            .toBuilder()
            .setTopLeftCorner(CornerFamily.ROUNDED, bottomRadius)
            .setTopRightCorner(CornerFamily.ROUNDED, bottomRadius)
            .setBottomLeftCorner(CornerFamily.ROUNDED, bottomRadius)
            .setBottomRightCorner(CornerFamily.ROUNDED, bottomRadius)
            .build()
        return TaskListViewHolder(
            TaskListItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: TaskListViewHolder, position: Int) {
        val task = taskList[position]
        with(holder.binding.mainCard) {
            setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    clickListener.onClick(position)
                    shapeAppearanceModel = focusCardShape
                } else {
                    shapeAppearanceModel = normalCardShape
                }
                toggleTaskEditView(holder, hasFocus)
            }
        }
        holder.binding.data = task
        with(holder.binding.taskStatusIcon) {
            when (task.status) {
                Status.IN_PROGRESS -> {
                    setStatusOfTask(
                        R.drawable.twotone_draw_24,
                        R.color.taskIn,
                        R.color.taskInBack
                    )
                }
                Status.DONE -> {
                    setStatusOfTask(
                        R.drawable.round_done_outline_24,
                        R.color.taskDone,
                        R.color.taskDoneBack
                    )
                }
                Status.PENDING -> {
                    setStatusOfTask(
                        R.drawable.outline_circle_24,
                        R.color.control,
                        R.color.controlBack
                    )
                }
            }
        }
    }

    fun toggleTaskEditView(holder: TaskListViewHolder, toggle: Boolean) {
        if (toggle) {
            //holder.binding.taskView.visibility = View.GONE
            holder.binding.taskEdit.visibility = View.VISIBLE
        } else {
            //holder.binding.taskView.visibility = View.VISIBLE
            holder.binding.taskEdit.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = taskList.size

    fun setData(newList: List<Task>) {
        val diffUtil = TaskListDiffUtil(taskList, newList)
        val diffResult = DiffUtil.calculateDiff(diffUtil)
        taskList.clear()
        taskList.addAll(newList)
        diffResult.dispatchUpdatesTo(this)
    }

    fun updateData(pos: Int, task: Task) {
        taskList[pos] = task
        notifyItemChanged(pos)
        //log(taskList.remove(task).toString())
    }

    fun clearList() {
        setData(emptyList<Task>())
    }

    infix fun getItem(pos: Int) = taskList[pos]

    fun setClickListner(clickListener: TaskOnClickListener) {
        this.clickListener = clickListener
    }
}

private fun MaterialButton.setStatusOfTask(
    iconDrawable: Int,
    iconColor: Int,
    backColor: Int
) {
    icon = ContextCompat.getDrawable(context, iconDrawable)
    iconTint = ColorStateList.valueOf(ContextCompat.getColor(context, iconColor))
    setBackgroundColor(ContextCompat.getColor(context, backColor))
}