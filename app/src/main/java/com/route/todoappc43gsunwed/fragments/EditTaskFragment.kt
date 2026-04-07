package com.route.todoappc43gsunwed.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.route.todoappc43gsunwed.database.Task
import com.route.todoappc43gsunwed.database.TaskDatabase
import com.route.todoappc43gsunwed.databinding.FragmentEditTaskBinding
import java.util.Calendar
import java.util.Date

class EditTaskFragment : Fragment() {
    private lateinit var binding: FragmentEditTaskBinding
    private var task: Task? = null
    private var selectedDate: Date? = null
    private val calendar = Calendar.getInstance()

    companion object {
        private const val ARG_TASK = "task"
        fun newInstance(task: Task): EditTaskFragment {
            val fragment = EditTaskFragment()
            val bundle = Bundle()
            bundle.putSerializable(ARG_TASK, task)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        task = arguments?.getSerializable(ARG_TASK) as? Task
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Prefill fields
        task?.let {
            binding.editTitle.setText(it.title)
            binding.editDetails.setText(it.details)
            selectedDate = it.date
            it.date?.let { date ->
                calendar.time = date
                binding.selectedTimeText.text = formatDate(
                    calendar.get(Calendar.DAY_OF_MONTH),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.YEAR)
                )
            }
        }

        // Open TimePicker
        binding.selectedTimeText.setOnClickListener {
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            val month = calendar.get(Calendar.MONTH)
            val year = calendar.get(Calendar.YEAR)
            DatePickerDialog(requireContext(), { _, y, m, d ->
                val originalDate = task?.date
                if (originalDate != null) {
                    calendar.time = originalDate
                }
                calendar.set(Calendar.YEAR, y)
                calendar.set(Calendar.MONTH, m)
                calendar.set(Calendar.DAY_OF_MONTH, d)
                selectedDate = calendar.time
                binding.selectedTimeText.text = formatDate(d, m, y)
            }, year, month, day).show()
        }

        binding.saveChangesButton.setOnClickListener {
            val newTitle = binding.editTitle.text.toString().trim()
            if (newTitle.isEmpty()) {
                binding.editTitle.error = "Title is required"
                return@setOnClickListener
            }
            val updatedTask = task?.copy(
                title = newTitle,
                details = binding.editDetails.text.toString().trim(),
                date = selectedDate
            )
            updatedTask?.let {
                TaskDatabase.getInstance(requireContext().applicationContext)
                    .getTaskDao().updateTask(it)
                Toast.makeText(requireContext(), "Task updated!", Toast.LENGTH_SHORT).show()
                parentFragmentManager.popBackStack()
            }
        }

        binding.backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun formatDate(day: Int, month: Int, year: Int): String {
        return String.format("%d - %d - %d", day, month + 1, year)
    }
}