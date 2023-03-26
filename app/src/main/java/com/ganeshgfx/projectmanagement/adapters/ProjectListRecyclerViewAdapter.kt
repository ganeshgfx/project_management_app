package com.ganeshgfx.projectmanagement.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ganeshgfx.projectmanagement.R
import com.ganeshgfx.projectmanagement.Utils.ProjectListDiffUtil
import com.ganeshgfx.projectmanagement.Utils.log
import com.ganeshgfx.projectmanagement.databinding.ProjectListItemBinding
import com.ganeshgfx.projectmanagement.models.ProjectWithTasks
import com.ganeshgfx.projectmanagement.models.Status
import com.ganeshgfx.projectmanagement.models.User
import org.eazegraph.lib.models.PieModel

class ProjectListRecyclerViewAdapter(private val projectOnClickListener: ProjectOnClickListener) :
    RecyclerView.Adapter<ProjectListRecyclerViewAdapter.ProjectListViewHolder>() {

    private var projectList = emptyList<ProjectWithTasks>()

    class ProjectListViewHolder(val binding: ProjectListItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectListViewHolder =
        ProjectListViewHolder(
            ProjectListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun onBindViewHolder(holder: ProjectListViewHolder, position: Int) {
        val item = projectList[position]
        with(holder.binding) {
            card.setOnClickListener {
                projectOnClickListener.onClick(item)
            }
            val pending = item.getStatusCount(Status.PENDING)
            val inProgress = item.getStatusCount(Status.IN_PROGRESS)
            val done = item.getStatusCount(Status.DONE)
            //log(pending,inProgress,done)
            with(pieChart) {
                clearChart()
                if (pending > 0) addPieSlice(
                    PieModel(
                        "Pending", pending.toFloat(),
                        ContextCompat.getColor(card.context, R.color.control)
                    )
                )
                if (inProgress > 0) addPieSlice(
                    PieModel(
                        "In Progress", inProgress.toFloat(),
                        ContextCompat.getColor(
                            card.context,
                            R.color.taskInBack
                        )
                    )
                )
                if (done > 0) addPieSlice(
                    PieModel(
                        "Done", done.toFloat(),
                        ContextCompat.getColor(
                            card.context,
                            R.color.taskDoneBack
                        )
                    )
                )
                startAnimation()
            }
            data = item
        }
    }

    override fun getItemCount(): Int = projectList.size

    fun setData(newList: List<ProjectWithTasks>) {
        val diffUtil = ProjectListDiffUtil(projectList, newList)
        val diffResult = DiffUtil.calculateDiff(diffUtil)
        projectList = newList
        diffResult.dispatchUpdatesTo(this)
    }

    fun clearList() {
        setData(emptyList<ProjectWithTasks>())
    }
}