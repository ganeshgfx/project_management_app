package com.ganeshgfx.projectmanagement.adapters

import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ganeshgfx.projectmanagement.Utils.TaskListDiffUtil
import com.ganeshgfx.projectmanagement.databinding.TaskListItemBinding
import com.ganeshgfx.projectmanagement.models.Task
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.ShapeAppearanceModel

class TaskListRecyclerViewAdapter(private var taskList: MutableList<Task> = mutableListOf()) :
    RecyclerView.Adapter<TaskListRecyclerViewAdapter.TaskListViewHolder>() {
    private lateinit var clickListener: OnClickListener

    class TaskListViewHolder(val binding: TaskListItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    private var _20dp = 20F
    private var _10dp = 10F
    private var _3dp = 3F
    private var focusCardShape: ShapeAppearanceModel? = null
    private var normalCardShape: ShapeAppearanceModel? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskListViewHolder {
        _20dp = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            20.toFloat(),
            parent.context.resources.displayMetrics
        )
        _10dp = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            10.toFloat(),
            parent.context.resources.displayMetrics
        )
        _3dp = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            3.toFloat(),
            parent.context.resources.displayMetrics
        )
        focusCardShape = makeShape(_20dp, _3dp);
        normalCardShape = makeShape(_10dp, _10dp)

        return TaskListViewHolder(
            TaskListItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: TaskListViewHolder, position: Int) {
        val task = taskList[position]
        with(holder.binding.mainCard) {
            var editisible = false
            setOnFocusChangeListener { _, hasFocus ->
                // log("has focus : $hasFocus")
                if (hasFocus) {
                    clickListener.onClick(position)
                    shapeAppearanceModel = focusCardShape!!
                } else shapeAppearanceModel = normalCardShape!!
                toggleTaskEditView(holder, hasFocus)
                editisible = hasFocus
            }
            setOnClickListener {
                if (editisible) clearFocus()
            }
        }
        holder.binding.data = task
    }

    private fun toggleTaskEditView(holder: TaskListViewHolder, toggle: Boolean) {
        if (toggle) {
            holder.binding.taskEdit.visibility = View.VISIBLE
        } else {
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
    }

    fun clearList() {
        setData(emptyList<Task>())
    }

    infix fun getItem(pos: Int) = taskList[pos]

    fun setClickListener(clickListener: OnClickListener) {
        this.clickListener = clickListener
    }

    private fun makeShape(topRadius: Float, bottomRadius: Float) =
        ShapeAppearanceModel()
            .toBuilder()
            .setTopLeftCorner(CornerFamily.ROUNDED, topRadius)
            .setTopRightCorner(CornerFamily.ROUNDED, topRadius)
            .setBottomLeftCorner(CornerFamily.ROUNDED, bottomRadius)
            .setBottomRightCorner(CornerFamily.ROUNDED, bottomRadius)
            .build()
}