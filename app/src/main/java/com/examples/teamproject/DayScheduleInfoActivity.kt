package com.examples.teamproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.examples.teamproject.databinding.ActivityDayScheduleInfoBinding
import com.examples.teamproject.databinding.RowDayScheduleBinding
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DayScheduleInfoActivity : AppCompatActivity() {
    private val binding by lazy { ActivityDayScheduleInfoBinding.inflate(layoutInflater) }
    private var data: ArrayList<Schedule>? = null
    private lateinit var time: LocalDate


    inner class Adapter(val items: ArrayList<Schedule>) :
        RecyclerView.Adapter<Adapter.ViewHolder>() {
        inner class ViewHolder(val binding: RowDayScheduleBinding) :
            RecyclerView.ViewHolder(binding.root) {
            init {
                binding.root.setOnClickListener {
                    val intent =
                        Intent(this@DayScheduleInfoActivity, ScheduleInfoActivity::class.java)
                    intent.putExtra("schedule", items[adapterPosition])
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

        initLayout()
    }

    private fun initLayout() {
        time = LocalDate.parse(intent.getStringExtra("time"))
        refreshData()

        binding.textviewDayScheduleTitle.text =time.format(DateTimeFormatter.ofPattern("MM월 dd일"))

        binding.recyclerViewDaySchedule.adapter = Adapter(data!!)
        binding.recyclerViewDaySchedule.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        binding.root.setOnClickListener {
            finish()
        }
    }

    fun refreshData() {
        val dbData = llooaadd()

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
}