package com.route.todoappc43gsunwed.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.TextViewCompat
import androidx.fragment.app.Fragment
import com.kizitonwose.calendar.core.WeekDay
import com.kizitonwose.calendar.core.atStartOfMonth
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.kizitonwose.calendar.view.WeekDayBinder
import com.route.todoappc43gsunwed.R
import com.route.todoappc43gsunwed.adapter.ItemDayViewContainer
import com.route.todoappc43gsunwed.adapter.TaskListAdapter
import com.route.todoappc43gsunwed.database.TaskDatabase
import com.route.todoappc43gsunwed.databinding.FragmentTaskListBinding
import com.route.todoappc43gsunwed.extension.clearTime
import com.route.todoappc43gsunwed.extension.toCalendarInstant
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.TextStyle
import java.util.Calendar
import java.util.Date
import java.util.Locale

class TaskListFragment : Fragment() {
    private lateinit var binding: FragmentTaskListBinding
    private val adapter = TaskListAdapter()
    private val calendar = Calendar.getInstance()
    var selectedDate: LocalDate? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTaskListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tasksRecyclerView.adapter = adapter
        getAllTasks()
        initCalendarView()
        adapter.onTaskClickListener =  { task ->
            val editFragment = EditTaskFragment.newInstance(task)
            parentFragmentManager.beginTransaction()
                .replace(R.id.task_fragment_container, editFragment)
                .addToBackStack(null)
                .commit()
        }
    }

    fun initCalendarView() {
        binding.weekCalendarView.dayBinder = object : WeekDayBinder<ItemDayViewContainer> {
            override fun create(view: View): ItemDayViewContainer {
                return ItemDayViewContainer(view)
            }

            override fun bind(
                container: ItemDayViewContainer,
                data: WeekDay
            ) {
                container.weekDayTextView.text = data.date.dayOfWeek.getDisplayName(
                    TextStyle.SHORT,
                    Locale.getDefault()
                )
                container.monthDayTextView.text = "${data.date.dayOfMonth}"
                if (selectedDate == data.date) {
                    TextViewCompat.setTextAppearance(
                        container.weekDayTextView,
                        R.style.SelectedWeeKCalendarTextStyle
                    )
                    TextViewCompat.setTextAppearance(
                        container.monthDayTextView,
                        R.style.SelectedWeeKCalendarTextStyle
                    )
                } else {
                    TextViewCompat.setTextAppearance(
                        container.weekDayTextView,
                        R.style.WeeKCalendarTextStyle
                    )
                    TextViewCompat.setTextAppearance(
                        container.monthDayTextView,
                        R.style.WeeKCalendarTextStyle
                    )
                }
                container.itemView.setOnClickListener {
                    val currentSelection = selectedDate
                    if (currentSelection == data.date) {
                        // Remove Selection
                        selectedDate = null
                        getAllTasks()
                        binding.weekCalendarView.notifyDateChanged(currentSelection)
                    } else {
                        selectedDate = data.date
                        binding.weekCalendarView.notifyDateChanged(data.date)
                        Log.e("TAG", "date . month :   ${data.date.monthValue} ")

                        val date =
                            Date.from(data.date.toCalendarInstant())

                        calendar.time = date
                        Log.e("TAG", "calendar . month 1 :   ${calendar.get(Calendar.MONTH)} ")
                        //   25 / 2 / 2026   00:00AM
                        calendar.clearTime()
                        val secondsInDay = 86_400_000L
                        val endDate = calendar.time.time + secondsInDay
                        getTasksByDate(
                            calendar.time, Date(endDate)
                        )
                        Log.e("Start Date", "Start Date: ${calendar.time.time}")
                        Log.e("End Date", "End Date: ${Date(endDate).time}")
                        if (currentSelection != null) {
                            binding.weekCalendarView.notifyDateChanged(currentSelection)

                        }

                    }

                }
            }
        }
        val currentDate = LocalDate.now()
        val currentMonth = YearMonth.now()
        val startDate = currentMonth.minusMonths(100).atStartOfMonth() // Adjust as needed
        val endDate = currentMonth.plusMonths(100).atEndOfMonth() // Adjust as needed
        val firstDayOfWeek = firstDayOfWeekFromLocale() // Available from the library
        binding.weekCalendarView.setup(startDate, endDate, firstDayOfWeek)
        binding.weekCalendarView.scrollToWeek(currentDate)
    }

    fun getTasksByDate(startDate: Date, endDate: Date) {
        val tasks =
            TaskDatabase.getInstance(requireContext().applicationContext).getTaskDao()
                .getTasksByDate(startDate, endDate)
        adapter.updateTasks(tasks)
    }

    fun getAllTasks() {
        val tasks =
            TaskDatabase.getInstance(requireContext().applicationContext).getTaskDao().getAllTasks()
        adapter.updateTasks(tasks)
    }
    override fun onResume() {
        super.onResume()
        if (selectedDate != null) {
            val startDate = Date.from(selectedDate?.toCalendarInstant())
            calendar.time = startDate
            calendar.clearTime()
            val endDate = calendar.time.time + 86_400_000L
            getTasksByDate(calendar.time, Date(endDate))
        } else {
            getAllTasks()
        }
    }
    // How to make 2 Fragments Communicate With Each other ?
    //              1-  Interface Callback / Delegates
    //              2-  Shared View Model


    /**
     *  0- ToDo App  date Filtration
     *  1- Kotlin Extensions  ->
     *  2- Lambda Expression
     *
     *
     *  3- Jetpack compose
     */
}
