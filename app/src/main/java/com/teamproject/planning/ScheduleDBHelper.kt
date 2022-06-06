package com.teamproject.planning

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.time.LocalDateTime

class ScheduleDBHelper(val context: Context) :
    SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    companion object {
        const val DB_NAME = "sch_db.db"
        const val DB_VERSION = 1
        const val TABLE_NAME = "scheduleDB"
        const val TITLE = "title"
        const val CONTENT = "content"
        const val PLACE = "place"
        const val STTIME = "startTime"
        const val EDTIME = "endTime"
        const val OPEN = "open"
        const val ISHIS = "isHistory"
        const val HISGR = "histGrade"
        const val HISMEMO = "histMemo"
        const val COLOR = "color"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val create_table = "create table if not exists $TABLE_NAME(" +
                "$TITLE text, " +
                "$CONTENT text, " +
                "$PLACE text, " +
                "$STTIME text, " +
                "$EDTIME text, " +
                "$OPEN boolean, " +
                "$ISHIS boolean, " +
                "$HISGR text, " +
                "$HISMEMO text, " +
                "$COLOR text);"
        db!!.execSQL(create_table)
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        val drop_table = "drop table if exists $TABLE_NAME;"
        db!!.execSQL(drop_table)
        onCreate(db)
    }

    fun save(arr: ArrayList<Schedule>): Boolean {
        //list를
        delete()    // db에 저장되어 있는 모든 정보를 없에준다.
        val db = writableDatabase
        var flag: Boolean
        val n = arr.size

        for (i in 0 until n) {
            val values = ContentValues()
            values.put(TITLE, arr[i].title)
            values.put(CONTENT, arr[i].content)
            values.put(PLACE, arr[i].place)
            //저장되는 LocalDateTime = 2022-12-12T20:11
            //starttime을 저장


            val starttime = arr[i].startTime.toString()
            val s_year = starttime.substring(0 until 4)
            val s_month = starttime.substring(5 until 7)
            val s_day = starttime.substring(8 until 10)
            val s_hour = starttime.substring(11 until 13)
            val s_min = starttime.substring(14 until 16)
            val f_sttime = s_year + " " + s_month + " " + s_day + " " + s_hour + " " + s_min
            values.put(STTIME, f_sttime)

            //endtime을 저장
            val endtime = arr[i].endTime.toString()
            val e_year = endtime.substring(0 until 4)
            val e_month = endtime.substring(5 until 7)
            val e_day = endtime.substring(8 until 10)
            val e_hour = endtime.substring(11 until 13)
            val e_min = endtime.substring(14 until 16)
            val f_edtime = e_year + " " + e_month + " " + e_day + " " + e_hour + " " + e_min
            values.put(EDTIME, f_edtime)
            values.put(OPEN, arr[i].open)
            values.put(ISHIS, arr[i].isHistory)
            values.put(HISGR, arr[i].histGrade)
            values.put(HISMEMO, arr[i].histMemo)
            values.put(COLOR, arr[i].color)
            flag = db.insert(TABLE_NAME, null, values) > 0
            if (flag)
                continue
            else if (!flag)
                return false
        }
        db.close()
        return true
    }

    private fun delete(): Boolean {
        val strsql = "select * from $TABLE_NAME;"
        val db = writableDatabase
        val cursor = db.rawQuery(strsql, null)
        val flag = cursor.count != 0
        if (flag) {
            cursor.moveToFirst()
            db.delete(TABLE_NAME, null, null)
        }
        cursor.close()
        db.close()
        return flag
    }

    fun load(): ArrayList<Schedule> {
        val sc_list = ArrayList<Schedule>()
        //getAllrecord
        val strsql = "select * from $TABLE_NAME;"
        val db = readableDatabase
        val cursor = db.rawQuery(strsql, null)

        if (cursor.count == 0) return ArrayList()
        //showschedule
        cursor.moveToFirst()
        val attrcount = cursor.columnCount

        do {
            lateinit var tit: String
            lateinit var contt: String
            lateinit var plc: String
            lateinit var stt: LocalDateTime
            lateinit var edt: LocalDateTime
            var is_open = true
            var is_his = true
            lateinit var his_gr: String
            lateinit var h_mem: String
            var sc_color: Int = 0

            for (i in 0 until attrcount) {
                when (i) {
                    0 -> tit = cursor.getString(i)
                    1 -> contt = cursor.getString(i)
                    2 -> plc = cursor.getString(i)
                    3 -> {
                        val time_temp =
                            cursor.getString(i)   //  db에서는 문자열로 정보를 받고 이를 localdatetime으로 변환한다.
                        val time_list = time_temp.split(' ')
                        val date: LocalDateTime = LocalDateTime.of(
                            time_list[0].toInt(), time_list[1].toInt(), time_list[2].toInt(),
                            time_list[3].toInt(), time_list[4].toInt()
                        )
                        stt = date
                    }
                    4 -> {
                        //db에는 시간이 2022 12 12 20 11(2022년 12월 12일 20시 11분)의 형태로 저장됨.
                        val time_temp =
                            cursor.getString(i)   //  db에서는 문자열로 정보를 받고 이를 localdatetime으로 변환한다.
                        val time_list = time_temp.split(' ')
                        val date: LocalDateTime = LocalDateTime.of(
                            time_list[0].toInt(), time_list[1].toInt(), time_list[2].toInt(),
                            time_list[3].toInt(), time_list[4].toInt()
                        )
                        edt = date
                    }
                    5 -> {
                        //boolean 타입을 받을 때 대문자로 받는다고 생각하고 구현하였습니다.
                        is_open = cursor.getString(i).equals("1")
                    }
                    6 -> {
                        //boolean 타입을 받을 때 대문자로 받는다고 생각하고 구현하였습니다.
                        is_his = cursor.getString(i).equals("1")
                    }
                    7 -> {
                        his_gr = cursor.getString(i)
                    }
                    8 -> {
                        h_mem = cursor.getString(i)
                    }
                    9 -> {
                        sc_color = cursor.getString(i).toInt()
                    }
                }
            }
            sc_list.add(Schedule(tit, contt, plc, stt, edt, is_open).apply {
                this.isHistory = is_his
                this.histGrade = his_gr
                this.histMemo = h_mem
                this.color = sc_color
            })
        } while (cursor.moveToNext())

        cursor.close()
        db.close()
        return sc_list
    }
}