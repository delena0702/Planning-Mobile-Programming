package com.examples.teamproject.yujin

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.examples.teamproject.DayScheduleInfoActivity
import com.examples.teamproject.EditScheduleInfoActivity
import com.examples.teamproject.databinding.FragmentWeeklyScheduleBinding
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class WeeklyScheduleFragment : Fragment() {
    val binding by lazy { FragmentWeeklyScheduleBinding.inflate(layoutInflater) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // 임의로 planningWeekly View의 weeklyData 정보 세팅함
        //binding.planningWeekly.weeklyData = llooaadd()

        //주간 캘린더의 날짜(커서) 누르면 해당 날짜 일정 정보 띄우기
        binding.planningWeekly.dateSelectedListener = object:
            PlanningWeeklyView.OnDateSelectedListener {
            override fun onDateSelected(date: LocalDate) {
                val intent = Intent(this@WeeklyScheduleFragment.context, DayScheduleInfoActivity::class.java)
                val t = binding.planningWeekly.time
                intent.putExtra("time",t.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                startActivity(intent)
            }
        }


        //타임테이블 클릭 시 해당 열 날짜의 일정 정보 띄우기
        binding.planningWeekly.binding.tableSchedulePanel.touchEventListener = object:
            TableSchedulePanel.OnTouchEventListener {
            override fun onTouch(date: LocalDate) {
                val intent = Intent(this@WeeklyScheduleFragment.context, DayScheduleInfoActivity::class.java)
                val t = binding.planningWeekly.binding.tableSchedulePanel.pointDate
                intent.putExtra("time", t!!.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                startActivity(intent)
            }
        }

        binding.floatingButton2Add.setOnClickListener {
            val intent = Intent(this.context, EditScheduleInfoActivity::class.java)
            startActivity(intent)
        }

        return binding.root
    }

//    override fun onResume(){
//        binding.planningWeekly.createWeekly()
//        super.onResume()
//    }

}