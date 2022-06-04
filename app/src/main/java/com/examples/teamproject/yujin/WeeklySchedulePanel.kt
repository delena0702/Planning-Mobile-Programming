package com.examples.teamproject.yujin

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import com.examples.teamproject.PlanningCalendarView
import com.examples.teamproject.Schedule
import com.examples.teamproject.llooaadd
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.ChronoUnit

class WeeklySchedulePanel(context: Context, attrs: AttributeSet?) :
    androidx.appcompat.widget.AppCompatTextView(context, attrs) {

    companion object{
        const val TAG = "WeeklySchedulePanel"
    }
    private val mainOffset = PlanningCalendarView.HEADER_HEIGHT
    private val dataCount = 3

    private var drawData: ArrayList<ArrayList<Schedule?>>? = null
    var ym: YearMonth = YearMonth.now()
    private val paint = Paint()
    var w = 0f
    var h = 0f

    override fun onDraw(canvas: Canvas?) {
        Log.e(TAG, "onDraw: ", )
        if (drawData == null) {
            super.onDraw(canvas)
            return
        }
        w = width.toFloat()
        h = (height - mainOffset).toFloat()
        canvas!!.translate(0F, mainOffset.toFloat())
        paint.textSize = 0.14F * h

        super.onDraw(canvas)
        //refreshData()
    }

    fun refreshData() {
        val data = llooaadd()
        data.sortWith { a, b -> a.startTime.compareTo(b.startTime) }

//        drawData = ArrayList(weekCount * 7 + 1)
//        for (i in 0..weekCount * 7) {
//            drawData!!.add(ArrayList(dataCount))
//            for (j in 0 until dataCount)
//                drawData!![i].add(null)
//        }

        val pivot = LocalDate.of(ym.year, ym.monthValue, 1).minusDays(1)
        val offset = LocalDate.of(ym.year, ym.monthValue, 1).dayOfWeek.value % 7
        Log.d(TAG, "refreshData: pivot: ${pivot.toString()} offset: $offset")
        data.forEach{
            Log.e(TAG, "refreshData: $it", )
        }
        for (schedule in data) {
            // 날짜 계산
            var s = ChronoUnit.DAYS.between(pivot, schedule.startTime.toLocalDate()).toInt()
            var e = ChronoUnit.DAYS.between(pivot, schedule.endTime.toLocalDate()).toInt()
            val lastDate = ym.atEndOfMonth().dayOfMonth

            if (e < 1 || lastDate < s) continue
            if (s < 1) s = 1
            if (e > lastDate) e = lastDate

            // 비어있는 행에 스케쥴 배치 // 실패할 경우 출력하지 않음
//            for (i in 0 until dataCount) {
//                if (drawData!![s + offset][i] == null) {
//                    for (j in s..e)
//                        drawData!![j + offset][i] = schedule
//                    break
//                }
//            }
        }

        // 바뀐 데이터로 출력
        invalidate()
    }
}