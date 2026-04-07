package com.route.todoappc43gsunwed.fragments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.route.todoappc43gsunwed.R
import com.route.todoappc43gsunwed.callbacks.OnTaskAddedListener
import com.route.todoappc43gsunwed.database.Task
import com.route.todoappc43gsunwed.database.TaskDatabase
import com.route.todoappc43gsunwed.databinding.FragmentAddTaskBinding
import java.util.Calendar

class AddTaskBottomSheetFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentAddTaskBinding
    private lateinit var calendar: Calendar
    var onTaskAddedListener: OnTaskAddedListener? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        calendar = Calendar.getInstance()
        binding.selectDateValueTextView.setOnClickListener {
            openDatePickerDialog()
        }
        binding.selectTimeValueTextView.setOnClickListener {
            openTimePickerDialog()
        }
        binding.saveButton.setOnClickListener {
            if (validateFields()) {
                val task = Task(
                    title = binding.titleEditText.text.toString(),
                    date = calendar.time,
                    details = binding.descriptionEditText.text.toString()
                )
                TaskDatabase.getInstance(requireContext()).getTaskDao().insertTask(task)
                onTaskAddedListener?.onTaskAdded()
                dismiss()
            }
        }
    }

    private fun openTimePickerDialog() {
        val onTimeSet = object : TimePickerDialog.OnTimeSetListener {
            override fun onTimeSet(timePicker: TimePicker?, hour: Int, minutes: Int) {
                binding.selectTimeValueTextView.text = "$hour : $minutes"
                calendar.set(Calendar.HOUR_OF_DAY, hour)
                calendar.set(Calendar.MINUTE, minutes)
            }
        }
        val timePicker = TimePickerDialog(
            requireContext(),
            R.style.CustomTimePickerDialogTheme,
            onTimeSet,
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(
                Calendar.MINUTE
            ),
            false
        )
        timePicker.show()
    }

    private fun openDatePickerDialog() {
        val onDateSet = object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(
                datePicker: DatePicker?,
                year: Int,
                month: Int,
                dayOfMonth: Int
            ) {
                binding.selectDateValueTextView.text = "$dayOfMonth / ${month + 1} / $year"
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            }

        }
        val datePicker =
            DatePickerDialog(
                requireContext(),
                R.style.CustomDatePickerDialog,
                onDateSet,
                calendar.get(Calendar.YEAR),
                calendar.get(
                    Calendar.MONTH
                ),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
        datePicker.show()
    }

    private fun validateFields(): Boolean {
        val title = binding.titleEditText.text.toString()
        val description = binding.descriptionEditText.text.toString()
        val dateHint = getString(R.string.date_hint)
        val timeHint = getString(R.string.time_hint)
        if (title.isEmpty() || title.isBlank()) {
            binding.titleEditText.error = getString(R.string.task_title_is_required)
            return false
        } else
            binding.descriptionEditText.error = null
        if (title.length < 5) {
            binding.titleEditText.error = getString(R.string.task_title_is_short)
            return false
        } else
            binding.titleEditText.error = null

        if (description.isEmpty() || description.isBlank()) {
            binding.descriptionEditText.error = getString(R.string.task_desc_is_required)
            return false
        } else
            binding.descriptionEditText.error = null
        if (dateHint == binding.selectDateValueTextView.text.toString()) {
            Toast.makeText(
                requireContext(),
                getString(R.string.please_select_date),
                Toast.LENGTH_LONG
            ).show()
            return false
        }
        if (timeHint == binding.selectTimeValueTextView.text.toString()) {
            Toast.makeText(
                requireContext(),
                getString(R.string.please_select_date),
                Toast.LENGTH_LONG
            ).show()
            return false
        }
        return true
    }
}