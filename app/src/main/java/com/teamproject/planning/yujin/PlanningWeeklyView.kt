package com.teamproject.planning.yujin

import android.app.DatePickerDialog
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.res.ResourcesCompat
import com.teamproject.planning.MainActivity
import com.teamproject.planning.R
import com.teamproject.planning.Schedule
import com.teamproject.planning.databinding.ViewPlanningWeeklyBinding
import java.time.LocalDate
import java.time.YearMonth
import kotlin.math.abs


class PlanningWeeklyView(context: Context?, attrs: AttributeSet?) : LinearLayout(context, attrs) {
    var time = (context as MainActivity).time
        set(value) {
            (context as MainActivity).time = value
            field = value
        }
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
        binding.textviewWeeklyTitle.setOnClickListener {
            DatePickerDialog(this.context, { _, y, m, d ->
                time = LocalDate.of(y, m + 1, d)
                createWeekly()
                createTimeTable()
            }, time.year, time.monthValue - 1, time.dayOfMonth).show()
        }

//        createWeekly()
//        createTimeTable()
    }

    private fun createTimeTable() {
        //??? ?????? ??? ??????????????? 9????????? ?????????
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
                    foreground =
                        ResourcesCompat.getDrawable(resources, R.drawable.timetable_border, null)
                    gravity = Gravity.TOP or Gravity.START
                }
                tableRow.addView(textView)
            }
            binding.timeTableLayout.addView(tableRow)
        }
        binding.tableSchedulePanel.refreshData(time)
    }

    fun createWeekly() {
//        ??? ??? ???????????? ?????? (?????? : ??? ???????????? / ???)
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
        binding.textviewWeeklyTitle.text = "${montharr[time.monthValue - 1]} / ${time.year} "

        binding.tableWeekly.removeAllViews()
        binding.tableWeekly.addView(createWeeklyHeader())

        var sunday = time
        var saturday = time

        for (i in 1 until 8) {
            if (time.dayOfWeek.value == i) {
                sunday = time.minusDays((i % 7).toLong())     //??????????????? ????????? ????????? ??????

                //??????????????? ?????? ????????? ??????
                saturday = sunday.plusDays(6)
            }
        }

        var date = sunday

        val tableRow = TableRow(context).apply {
            layoutParams =
                TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT
                )
        }

        val textViewLP = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1F)

        // val result = ArrayList<Schedule>()
        val weeklyDataGroup = HashMap<String, ArrayList<Schedule>>()

        weeklyData.sortBy { it.startTime }

        for (sch in weeklyData) {
            if (sch.endTime.toLocalDate() < sunday) continue
            if (sch.startTime.toLocalDate() > saturday) continue

            var d = sch.startTime.toLocalDate()
            while (d <= sch.endTime.toLocalDate()) {
                if (!weeklyDataGroup.contains(d.toString()))
                    weeklyDataGroup[d.toString()] = ArrayList()

                weeklyDataGroup[d.toString()]!!.add(sch)
                d = d.plusDays(1L)
            }
        }

        // val weeklyDataGroup = result.groupBy { it.startTime.dayOfMonth }

        val indexArray = Array(7) { _ -> "" }
        val depthArray = IntArray(7)

        for (i in 0 until 7) {
            val textView = TextView(context).apply {
                textViewLP.setMargins(2, 2, 2, 2)
                layoutParams = textViewLP
                setPadding(10, 2, 10, 2)
                gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL

                text = if (date in sunday..saturday) date.dayOfMonth.toString() else ""

                indexArray[i] = date.toString()
                if (i == 0) setTextColor(Color.RED)
                else if (i == 6) setTextColor(Color.BLUE)
                else setTextColor(Color.BLACK)

                textSize = 10F

                setOnClickListener {
                    if ((it as TextView).text == "")
                        return@setOnClickListener
                    onClickDate(it.text.split("\n").first().toInt(), it)
                }
            }

            //?????? ?????? ?????? ??????
            if (date == time) {
                textView.foreground =
                    ResourcesCompat.getDrawable(resources, R.drawable.cursor, null)
                cursor = textView
            }

            tableRow.addView(textView)
            date = date.plusDays(1L)
        }
        binding.tableWeekly.addView(tableRow)

        //refresh
//        binding.weeklySchedulePanel.ym = YearMonth.of(time.year, time.month)
        // binding.weeklySchedulePanel.refreshData()
        val maxCount = weeklyDataGroup.values.maxOfOrNull { it.size } ?: return
        repeat(maxCount) { row ->
            val tableRow = TableRow(context).apply {
                layoutParams =
                    TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, 0, 1F)
            }
            val textViewLP = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1F)
            for (i in 0 until 7) {
                val textView = TextView(context).apply {
                    textViewLP.setMargins(2, 2, 2, 2)
                    layoutParams = textViewLP
                    setPadding(10, 2, 10, 2)
                    gravity = Gravity.TOP or Gravity.START

                    if (weeklyDataGroup.containsKey(indexArray[i])) {
                        if (weeklyDataGroup[indexArray[i]]?.size!! > depthArray[i]) {
                            text = weeklyDataGroup[indexArray[i]]?.get(depthArray[i])?.title
                            when (weeklyDataGroup[indexArray[i]]?.get(depthArray[i])?.color) {
                                1 -> setBackgroundResource(R.drawable.textview_background_radius1)
                                2 -> setBackgroundResource(R.drawable.textview_background_radius2)
                                3 -> setBackgroundResource(R.drawable.textview_background_radius3)
                                4 -> setBackgroundResource(R.drawable.textview_background_radius4)
                                5 -> setBackgroundResource(R.drawable.textview_background_radius5)
                                else -> setBackgroundResource(R.drawable.textview_background_radius1)
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
        val weeks = "?????????????????????"
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
                if (ch == '???') setTextColor(Color.BLUE)
                else if (ch == '???') setTextColor(Color.RED)
                else setTextColor(Color.BLACK)
                textSize = 20F
            }

            tableRow.addView(textView)
        }
        return tableRow
    }

    private fun onClickDate(date: Int, textView: TextView) {
        if (date != time.dayOfMonth) {
            time = if (abs(time.dayOfMonth - date) < 7) {
                //?????? ??? ????????? ??????
                LocalDate.of(time.year, time.monthValue, date)
            } else if (time.dayOfMonth < date) {
                // date ??? ?????? ?????? ??????
                val ym = YearMonth.of(time.year, time.monthValue).minusMonths(1)
                LocalDate.of(ym.year, ym.monthValue, date)
            } else {
                // date ??? ?????? ?????? ??????
                val ym = YearMonth.of(time.year, time.monthValue).plusMonths(1)
                LocalDate.of(ym.year, ym.monthValue, date)
            }
//        ??? ??? ???????????? ?????? (?????? : ??? ???????????? / ???)
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
            binding.textviewWeeklyTitle.text = "${montharr[time.monthValue - 1]} / ${time.year} "

            cursor.foreground = null
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