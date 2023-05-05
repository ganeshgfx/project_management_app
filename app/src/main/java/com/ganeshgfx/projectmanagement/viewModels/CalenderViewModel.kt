package com.ganeshgfx.projectmanagement.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ganeshgfx.projectmanagement.Utils.dateString
import com.ganeshgfx.projectmanagement.Utils.dateStringToDay
import com.ganeshgfx.projectmanagement.Utils.epochMillis
import com.ganeshgfx.projectmanagement.Utils.getLastDay
import com.ganeshgfx.projectmanagement.Utils.log
import com.ganeshgfx.projectmanagement.adapters.CalenderAdapter
import com.ganeshgfx.projectmanagement.models.Day
import com.ganeshgfx.projectmanagement.repositories.TaskListRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.time.Year
import javax.inject.Inject


@HiltViewModel
class CalenderViewModel @Inject constructor(private val repo: TaskListRepository) : ViewModel() {


    private var _currentProjectId = ""

    private var dates = mutableListOf<String>()

    private val dateRange = mutableListOf<ClosedRange<Long>>()

    private var tasksFlowJob: Job? = null
    fun getTasks(projectId: String) {
        _currentProjectId = projectId
        // log(projectId)
        tasksFlowJob?.cancel()
        tasksFlowJob = viewModelScope.launch {
            repo.tasksFlow(_currentProjectId).collect { taskList ->
                dateRange.clear()
                dates.clear()
                val lists =
                    taskList.map { date ->
                        date.endDate?.let {
                            dateRange.add(date.startDate!!.toLong()..date.endDate!!.toLong())
                            dateString(it)
                        }
                    }.filterNotNull()
                //log(dateRange)
                dates.addAll(lists)

                val weeks = getCalender()
                adapter.setData(weeks)
            }
        }
    }

    val adapter = CalenderAdapter()

    private val _loading = MutableLiveData(false)
    val loading get() = _loading

    var lastItem: Day? = null
    var firstItem: Day? = null

    init {
        adapter.setOnLastItemLoad { day ->
            lastItem = day
        }
        adapter.setonFirstItemLoad { day ->
            firstItem = day
        }
    }

    private fun getCalender(): List<Day> {

        var year = Year.now().value
        val length = 3
        var range = 1..1 + length

        if (dates.isNotEmpty()) {
            val date = dateStringToDay(
                dates.map {
                    epochMillis(it)
                }
                    .sorted()
                    .map {
                        dateString(it)
                    }.first()
                    .toString()
            )
            year = date.year
            var month = date.month

            if (month + length > 12) {
                month = 12 - length
            }

            range = month..month + length

            log(date.date,year, range)
        }

        val days = range.map { month ->
            getMonthDays(month, year)
        }.flatten()
        //log(days)
        return days
    }

    fun getMonthDays(
        month: Int,
        year: Int
    ): List<Day> {
        val lastDay: Int = getLastDay(year, month)
        val days = (1..lastDay).map { day ->

            val epochMillis: Long = epochMillis("${day}/${month}/${year}")

            val dateString = dateString(epochMillis).toString()

            //val isTaskDay = dates.contains(dateString)

            val isDue = dateRange.map { epochMillis in it }.contains(true)

            // log(isDue)

            //if (isTaskDay) {
            //log(epochMillis, dates.map { epochMillis(it) })
            // }

            val day = Day(day, month, year, isDue)
            day
        }
        return days
    }

    fun newPageLast(day: Day) {
        var month = day.month
        var year = day.year
        if (day.month == 12) {
            month = 1
            year++
        } else {
            month++
        }
        val dayList = getMonthDays(month, year)
        adapter.addData(dayList)
    }

    fun newPageFirst(day: Day) {
        var month = day.month
        var year = day.year
        if (day.month == 1) {
            month = 12
            year--
        } else {
            month--
        }
        val dayList = getMonthDays(month, year)
        adapter.addDataOnTop(dayList)
    }

}