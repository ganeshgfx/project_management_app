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
import com.ganeshgfx.projectmanagement.adapters.DatesNavListAdapter
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

    private var _days = listOf<Day>()
    val days get() = _days

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
                    taskList.mapNotNull { date ->
                        date.endDate?.let {
                            dateRange.add(date.startDate!!.toLong()..date.endDate!!.toLong())
                            dateString(it)
                        }
                    }
                dates.addAll(lists)

                _days = getCalender()
                adapter.setData(days)

                val x = dates.map { epochMillis(it) }.sorted()
                val y = x.map { dateString(it)!! }

                datesAdapter.setData(y)
                datesAdapter.setOnPressListener { date ->
                    val day = dateStringToDay(date)
                    adapter.setData(getCalender(day))
                }
            }
        }
    }

    val adapter = CalenderAdapter()
    val datesAdapter = DatesNavListAdapter()

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

    private fun getCalender(day: Day? = null): List<Day> {

        var year = Year.now().value
        val length = 3
        var range = 1..1 + length

        if (day != null) {
            year = day.year
            var month = day.month

            if (month + length > 12) {
                month = 12 - length
            }

            range = month..month + length

            //log(day.date, year, range)

        } else if (dates.isNotEmpty()) {
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
                //log(month)
            }

            range = month..month + length

            //log(date.date, year, range)
        }

        val days = range.map { month ->
            getMonthDays(month, year)
        }.flatten()
        return days
    }

    fun getMonthDays(
        month: Int,
        year: Int
    ): List<Day> {
        val lastDay: Int = getLastDay(year, month)
        val days = (1..lastDay).map { day ->
            val epochMillis: Long = epochMillis("${day}/${month}/${year}")
            val isDue = dateRange.map { epochMillis in it }.contains(true)
            Day(day, month, year, isDue)
        }
       // log(days.map { it.date })
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

    fun newPageFirst() {
        val day = days.first()
        var month = day.month
        var year = day.year
        if (day.month == 1) {
            month = 12
            year--
        } else {
            month--
        }
        val nextList = getMonthDays(month, year)

        val list = _days.toMutableList()
        list.addAll(0, nextList)

        _days = list

        adapter.setData(days)
    }

}