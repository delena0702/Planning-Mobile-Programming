package com.examples.teamproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.res.ResourcesCompat
import com.examples.teamproject.databinding.ActivityScheduleInfoBinding
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ScheduleInfoActivity : AppCompatActivity() {
    private val binding by lazy { ActivityScheduleInfoBinding.inflate(layoutInflater) }
    private lateinit var schedule: Schedule
    private val activityLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.data!!.extras == null) return@registerForActivityResult
            val sch = it.data!!.getSerializableExtra("schedule") as Schedule
            schedule = sch
            initLayout()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        schedule = intent.getSerializableExtra("schedule") as Schedule
        initLayout()
    }

    private fun initLayout() {
        binding.apply {
            textviewTitle.text = schedule.title
            textviewContent.text = schedule.content
            textviewTime.text =
                "${makeTimeString(schedule.startTime)} ${makeTimeString(schedule.endTime)}"
            textviewPlace.text = schedule.place
            textviewOpen.text = if (schedule.open) "공개" else "비공개"

            if (schedule.isHistory) {
                when (schedule.histGrade) {
                    "상" -> imageviewOpen.setImageDrawable(
                        ResourcesCompat.getDrawable(
                            resources,
                            R.drawable.ic_baseline_thumb_up_24,
                            null
                        )
                    )
                    "중" -> imageviewOpen.setImageDrawable(
                        ResourcesCompat.getDrawable(
                            resources,
                            R.drawable.ic_baseline_thumbs_up_down_24,
                            null
                        )
                    )
                    "하" -> imageviewOpen.setImageDrawable(
                        ResourcesCompat.getDrawable(
                            resources,
                            R.drawable.ic_baseline_thumb_down_24,
                            null
                        )
                    )
                }

                textviewMemo.text = schedule.histMemo
            } else {
                rowHistory1.visibility = View.GONE
                rowHistory2.visibility = View.GONE
            }

            buttonEdit.setOnClickListener {
                val intent = Intent(this@ScheduleInfoActivity, EditScheduleInfoActivity::class.java)
                intent.putExtra("schedule", schedule)
                activityLauncher.launch(intent)
            }

            buttonDelete.setOnClickListener {
                val dbData = llooaadd()
                for (sch in dbData) {
                    if (sch.isEqual(schedule)) {
                        dbData.remove(sch)
                        break
                    }
                }
                finish()
            }
        }
    }

    private fun makeTimeString(t: LocalDateTime): String {
        return t.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"))
    }
}