package com.examples.teamproject.yujin

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.examples.teamproject.MainActivity
import com.examples.teamproject.R
import com.examples.teamproject.Schedule
import com.examples.teamproject.databinding.ViewPlanningWeeklyBinding
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import kotlin.math.abs


class PlanningWeeklyView(context: Context?, attrs: AttributeSet?) : LinearLayout(context, attrs) {

    var time = LocalDate.now()!!
    val binding by lazy { ViewPlanningWeeklyBinding.bind(this) }
    private lateinit var cursor: TextView
    var dateSelectedListener: OnDateSelectedListener? = null
    private val dataCount = 49
    var weeklyData = (context as MainActivity).DBHelper!!.load()

    interface OnDateSelectedListener {
        fun onDateSelected(date: LocalDate)
    }

    companion object {
        const val HEADER_HEIGHT = 100
    }

    init {
        initLayout()
    }

    private fun initLayout() {
        val inflater = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.view_planning_weekly, this)

        binding.buttonWeeklyLeft.setOnClickListener {
            time = time.minusDays(7)
            createWeekly()
            createTimeTable()
        }
        binding.buttonWeeklyRight.setOnClickListener {
            time = time.plusDays(7)
            createWeekly()
            createTimeTable()
        }

//        createWeekly()
//        createTimeTable()
    }

    private fun createTimeTable() {
        //앱 실행 시 타임테이블 9시부터 보이게
        binding.tableScrollView.post { binding.tableScrollView.scrollTo(0, binding.nine.top) }

        binding.timeTableLayout.removeAllViews()

        for (i in 0 until dataCount) {
            val tableRow = TableRow(context).apply {
                layoutParams =
                    TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, 0, 1F)
            }

            val textViewLP = TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 1F)

            for (i in 0 until 7) {
                val textView = TextView(context).apply {
                    layoutParams = textViewLP
                    setPadding(10, 20, 10, 20)
                    gravity = Gravity.TOP or Gravity.START

                    foreground = ResourcesCompat.getDrawable(resources, R.drawable.timetable_border, null)
                }
                tableRow.addView(textView)
            }
            binding.timeTableLayout.addView(tableRow)
        }
        binding.tableSchedulePanel.refreshData(time)
    }

    fun createWeekly() {
        binding.textviewWeeklyTitle.text = "${time.year}년 ${time.monthValue}월"

        binding.tableWeekly.removeAllViews()
        binding.tableWeekly.addView(createWeeklyHeader())

        val lastDate = YearMonth.of(time.year, time.monthValue).atEndOfMonth().dayOfMonth

        //time(현재 커서 위치)의 전 달의 마지막 날 ex. 28,30,31
        val preMonthLastdate =
            YearMonth.of(time.year, time.monthValue - 1).atEndOfMonth().dayOfMonth

        var sunday = time
        var saturday = time

        for (i in 0 until 7) {
            if (time.dayOfWeek.value == i) {
                //이번주차의 시작인 일요일 날짜
                sunday = time.minusDays(i.toLong())

                //이번주차의 끝인 토요일 날짜
                saturday = sunday.plusDays(6)
            }
        }
        var date = sunday

        val tableRow = TableRow(context).apply {
            layoutParams =
                TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, 0, 1F)
        }

        val textViewLP = TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 1F)

        weeklyData.sortBy { it.startTime }
        val weeklyDataGroup = weeklyData.groupBy { it.startTime.dayOfMonth }
        val indexArray = IntArray(7)
        val depthArray = IntArray(7)

        for (i in 0 until 7) {
            val textView = TextView(context).apply {
                textViewLP.setMargins(2, 2, 2, 2)
                layoutParams = textViewLP
                setPadding(10, 2, 10, 2)
                gravity = Gravity.TOP or Gravity.START

                foreground = ResourcesCompat.getDrawable(resources, R.drawable.border, null)

                text = if (date in sunday..saturday) {
                    date.dayOfMonth.toString()
                } else ""
                indexArray[i] = date.dayOfMonth

                textSize = 10F

                setOnClickListener {
                    if ((it as TextView).text == "")
                        return@setOnClickListener
                    onClickDate(it.text.split("\n").first().toInt(), it)
                }
            }

            //현재 날짜 위치 커서
            if (date == time) {
                textView.foreground = ResourcesCompat.getDrawable(resources, R.drawable.cursor, null)
                cursor = textView
            }

            tableRow.addView(textView)
            date = date.plusDays(1L)
        }
        binding.tableWeekly.addView(tableRow)

        //refresh
//        binding.weeklySchedulePanel.ym = YearMonth.of(time.year, time.month)
        // binding.weeklySchedulePanel.refreshData()
        val maxCount = weeklyDataGroup.values.maxOf { it.size }
        repeat(maxCount) { row ->
            val tableRow = TableRow(context).apply {
                layoutParams =
                    TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, 0, 1F)
            }
            val textViewLP = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1F)
            for (i in 0 until 7) {
                val textView = TextView(context).apply {
                    textViewLP.setMargins(2, 2, 2, 2)
                    layoutParams = textViewLP
                    setPadding(10, 2, 10, 2)
                    gravity = Gravity.TOP or Gravity.START

                    if (weeklyDataGroup.containsKey(indexArray[i])) {
                        if (weeklyDataGroup[indexArray[i]]?.size!! > depthArray[i]){
                            text = weeklyDataGroup[indexArray[i]]?.get(depthArray[i])?.title
                            when (weeklyDataGroup[indexArray[i]]?.get(depthArray[i])?.color) {
                                1 ->setBackgroundResource(R.drawable.textview_background_radius1)
                                2 ->setBackgroundResource(R.drawable.textview_background_radius2)
                                3 ->setBackgroundResource(R.drawable.textview_background_radius3)
                                4 ->setBackgroundResource(R.drawable.textview_background_radius4)
                                5 ->setBackgroundResource(R.drawable.textview_background_radius5)
                                else ->setBackgroundResource(R.drawable.textview_background_radius1)
                            }

                            depthArray[i]++
                        }
                    }
                    textSize = 10F
                }
                tableRow.addView(textView)
            }
            binding.tableWeekly.addView(tableRow)
        }


    }

    private fun createWeeklyHeader(): TableRow {
        val weeks = "일월화수목금토"
        val tableRow = TableRow(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                HEADER_HEIGHT
            )
            setBackgroundColor(Color.parseColor("#ABCDEF"))
        }
        val textViewLP = TableRow.LayoutParams(0, HEADER_HEIGHT, 1F)
        for (ch in weeks) {
            val textView = TextView(context).apply {
                layoutParams = textViewLP
                setPadding(0, 10, 0, 10)
                gravity = Gravity.CENTER

                setBackgroundColor(Color.parseColor("#DDDDDD"))
                foreground = ResourcesCompat.getDrawable(resources, R.drawable.border, null)

                text = ch.toString()
                textSize = 20F
            }

            tableRow.addView(textView)
        }
        return tableRow
    }

    private fun onClickDate(date: Int, textView: TextView) {
        if (date != time.dayOfMonth) {
            time = if (abs(time.dayOfMonth - date) < 7) {
                //월에 안 걸치는 경우
                LocalDate.of(time.year, time.monthValue, date)
            } else if (time.dayOfMonth < date) {
                // date 가 이전 월인 경우
                val ym = YearMonth.of(time.year, time.monthValue).minusMonths(1)
                LocalDate.of(ym.year, ym.monthValue, date)
            } else {
                // date 가 다음 월인 경우
                val ym = YearMonth.of(time.year, time.monthValue).plusMonths(1)
                LocalDate.of(ym.year, ym.monthValue, date)
            }

            binding.textviewWeeklyTitle.text = time.format(DateTimeFormatter.ofPattern("yyyy년 M월"))

            cursor.foreground = ResourcesCompat.getDrawable(resources, R.drawable.border, null)
            textView.foreground = ResourcesCompat.getDrawable(resources, R.drawable.cursor, null)
            cursor = textView
        } else {
            dateSelectedListener?.onDateSelected(time)
        }
    }

    fun refreshData() {
        weeklyData = (context as MainActivity).DBHelper!!.load()
        // binding.weeklySchedulePanel.refreshData()
        createWeekly()
        createTimeTable()

        invalidate()
    }

}