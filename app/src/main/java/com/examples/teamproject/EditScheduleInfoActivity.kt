package com.examples.teamproject

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.examples.teamproject.databinding.ActivityEditScheduleInfoBinding
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class EditScheduleInfoActivity : AppCompatActivity() {
    private val binding by lazy { ActivityEditScheduleInfoBinding.inflate(layoutInflater) }
    private var originalSchedule: Schedule? = null
    private var startTime = LocalDateTime.now()
    private var endTime = LocalDateTime.now()
    private var grade = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        if (intent.extras != null)
            originalSchedule = intent.getSerializableExtra("schedule") as Schedule

        initLayout()
    }

    private fun initLayout() {
        if (originalSchedule != null) {
            binding.edittextTitle.setText(originalSchedule!!.title)
            binding.edittextContent.setText(originalSchedule!!.content)
            binding.edittextPlace.setText(originalSchedule!!.place)
            binding.edittextMemo.setText(originalSchedule!!.histMemo)

            startTime = originalSchedule!!.startTime
            endTime = originalSchedule!!.endTime
            grade = originalSchedule!!.histGrade
            if (originalSchedule!!.open)
                binding.switchOpen.isChecked = true
            else
                binding.switchOpen.isChecked = true
        }
        refreshViews()

        binding.apply {
            textviewTime1.setOnClickListener {
                val arr = arrayOf(startTime.year, startTime.monthValue, startTime.dayOfMonth)
                DatePickerDialog(
                    this@EditScheduleInfoActivity,
                    { _, y, m, d ->
                        startTime = startTime.withYear(y).withMonth(m+1).withDayOfMonth(d)

                        if (startTime.isAfter(endTime))
                            endTime = startTime

                        refreshViews()
                    },
                    arr[0], arr[1] - 1, arr[2]
                ).show()
            }

            textviewTime2.setOnClickListener {
                val arr = arrayOf(startTime.hour, startTime.minute)
                TimePickerDialog(
                    this@EditScheduleInfoActivity,
                    { _, h, m ->
                        startTime = startTime.withHour(h).withMinute(m)

                        if (startTime.isAfter(endTime))
                            endTime = startTime

                        refreshViews()
                    },
                    arr[0], arr[1], false
                ).show()
            }

            textviewTime3.setOnClickListener {
                val arr = arrayOf(endTime.year, endTime.monthValue, endTime.dayOfMonth)
                DatePickerDialog(
                    this@EditScheduleInfoActivity,
                    { _, y, m, d ->
                        endTime = endTime.withYear(y).withMonth(m+1).withDayOfMonth(d)

                        if (endTime.isBefore(startTime))
                            startTime = endTime

                        refreshViews()
                    },
                    arr[0], arr[1] - 1, arr[2]
                ).show()
            }

            textviewTime4.setOnClickListener {
                val arr = arrayOf(endTime.hour, endTime.minute)
                TimePickerDialog(
                    this@EditScheduleInfoActivity,
                    { _, h, m ->
                        endTime = endTime.withHour(h).withMinute(m)

                        if (endTime.isBefore(startTime))
                            startTime = endTime

                        refreshViews()
                    },
                    arr[0], arr[1], false
                ).show()
            }

            imageviewGrade1.setOnClickListener {
                grade = "상"
                refreshViews()
            }
            imageviewGrade2.setOnClickListener {
                grade = "중"
                refreshViews()
            }
            imageviewGrade3.setOnClickListener {
                grade = "하"
                refreshViews()
            }

            buttonEditCancel.setOnClickListener {
                finish()
            }

            buttonEditSubmit.setOnClickListener {
                if (originalSchedule == null) {
                    val arr = llooaadd()
                    arr.add(makeSchedule())
                }

                else {
                    val arr = llooaadd()
                    val sch = makeSchedule()
                    for (i in 0 until arr.size) {
                        if (arr[i].isEqual(originalSchedule!!)) {
                            arr[i] = sch
                            break
                        }
                    }

                    ssaavvee(arr)
                    val intent = Intent()
                    intent.putExtra("schedule", sch)
                    setResult(RESULT_OK, intent)
                }
                finish()
            }
        }
    }

    private fun refreshViews() {
        binding.apply {
            textviewTime1.text = startTime.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"))
            textviewTime2.text = startTime.format(DateTimeFormatter.ofPattern("HH:mm"))
            textviewTime3.text = endTime.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"))
            textviewTime4.text = endTime.format(DateTimeFormatter.ofPattern("HH:mm"))

            imageviewGrade1.setColorFilter(ContextCompat.getColor(this@EditScheduleInfoActivity, R.color.disabled), android.graphics.PorterDuff.Mode.MULTIPLY)
            imageviewGrade2.setColorFilter(ContextCompat.getColor(this@EditScheduleInfoActivity, R.color.disabled), android.graphics.PorterDuff.Mode.MULTIPLY)
            imageviewGrade3.setColorFilter(ContextCompat.getColor(this@EditScheduleInfoActivity, R.color.disabled), android.graphics.PorterDuff.Mode.MULTIPLY)

            when(grade) {
                "상"-> imageviewGrade1.colorFilter = null
                "중"-> imageviewGrade2.colorFilter = null
                "하"-> imageviewGrade3.colorFilter = null
            }
        }
    }

    private fun makeSchedule(): Schedule {
        val title = binding.edittextTitle.text.toString()
        val content = binding.edittextContent.text.toString()
        val place = binding.edittextPlace.text.toString()
        val open = binding.switchOpen.isChecked
        val schedule = Schedule(title, content, place, startTime, endTime, open).apply {
            isHistory = true
            histGrade = when {
                binding.imageviewGrade1.colorFilter == null -> "상"
                binding.imageviewGrade2.colorFilter == null -> "중"
                binding.imageviewGrade3.colorFilter == null -> "하"
                else -> ""
            }
            histMemo = binding.edittextMemo.text.toString()
        }
        return schedule
    }
}