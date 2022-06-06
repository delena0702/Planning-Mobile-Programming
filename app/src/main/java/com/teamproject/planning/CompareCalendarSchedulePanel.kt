package com.teamproject.planning

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.ChronoUnit

class CompareCalendarSchedulePanel(context: Context, attrs: AttributeSet?) :
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
                    paint.color = when (drawData!![pos][row]!!.color) {
                        1 -> resources.getColor(R.color.schedule_color1, null)
                        2 -> resources.getColor(R.color.schedule_color2, null)
                        3 -> resources.getColor(R.color.schedule_color3, null)
                        4 -> resources.getColor(R.color.schedule_color4, null)
                        5 -> resources.getColor(R.color.schedule_color5, null)
                        else -> resources.getColor(R.color.schedule_color1, null)
                    }

                    canvas.drawRect(
                        ((pos - 1) % 7).toFloat() * w / 7,
                        (((pos - 1) / 7).toFloat() + 0.4F + row * 0.2F) * h / weekCount,
                        ((pos - 1) % 7 + 1).toFloat() * w / 7,
                        (((pos - 1) / 7).toFloat() + 0.55F + row * 0.2F) * h / weekCount,
                        paint
                    )

                    if ((drawData!![pos][row] != drawData!![pos - 1][row]) || (pos % 7 == 1)) {
                        paint.color = Color.parseColor("#000000")
                        var title = drawData!![pos][row]!!.title

                        if (paint.measureText(title) >= w / 7) {
                            title = "${drawData!![pos][row]!!.title}..."
                            while (paint.measureText(title) >= w / 7)
                                title = "${title.substring(0, title.length - 4)}..."
                        }

                        canvas.drawText(
                            title,
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
        val data = (context as CompareCalendarActivity).schedules
        data.sortWith { a, b ->
            if (a.startTime.compareTo(b.startTime) != 0)
                return@sortWith a.startTime.compareTo(b.startTime)
            return@sortWith b.endTime.compareTo(a.endTime)
        }

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
            var colorCheck = 0
            for (i in 0 until dataCount) {
                if (i == dataCount - 1 && colorCheck == schedule.color)
                    break

                if (drawData!![s + offset][i] == null) {
                    for (j in s..e)
                        drawData!![j + offset][i] = schedule
                    colorCheck = -1
                    break
                } else {
                    colorCheck =
                        if (colorCheck == 0 || colorCheck == drawData!![s + offset][i]!!.color)
                            drawData!![s + offset][i]!!.color
                        else
                            -1
                }
            }
        }

        refreshColor()

        // 바뀐 데이터로 출력
        invalidate()
    }

    private fun refreshColor() {
        val lastDate = ym.atEndOfMonth().dayOfMonth
        val arr = Array(lastDate + 1) { true }
        val offset = LocalDate.of(ym.year, ym.monthValue, 1).dayOfWeek.value % 7

        for (i in 1..lastDate) {
            for (j in 0 until dataCount) {
                if (drawData!![i + offset][j] != null) {
                    arr[i] = false
                    break
                }
            }
        }

        (context as CompareCalendarActivity).binding.planningCompareCalendar.refreshColor(arr)
    }
}