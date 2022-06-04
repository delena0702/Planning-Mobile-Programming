package com.examples.teamproject.yujin

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.MotionEvent.*
import com.examples.teamproject.Schedule
import com.examples.teamproject.llooaadd
import java.time.LocalDate

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

                var startDateValue = start.dayOfWeek.value  // 일정 시작 요일 값
                if (startDateValue == 7) startDateValue = 0     //일요일은 0으로

                val startHour = start.hour    //시작 hour
                val endHour = end.hour    //끝 hour

                val startHeight = 2 * startHour + start.minute.toFloat() / 30 + 1
                val endHeight = 2 * endHour + end.minute.toFloat() / 30 + 1


                paint.color = Color.parseColor("#ABCDEF")
                paint.style = Paint.Style.FILL
                paint.textSize =  h / 2.5.toFloat()

                //Log.d("drawData[$i]","${drawData!![i]}")
                if (start.toLocalDate() in sunday..saturday && start.toLocalDate()==end.toLocalDate()) {
                    canvas!!.drawRect(
                        startDateValue * w,
                        startHeight * h - 2,
                        (startDateValue + 1) * w,
                        endHeight * h - 2,
                        paint
                    )
                    paint.color = Color.parseColor("#000000")

                    canvas.drawText(
                        drawData!![i].title,
                        (startDateValue) * w + 10,
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
        val data = llooaadd()
        data.sortWith { a, b -> a.startTime.compareTo(b.startTime) }

        drawData = ArrayList(data.size)

        for(i in 0 until 7){
            if(time.dayOfWeek.value == i){
                //이번주차의 시작인 일요일 날짜
                sunday = LocalDate.of(time.year, time.monthValue, time.dayOfMonth).minusDays(i.toLong())

                //이번주차의 끝인 토요일 날짜
                saturday = sunday.plusDays(6)
            }
        }

        for (schedule in data){
            drawData!!.add(schedule)
        }

        // 바뀐 데이터로 출력
        invalidate()
    }

}