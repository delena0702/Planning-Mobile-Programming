package com.examples.teamproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.examples.teamproject.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    var DBHelper: ScheduleDBHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        DBHelper = ScheduleDBHelper(this)
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