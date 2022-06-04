package com.examples.teamproject

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.ChronoUnit

class CalendarSchedulePanel(context: Context, attrs: AttributeSet?) :
    androidx.appcompat.widget.AppCompatTextView(context, attrs) {
    private val dataCount = 3
    private val mainOffset = PlanningCalendarView.HEADER_HEIGHT

    private var drawData: ArrayList<ArrayList<Schedule?>>? = null
    var ym: YearMonth = YearMonth.now()
    var weekCount = 0
    private val paint = Paint()

    override fun onDraw(canvas: Canvas?) {
        if (drawData == null) {
            super.onDraw(canvas)
            return
        }

        val w = width.toFloat()
        val h = (height - mainOffset).toFloat()
        canvas!!.translate(0F, mainOffset.toFloat())
        paint.textSize = 0.14F * h / weekCount

        for (pos in 1..weekCount * 7) {
            for (row in 0 until dataCount) {
                if (drawData!![pos][row] != null) {
                    paint.color = Color.parseColor("#ABCDEF")
                    canvas.drawRect(
                        ((pos - 1) % 7).toFloat() * w / 7,
                        (((pos - 1) / 7).toFloat() + 0.4F + row * 0.2F) * h / weekCount,
                        ((pos - 1) % 7 + 1).toFloat() * w / 7,
                        (((pos - 1) / 7).toFloat() + 0.55F + row * 0.2F) * h / weekCount,
                        paint
                    )

                    if ((drawData!![pos][row] != drawData!![pos - 1][row]) || (pos % 7 == 1)) {
                            paint.color = Color.parseColor("#000000")

                            canvas.drawText(
                                drawData!![pos][row]!!.title,
                                ((pos - 1) % 7).toFloat() * w / 7,
                                (((pos - 1) / 7).toFloat() + 0.55F + row * 0.2F - 0.02F) * h / weekCount,
                                paint
                            )
                        }
                }
            }
        }
        super.onDraw(canvas)
    }

    fun refreshData() {
        val data = llooaadd()
        data.sortWith { a, b -> a.startTime.compareTo(b.startTime) }

        drawData = ArrayList(weekCount * 7 + 1)
        for (i in 0..weekCount * 7) {
            drawData!!.add(ArrayList(dataCount))
            for (j in 0 until dataCount)
                drawData!![i].add(null)
        }

        val pivot = LocalDate.of(ym.year, ym.monthValue, 1).minusDays(1)
        val offset = LocalDate.of(ym.year, ym.monthValue, 1).dayOfWeek.value % 7

        for (schedule in data) {
            // 날짜 계산
            var s = ChronoUnit.DAYS.between(pivot, schedule.startTime.toLocalDate()).toInt()
            var e = ChronoUnit.DAYS.between(pivot, schedule.endTime.toLocalDate()).toInt()
            val lastDate = ym.atEndOfMonth().dayOfMonth

            if (e < 1 || lastDate < s) continue
            if (s < 1) s = 1
            if (e > lastDate) e = lastDate

            // 비어있는 행에 스케쥴 배치 // 실패할 경우 출력하지 않음
            for (i in 0 until dataCount) {
                if (drawData!![s + offset][i] == null) {
                    for (j in s..e)
                        drawData!![j + offset][i] = schedule
                    break
                }
            }
        }

        // 바뀐 데이터로 출력
        invalidate()
    }
}