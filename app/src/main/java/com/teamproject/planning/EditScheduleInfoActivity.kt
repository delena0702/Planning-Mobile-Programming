package com.teamproject.planning

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.teamproject.planning.databinding.ActivityEditScheduleInfoBinding
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class EditScheduleInfoActivity : AppCompatActivity() {
    private val binding by lazy { ActivityEditScheduleInfoBinding.inflate(layoutInflater) }
    private var DBHelper: ScheduleDBHelper? = null

    private var originalSchedule: Schedule? = null
    private var startTime = LocalDateTime.now()
    private var endTime = LocalDateTime.now()
    private var grade = "중"
    private var color = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        DBHelper = ScheduleDBHelper(this)

        if (intent.extras!!.containsKey("schedule"))
            originalSchedule = intent.getSerializableExtra("schedule") as Schedule
        if (intent.extras!!.containsKey("time")) {
            val time = LocalDate.parse(intent.getStringExtra("time"))
            startTime = time.atTime(0, 0)
            endTime = time.atTime(23, 59)
        }

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
            color = originalSchedule!!.color

            binding.switchOpen.isChecked = originalSchedule!!.open
            binding.switchHistory.isChecked = originalSchedule!!.isHistory
        }
        refreshViews()

        binding.apply {
            textviewTime1.setOnClickListener {
                val arr = arrayOf(startTime.year, startTime.monthValue, startTime.dayOfMonth)
                DatePickerDialog(
                    this@EditScheduleInfoActivity,
                    { _, y, m, d ->
                        startTime = startTime.withYear(y).withMonth(m + 1).withDayOfMonth(d)

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
                        endTime = endTime.withYear(y).withMonth(m + 1).withDayOfMonth(d)

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

            imageviewColor1.setOnClickListener {
                color = 1
                refreshViews()
            }

            imageviewColor2.setOnClickListener {
                color = 2
                refreshViews()
            }

            imageviewColor3.setOnClickListener {
                color = 3
                refreshViews()
            }

            imageviewColor4.setOnClickListener {
                color = 4
                refreshViews()
            }

            imageviewColor5.setOnClickListener {
                color = 5
                refreshViews()
            }

            switchHistory.setOnCheckedChangeListener { _, _ ->
                refreshViews()
            }

            buttonEditCancel.setOnClickListener {
                finish()
            }

            buttonEditSubmit.setOnClickListener {
                if (originalSchedule == null) {
                    val arr = DBHelper!!.load()
                    arr.add(makeSchedule())
                    DBHelper!!.save(arr)
                } else {
                    val arr = DBHelper!!.load()
                    val sch = makeSchedule()
                    for (i in 0 until arr.size) {
                        if (arr[i].isEqual(originalSchedule!!)) {
                            arr[i] = sch
                            break
                        }
                    }
                    DBHelper!!.save(arr)
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

            imageviewGrade1.setColorFilter(
                ContextCompat.getColor(
                    this@EditScheduleInfoActivity,
                    R.color.disabled
                ), android.graphics.PorterDuff.Mode.MULTIPLY
            )
            imageviewGrade2.setColorFilter(
                ContextCompat.getColor(
                    this@EditScheduleInfoActivity,
                    R.color.disabled
                ), android.graphics.PorterDuff.Mode.MULTIPLY
            )
            imageviewGrade3.setColorFilter(
                ContextCompat.getColor(
                    this@EditScheduleInfoActivity,
                    R.color.disabled
                ), android.graphics.PorterDuff.Mode.MULTIPLY
            )

            imageviewColor1.foreground = null
            imageviewColor2.foreground = null
            imageviewColor3.foreground = null
            imageviewColor4.foreground = null
            imageviewColor5.foreground = null

            when (color) {
                1 -> imageviewColor1.foreground =
                    ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_check_24, null)
                2 -> imageviewColor2.foreground =
                    ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_check_24, null)
                3 -> imageviewColor3.foreground =
                    ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_check_24, null)
                4 -> imageviewColor4.foreground =
                    ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_check_24, null)
                5 -> imageviewColor5.foreground =
                    ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_check_24, null)
            }

            if (switchHistory.isChecked) {
                rowHistory1.visibility = View.VISIBLE
                rowHistory2.visibility = View.VISIBLE
                scrollEdit.post {
                    scrollEdit.fullScroll(View.FOCUS_DOWN)
                }
            } else {
                rowHistory1.visibility = View.GONE
                rowHistory2.visibility = View.GONE
            }

            when (grade) {
                "상" -> imageviewGrade1.colorFilter = null
                "중" -> imageviewGrade2.colorFilter = null
                "하" -> imageviewGrade3.colorFilter = null
            }
        }
    }

    private fun makeSchedule(): Schedule {
        val title = binding.edittextTitle.text.toString()
        val content = binding.edittextContent.text.toString()
        val place = binding.edittextPlace.text.toString()
        val open = binding.switchOpen.isChecked

        val schedule = Schedule(title, content, place, startTime, endTime, open).apply {
            color = this@EditScheduleInfoActivity.color
            isHistory = binding.switchHistory.isChecked

            if (isHistory) {
                histGrade = grade
                histMemo = binding.edittextMemo.text.toString()
            }
        }
        return schedule
    }

    override fun onResume() {
        if (DBHelper == null)
            DBHelper = ScheduleDBHelper(this)
        super.onResume()
    }

    override fun onPause() {
        DBHelper?.close()
        DBHelper = null
        super.onPause()
    }
}