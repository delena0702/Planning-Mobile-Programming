package com.examples.teamproject

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.examples.teamproject.databinding.FragmentCalendarBinding
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class CalendarFragment : Fragment() {
    val binding by lazy { FragmentCalendarBinding.inflate(layoutInflater) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding.planningCalendar.dateSelectedListener = object:PlanningCalendarView.OnDateSelectedListener {
            override fun onDateSelected(date: LocalDate) {
                val intent = Intent(this@CalendarFragment.context, DayScheduleInfoActivity::class.java)
                val t = binding.planningCalendar.time
                intent.putExtra("time",t.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                startActivity(intent)
            }
        }

        binding.floatingButtonAdd.setOnClickListener {
            val intent = Intent(this.context, EditScheduleInfoActivity::class.java)
            startActivity(intent)
        }

        return binding.root
    }

    override fun onResume() {
        binding.planningCalendar.createCalendar()
        super.onResume()
    }
}