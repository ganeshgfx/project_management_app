package com.ganeshgfx.projectmanagement.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ganeshgfx.projectmanagement.Utils.log
import com.ganeshgfx.projectmanagement.adapters.CalenderAdapter
import com.ganeshgfx.projectmanagement.models.Day
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Year
import java.time.YearMonth
import javax.inject.Inject


@HiltViewModel
class CalenderViewModel @Inject constructor() : ViewModel() {

    val adapter = CalenderAdapter()

    val _loading = MutableLiveData(false)
    val loading get() = _loading

    var lastItem: Day? = null

    init {
        _loading.postValue(true)
        viewModelScope.launch {
            delay(150)
            val list = getCalender()
            adapter.setData(list)
            _loading.postValue(false)
        }
        adapter.setOnClickListener { day ->
            day?.let {
                //log(it)
            }
        }
        adapter.setOnLastItemLoad { day ->
            lastItem = day
        }
    }

    private fun getCalender(): List<Day> {
        val year = Year.now().value
        val days = (1..2).map { month ->
            getMonthDays(month, year)
        }.flatten()
        return days
    }

    fun getMonthDays(
        month: Int,
        year: Int
    ): List<Day> {
        val lastDay: Int = YearMonth.of(year, month).atEndOfMonth().dayOfMonth
        val days = (1..lastDay).map { day ->
            Day(day, month, year)
        }
        return days
    }

    fun newPage(day: Day) {
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

}