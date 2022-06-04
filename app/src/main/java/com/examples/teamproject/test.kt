package com.examples.teamproject

import java.time.LocalDateTime

var ddbb = ArrayList<Schedule>()

fun llooaadd(): ArrayList<Schedule> {
    if (ddbb.size == 0) {
        ddbb.add(
            Schedule(
                "제목1",
                "내용1",
                "장소1",
                LocalDateTime.of(2022, 5, 21, 9, 0),
                LocalDateTime.of(2022, 5, 25, 11, 0),
                false
            ).apply {
                color = 2
            }
        )

        ddbb.add(
            Schedule(
                "제목2",
                "내용2",
                "장소2",
                LocalDateTime.of(2022, 5, 22, 9, 0),
                LocalDateTime.of(2022, 5, 22, 11, 0),
                true
            ).apply {
                color = 3
            }
        )

        ddbb.add(
            Schedule(
                "제목3",
                "내용3",
                "장소3",
                LocalDateTime.of(2022, 4, 22, 9, 0),
                LocalDateTime.of(2022, 5, 22, 11, 0),
                true
            ).apply {
                color = 4
            }
        )

        ddbb.add(
            Schedule(
                "제목4",
                "내용4",
                "장소4",
                LocalDateTime.of(2022, 5, 25, 9, 0),
                LocalDateTime.of(2022, 6, 12, 11, 0),
                true
            ).apply {
                isHistory = true
                histGrade = "상"
                histMemo = "메모4"
            }
        )
    }

    return ddbb
}

fun ssaavvee(arr : ArrayList<Schedule>) {
    ddbb = arr
}