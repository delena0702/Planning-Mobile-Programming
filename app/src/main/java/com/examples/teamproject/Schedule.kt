package com.examples.teamproject

import java.io.Serializable
import java.time.LocalDate
import java.time.LocalDateTime

data class Schedule(
    var title: String,              // 제목
    var content: String,            // 내용
    var place: String,              // 장소
    var startTime: LocalDateTime,   // 시작 시간
    var endTime: LocalDateTime,     // 끝나는 시간
    var open: Boolean,               // 공개 여부 // 비교 기능에서 공개할 것인가?
) : Serializable {
    var color = 1 // 색상 (1 ~ 5)
    var isHistory = false // 히스토리인가?
    var histGrade = "" // 상, 중, 하
    var histMemo = "" // 히스토리 메모

    fun example() {
        this.title = "팀플 모임"
        this.content = "UI 설계"
        this.place = "건국대학교"
        this.open = false   // 다른 사람에게 비공개
        this.startTime = LocalDateTime.of(2022, 5, 17, 13, 30)
        this.endTime = LocalDateTime.of(2022, 5, 17, 15, 0)
    }

    fun isEqual(schedule: Schedule): Boolean {
        if (this.title != schedule.title) return false
        if (this.content != schedule.content) return false
        if (this.place != schedule.place) return false
        if (!this.startTime.isEqual(schedule.startTime)) return false
        if (!this.endTime.isEqual(schedule.endTime)) return false
        if (this.open != schedule.open) return false

        if (this.isHistory != schedule.isHistory) return false
        if (this.isHistory) {
            if (this.histGrade != schedule.histGrade) return false
            if (this.histMemo != schedule.histMemo) return false
        }
        return true
    }
}