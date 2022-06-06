package com.teamproject.planning

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.teamproject.planning.databinding.ActivityCompareShareBinding
import kotlinx.coroutines.*
import org.jsoup.Jsoup
import org.jsoup.parser.Parser

class CompareShareActivity : AppCompatActivity() {
    val binding by lazy { ActivityCompareShareBinding.inflate(layoutInflater) }
    val scope = CoroutineScope(Dispatchers.IO)
    private var DBHelper: ScheduleDBHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        DBHelper = ScheduleDBHelper(this)

        initLayout()
    }

    private fun initLayout() {
        binding.layoutOutput.visibility = View.GONE

        scope.launch {
            val doc = Jsoup.connect("https://ku-planning-2022.herokuapp.com/request")
                .data("data", Schedule.toJSONString(DBHelper!!.load()))
                .parser(Parser.xmlParser()).post()
            withContext(Dispatchers.Main) {
                binding.progressBar.visibility = View.GONE
                binding.layoutOutput.visibility = View.VISIBLE
                binding.textviewShareCode.text = doc.select("code").text()
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