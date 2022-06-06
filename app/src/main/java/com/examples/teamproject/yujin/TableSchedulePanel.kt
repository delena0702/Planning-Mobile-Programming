package com.examples.teamproject.yujin

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.MotionEvent.*
import androidx.core.content.res.ResourcesCompat
import com.examples.teamproject.MainActivity
import com.examples.teamproject.R
import com.examples.teamproject.Schedule
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class TableSchedulePanel (context: Context, attrs: AttributeSet?) :
    androidx.appcompat.widget.AppCompatTextView(context, attrs) {
    private val dataCount = 49
    private var drawData: ArrayList<Schedule>? = null
    private val paint = Paint()
    var sunday= LocalDate.now()!!
    var saturday= LocalDate.now()!!
    var point: PointF? = null   //touch 시 point
    var w = 0.0f
    var h = 0.0f
    var pointDate : LocalDate? = null
    var touchEventListener: OnTouchEventListener? = null

    interface OnTouchEventListener {
        fun onTouch(date:LocalDate)
    }

    override fun onDraw(canvas: Canvas?) {
        if (drawData == null) {
            super.onDraw(canvas)
            return
        }

        w = width.toFloat()/7   //timetable 한 칸 가로
        h = height.toFloat()/dataCount  //timetable 한 칸 세로

        if(drawData?.size != 0) {
            for (i in 0 until drawData!!.size) {
                var start = drawData!![i].startTime //drawData에 저장된 i번째 일정 시작 정보
                var end = drawData!![i].endTime //drawData에 저장된 i번째 일정 끝 정보

                if (start < sunday.atStartOfDay()) start = sunday.atStartOfDay()
                if (end > saturday.plusDays(1L).atStartOfDay()) end = saturday.plusDays(1L).atStartOfDay()

                if (end.toLocalDate() < sunday || saturday < start.toLocalDate()) continue

                val startDateValue = start.dayOfWeek.value % 7  // 일정 시작 요일 값
                val endDateValue = end.dayOfWeek.value % 7

                for (dateValue in startDateValue..endDateValue) {
                    val d = sunday.plusDays(dateValue.toLong())
                    val startHour = start.hour    //시작 hour
                    val endHour = end.hour    //끝 hour

                    var startHeight = 2 * startHour + start.minute.toFloat() / 30 + 1
                    var endHeight = 2 * endHour + end.minute.toFloat() / 30 + 1

                    if (d != start.toLocalDate()) startHeight = 1F
                    if (d != end.toLocalDate()) endHeight = 2 * 24 + 1F

                    paint.color = when (drawData!![i].color) {
                        1 -> ResourcesCompat.getColor(resources, R.color.schedule_color1, null)
                        2 -> ResourcesCompat.getColor(resources, R.color.schedule_color2, null)
                        3 -> ResourcesCompat.getColor(resources, R.color.schedule_color3, null)
                        4 -> ResourcesCompat.getColor(resources, R.color.schedule_color4, null)
                        5 -> ResourcesCompat.getColor(resources, R.color.schedule_color5, null)
                        else -> ResourcesCompat.getColor(resources, R.color.schedule_color1, null)
                    }
                    paint.style = Paint.Style.FILL
                    paint.textSize = h / 3.toFloat()

                    //Log.d("drawData[$i]","${drawData!![i]}")
                    canvas!!.drawRect(
                        dateValue * w,
                        startHeight * h - 2,
                        (dateValue + 1) * w - 1,
                        endHeight * h - 2,
                        paint
                    )
                    paint.color = Color.parseColor("#000000")

                    var title = drawData!![i].title

                    if (paint.measureText(title) >= w) {
                        title = "${drawData!![i].title}..."
                        while (paint.measureText(title) >= w - 10)
                            title = "${title.substring(0, title.length - 4)}..."
                    }

                    canvas.drawText(
                        title,
                        (dateValue) * w + 10,
                        startHeight * h + 45,
                        paint
                    )
                }
            }
        }
        super.onDraw(canvas)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {

        if(event!!.action == ACTION_UP){
            point = PointF(event.x, event.y)
            //point 날짜 구하기
            for(i in 0..6){
                if(point!!.x >= w * i && point!!.x <= w * (i+1) ){
                    pointDate = sunday.plusDays(i.toLong())
                }
            }
        }else{
            return true
        }

        touchEventListener?.onTouch(pointDate!!)
        return true

    }

    fun refreshData(time: LocalDate) {
        val data = (context as MainActivity).DBHelper!!.load()
        data.sortWith { a, b -> a.startTime.compareTo(b.startTime) }

        drawData = ArrayList(data.size)

        for(i in 1 until 8){
            if(time.dayOfWeek.value == i){
                if(i != 7)
                    sunday = time.minusDays(i.toLong())     //이번주차의 시작인 일요일 날짜
                else sunday = time

                //이번주차의 끝인 토요일 날짜
                saturday = sunday.plusDays(6)
            }
        }

        for (schedule in data)
            drawData!!.add(schedule)

        drawData!!.sortWith { a, b->
            if (a.startTime.compareTo(b.startTime) != 0)
                return@sortWith a.startTime.compareTo(b.startTime)
            return@sortWith b.endTime.compareTo(a.endTime)
        }

        // 바뀐 데이터로 출력
        invalidate()
    }

}