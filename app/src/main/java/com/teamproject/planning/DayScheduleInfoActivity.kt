package com.teamproject.planning

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.teamproject.planning.databinding.ActivityDayScheduleInfoBinding
import com.teamproject.planning.databinding.RowDayScheduleBinding
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DayScheduleInfoActivity : AppCompatActivity() {
    private val binding by lazy { ActivityDayScheduleInfoBinding.inflate(layoutInflater) }
    private var data: ArrayList<Schedule>? = null
    private lateinit var time: LocalDate
    private var DBHelper: ScheduleDBHelper? = null
    private var isCompare = false

    inner class Adapter(val items: ArrayList<Schedule>) :
        RecyclerView.Adapter<Adapter.ViewHolder>() {
        inner class ViewHolder(val binding: RowDayScheduleBinding) :
            RecyclerView.ViewHolder(binding.root) {
            init {
                binding.root.setOnClickListener {
                    val intent =
                        Intent(this@DayScheduleInfoActivity, ScheduleInfoActivity::class.java)
                    intent.putExtra("schedule", items[adapterPosition])
                    intent.putExtra("isCompare", isCompare)
                    finish()
                    startActivity(intent)
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val b =
                RowDayScheduleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ViewHolder(b)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.binding.apply {
                var t = items[position].startTime
                textviewRowStartTime.text = t.format(DateTimeFormatter.ofPattern("M/d h:mm"))
                t = items[position].endTime
                textviewRowEndTime.text = t.format(DateTimeFormatter.ofPattern("M/d h:mm"))
                textviewRowTitle.text = items[position].title
            }
        }

        override fun getItemCount(): Int {
            return items.size
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        DBHelper = ScheduleDBHelper(this)
        initLayout()
    }

    private fun initLayout() {
        time = LocalDate.parse(intent.getStringExtra("time"))
        if (intent.hasExtra("schedules")) {
            isCompare = true
            val scheduleString = intent.getStringExtra("schedules")!!
            data = Schedule.parseJSON(scheduleString)
        } else {
            isCompare = false
            refreshData()
        }

        binding.textviewDayScheduleTitle.text = time.format(DateTimeFormatter.ofPattern("MM월 dd일"))

        binding.recyclerViewDaySchedule.adapter = Adapter(data!!)
        binding.recyclerViewDaySchedule.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        binding.root.setOnClickListener {
            finish()
        }
    }

    fun refreshData() {
        val dbData = DBHelper!!.load()

        data = ArrayList()

        for (schedule in dbData) {
            schedule.startTime.toLocalDate()
            schedule.endTime.toLocalDate()

            if (schedule.endTime.toLocalDate().isBefore(time) || schedule.startTime.toLocalDate()
                    .isAfter(time)
            )
                continue

            data!!.add(schedule)
        }
    }

    override fun onResume() {
        if (DBHelper == null)
            DBHelper = ScheduleDBHelper(this)
        super.onResume()
    }

    override fun onPause() {
        DBHelper?.close()
        DBHelper = null
        super.onPause()
    }
}