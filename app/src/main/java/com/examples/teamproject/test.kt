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
                LocalDateTime.of(2022, 6, 1, 9, 20),
                LocalDateTime.of(2022, 6, 1, 11, 5),
                false
            )
        )

        ddbb.add(
            Schedule(
                "제목2",
                "내용2",
                "장소2",
                LocalDateTime.of(2022, 6, 2, 12, 0),
                LocalDateTime.of(2022, 6, 2, 15, 30),
                true
            )
        )

        ddbb.add(
            Schedule(
                "제목3",
                "내용3",
                "장소3",
                LocalDateTime.of(2022, 5, 29, 9, 0),
                LocalDateTime.of(2022, 5, 29, 11, 0),
                true
            )
        )

        ddbb.add(
            Schedule(
                "제목4",
                "내용4",
                "장소4",
                LocalDateTime.of(2022, 6, 1, 15, 30),
                LocalDateTime.of(2022, 6, 1, 16, 10),
                true
            )
        )


        ddbb[3].isHistory = true
        ddbb[3].histGrade = "상"
        ddbb[3].histMemo = "메모4"
    }

    return ddbb
}

fun ssaavvee(arr : ArrayList<Schedule>) {
    ddbb = arr
}