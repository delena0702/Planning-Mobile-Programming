package com.teamproject.planning

import android.app.DatePickerDialog
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.res.ResourcesCompat
import com.teamproject.planning.databinding.ViewPlanningCalendarBinding
import java.time.LocalDate
import java.time.YearMonth

class PlanningCalendarView(context: Context?, attrs: AttributeSet?) : LinearLayout(context, attrs) {
    var time = (context as MainActivity).time
        set(value) {
            (context as MainActivity).time = value
            field = value
        }
    private lateinit var cursor: TextView
    val binding by lazy { ViewPlanningCalendarBinding.bind(this) }
    var dateSelectedListener: OnDateSelectedListener? = null

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
        inflater.inflate(R.layout.view_planning_calendar, this)

        binding.textviewCalendarTitle.setOnClickListener {
            DatePickerDialog(this.context, { _, y, m, d ->
                time = LocalDate.of(y, m + 1, d)
                createCalendar()
            }, time.year, time.monthValue - 1, time.dayOfMonth).show()
        }

        binding.buttonCalendarLeft.setOnClickListener {
            time = time.minusMonths(1)
            createCalendar()
        }
        binding.buttonCalendarRight.setOnClickListener {
            time = time.plusMonths(1)
            createCalendar()
        }

        createCalendar()
    }

    fun createCalendar() {
        //        년 월 출력방식 변경 (형식 : 월 영문표기 / 년)
        val montharr = arrayOf<String>(
            "JAN",
            "FEB",
            "MAR",
            "APR",
            "MAY",
            "JUN",
            "JUL",
            "AUG",
            "SEP",
            "OCT",
            "NOV",
            "DEC"
        )
        binding.textviewCalendarTitle.text = "${montharr[time.monthValue - 1]} / ${time.year} "

        binding.tableCalendar.removeAllViews()
        binding.tableCalendar.addView(createCalendarHeader())


        var date = 1 - (LocalDate.of(time.year, time.monthValue, 1).dayOfWeek.value % 7)
        val lastDate = YearMonth.of(time.year, time.monthValue).atEndOfMonth().dayOfMonth
        var weekCount = 0

        while (date <= lastDate) {
            val tableRow = TableRow(context).apply {
                layoutParams =
                    TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, 0, 1F)
            }

            val textViewLP = TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 1F)
            for (i in 0 until 7) {
                val textView = TextView(context).apply {
                    layoutParams = textViewLP
                    setPadding(10, 10, 10, 10)
                    gravity = Gravity.TOP or Gravity.START

                    foreground = ResourcesCompat.getDrawable(resources, R.drawable.border, null)

                    text = if (date in 1..lastDate) date.toString() else ""
//                    일자 : 토요일 -> 파란색, 일요일 -> 빨간색, 나머지 -> 검정색
                    if (i == 0) setTextColor(Color.RED)
                    else if (i == 6) setTextColor(Color.BLUE)
                    else setTextColor(Color.BLACK)

                    textSize = 16F

                    setOnClickListener {
                        if ((it as TextView).text == "")
                            return@setOnClickListener
                        onClickDate(it.text.toString().toInt(), it)
                    }
                }

                if (date == time.dayOfMonth) {
                    textView.foreground =
                        ResourcesCompat.getDrawable(resources, R.drawable.cursor, null)
                    cursor = textView
                }

                tableRow.addView(textView)

                date++
            }
            binding.tableCalendar.addView(tableRow)
            weekCount++
        }
        refreshSchedulePanel(weekCount)
    }

    private fun createCalendarHeader(): TableRow {
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
//                gravity = Gravity.CENTER

                setBackgroundColor(Color.parseColor("#DDDDDD"))
                foreground = ResourcesCompat.getDrawable(resources, R.drawable.border, null)
                gravity = Gravity.CENTER
                text = ch.toString()
//              토 -> 파란색, 일 -> 빨간색, 나머지 -> 검은색
                if (ch == '토') setTextColor(Color.BLUE)
                else if (ch == '일') setTextColor(Color.RED)
                else setTextColor(Color.BLACK)
                textSize = 20F
            }

            tableRow.addView(textView)
        }
        return tableRow
    }

    private fun onClickDate(date: Int, textView: TextView) {
        if (date != time.dayOfMonth) {
            time = LocalDate.of(time.year, time.monthValue, date)
            cursor.foreground = ResourcesCompat.getDrawable(resources, R.drawable.border, null)
            textView.foreground = ResourcesCompat.getDrawable(resources, R.drawable.cursor, null)
            cursor = textView
        } else {
            dateSelectedListener?.onDateSelected(time)
        }
    }

    private fun refreshSchedulePanel(weekCount: Int) {
        binding.calendarSchedulePanel.weekCount = weekCount
        binding.calendarSchedulePanel.ym = YearMonth.of(time.year, time.month)
        binding.calendarSchedulePanel.refreshData()
    }
}