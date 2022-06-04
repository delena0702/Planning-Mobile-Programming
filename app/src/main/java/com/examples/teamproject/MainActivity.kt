package com.examples.teamproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import com.examples.teamproject.databinding.ActivityMainBinding
import com.examples.teamproject.yujin.WeeklyScheduleFragment

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
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
                transaction.replace(R.id.fragmentContainerView, CalendarFragment())
                transaction.commit()
            }

            buttonWeekCalendar.setOnClickListener {
                val transaction = supportFragmentManager.beginTransaction()
                transaction.replace(R.id.fragmentContainerView, WeeklyScheduleFragment())
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