package com.teamproject.planning

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.teamproject.planning.databinding.ActivityMainBinding
import com.teamproject.planning.yujin.WeeklyScheduleFragment
import java.time.LocalDate

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    var time = LocalDate.now()
    var DBHelper: ScheduleDBHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
//        supportActionBar!!.elevation = 0F
//        supportActionBar!!.title = (Html.fromHtml("<font color='#000000'>${supportActionBar!!.title}</font>"))

        DBHelper = ScheduleDBHelper(this)

        initLayout()
    }

    private fun initLayout() {
        binding.apply {
            buttonMonthCalendar.setOnClickListener {
                val transaction = supportFragmentManager.beginTransaction()
                val fragment = CalendarFragment()
                transaction.replace(R.id.fragmentContainerView, fragment)
                transaction.commit()
            }

            buttonWeekCalendar.setOnClickListener {
                val transaction = supportFragmentManager.beginTransaction()
                val fragment = WeeklyScheduleFragment()
                transaction.replace(R.id.fragmentContainerView, fragment)
                transaction.commit()
            }

            buttonCompareCalendar.setOnClickListener {
                val transaction = supportFragmentManager.beginTransaction()
                transaction.replace(R.id.fragmentContainerView, CompareFragment())
                transaction.commit()
            }
        }
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