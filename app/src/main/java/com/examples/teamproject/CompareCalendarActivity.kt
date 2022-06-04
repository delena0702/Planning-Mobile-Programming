package com.examples.teamproject

import android.content.Intent
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.examples.teamproject.databinding.ActivityCompareCalendarBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.parser.Parser
import java.time.LocalDate

class CompareCalendarActivity : AppCompatActivity() {
    val binding by lazy { ActivityCompareCalendarBinding.inflate(layoutInflater) }
    var schedules = ArrayList<Schedule>()
    private val scope = CoroutineScope(Dispatchers.IO)
    var personCount = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val dbHelper = ScheduleDBHelper(this)
        schedules = dbHelper.load()

        for (sch in schedules)
            sch.color = 1

        dbHelper.close()
        initLayout()
    }

    private fun initLayout() {
        binding.apply {
            buttonCodeSubmit.setOnClickListener {
                val code = binding.edittextCode.text.toString().toInt()

                binding.layoutInput.visibility = View.GONE
                binding.compareCalendarProgressBar.visibility = View.VISIBLE

                scope.launch {
                    val doc =
                        Jsoup.connect("https://ku-planning-2022.herokuapp.com/connect?code=$code")
                            .parser(Parser.xmlParser()).get()

                    withContext(Dispatchers.Main) {
                        if (doc.select("state").text() == "0") {
                            Toast.makeText(applicationContext, "유효하지 않은 코드입니다.", Toast.LENGTH_SHORT).show()
                            finish()
                            return@withContext
                        }

                        appendOtherSchedule(Schedule.parseJSON(doc.select("data").text()))

                        binding.planningCompareCalendar.initLayout()

                        binding.compareCalendarProgressBar.visibility = View.GONE
                        binding.layoutCalendar.visibility = View.VISIBLE
                    }
                }
            }
            
            this.planningCompareCalendar.dateSelectedListener = object:PlanningCompareCalendarView.OnDateSelectedListener {
                override fun onDateSelected(date: LocalDate) {
                    val arr = ArrayList<Schedule>()

                    for (sch in schedules) {
                        if (sch.endTime.toLocalDate().isBefore(date))
                            continue
                        if (sch.startTime.toLocalDate().isAfter(date))
                            continue
                        arr.add(sch)
                    }

                    val str = Schedule.toJSONString(arr, true)
                    val intent = Intent(this@CompareCalendarActivity, DayScheduleInfoActivity::class.java)
                    intent.putExtra("schedules", str)
                    intent.putExtra("time", date.toString())
                    startActivity(intent)
                }
            }
        }
    }

    private fun appendOtherSchedule(otherSchedule : ArrayList<Schedule>) {
        personCount++

        for (sch in otherSchedule)
            sch.color = personCount
        schedules.addAll(otherSchedule)
    }
}