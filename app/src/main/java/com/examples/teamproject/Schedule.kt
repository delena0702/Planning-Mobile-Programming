package com.examples.teamproject

import org.json.JSONArray
import org.json.JSONObject
import java.io.Serializable
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class Schedule(
    var title: String,              // 제목
    var content: String,            // 내용
    var place: String,              // 장소
    var startTime: LocalDateTime,   // 시작 시간
    var endTime: LocalDateTime,     // 끝나는 시간
    var open: Boolean               // 공개 여부 // 비교 기능에서 공개할 것인가?
) : Serializable {
    var color = 1 // 색상 (1 ~ 5)
    var isHistory = false // 히스토리인가?
    var histGrade = "중" // 상, 중, 하
    var histMemo = "" // 히스토리 메모

    companion object {
        fun parseJSON(str: String): ArrayList<Schedule> {
            val arr = JSONArray(str)
            val resultArray = ArrayList<Schedule>()

            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                val title = obj.getString("title")
                val content = obj.getString("content")
                val place = obj.getString("place")
                val startTime = LocalDateTime.parse(
                    obj.getString("startTime"),
                    DateTimeFormatter.ofPattern("yyyy.MM.dd.HH.mm")
                )
                val endTime = LocalDateTime.parse(
                    obj.getString("endTime"),
                    DateTimeFormatter.ofPattern("yyyy.MM.dd.HH.mm")
                )
                val open = obj.getBoolean("open")
                val color = obj.getInt("color")
                val isHistory = obj.getBoolean("isHistory")
                val histGrade = obj.getString("histGrade")
                val histMemo = obj.getString("histMemo")
                val sch = Schedule(title, content, place, startTime, endTime, open).apply {
                    this.color = color
                    this.isHistory = isHistory
                    this.histGrade = histGrade
                    this.histMemo = histMemo
                }
                resultArray.add(sch)
            }

            return resultArray
        }

        fun toJSONString(arr: ArrayList<Schedule>, mode:Boolean = false): String {
            val resultArray = JSONArray()

            for (schedule in arr) {
                val obj = JSONObject()

                if (schedule.open || mode) {
                    // 공개
                    obj.put("title", schedule.title)
                    obj.put("content", schedule.content)
                    obj.put("place", schedule.place)
                    obj.put(
                        "startTime",
                        schedule.startTime.format(DateTimeFormatter.ofPattern("yyyy.MM.dd.HH.mm"))
                    )
                    obj.put(
                        "endTime",
                        schedule.endTime.format(DateTimeFormatter.ofPattern("yyyy.MM.dd.HH.mm"))
                    )
                    obj.put("open", schedule.open)
                    obj.put("color", schedule.color)
                    obj.put("isHistory", schedule.isHistory)
                    obj.put("histGrade", schedule.histGrade)
                    obj.put("histMemo", schedule.histMemo)
                }

                else {
                    // 비공개
                    obj.put("title", "비공개")
                    obj.put("content", "비공개")
                    obj.put("place", "비공개")
                    obj.put(
                        "startTime",
                        schedule.startTime.format(DateTimeFormatter.ofPattern("yyyy.MM.dd.HH.mm"))
                    )
                    obj.put(
                        "endTime",
                        schedule.endTime.format(DateTimeFormatter.ofPattern("yyyy.MM.dd.HH.mm"))
                    )
                    obj.put("open", schedule.open)
                    obj.put("color", 1)
                    obj.put("isHistory", false)
                    obj.put("histGrade", "중")
                    obj.put("histMemo", "")
                }

                resultArray.put(obj)
            }

            return resultArray.toString()
        }
    }

    fun isEqual(schedule: Schedule): Boolean {
        if (this.title != schedule.title) return false
        if (this.content != schedule.content) return false
        if (this.place != schedule.place) return false
        if (!this.startTime.isEqual(schedule.startTime)) return false
        if (!this.endTime.isEqual(schedule.endTime)) return false
        if (this.color != schedule.color) return false
        if (this.open != schedule.open) return false

        if (this.isHistory != schedule.isHistory) return false
        if (this.isHistory) {
            if (this.histGrade != schedule.histGrade) return false
            if (this.histMemo != schedule.histMemo) return false
        }
        return true
    }
}