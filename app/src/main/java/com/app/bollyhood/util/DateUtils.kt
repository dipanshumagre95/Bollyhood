package com.app.bollyhood.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DateUtils {

    companion object {

        fun getConvertDateTiemFormat(dateFormat: String): String? {
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val date = simpleDateFormat.parse(dateFormat) ?: return null

            val calendar = Calendar.getInstance()
            val today = calendar.time

            val dateCalendar = Calendar.getInstance()
            dateCalendar.time = date

            return if (calendar.get(Calendar.YEAR) == dateCalendar.get(Calendar.YEAR)) {
                if (calendar.get(Calendar.DAY_OF_YEAR) == dateCalendar.get(Calendar.DAY_OF_YEAR)) {
                    val diffInMillis = today.time - date.time
                    val diffInMinutes = diffInMillis / (1000 * 60)
                    val diffInHours = diffInMinutes / 60

                    if (diffInHours > 0) {
                        "$diffInHours hours ago"
                    } else if (diffInMinutes > 0) {
                        val amPm = if (dateCalendar.get(Calendar.AM_PM) == Calendar.AM) "AM" else "PM"
                        SimpleDateFormat("hh:mm aa", Locale.getDefault()).format(date)
                    } else {
                        "Just now"
                    }
                } else if (calendar.get(Calendar.MONTH) == dateCalendar.get(Calendar.MONTH)) {
                    val diffInMillis = today.time - date.time
                    val diffInDays = diffInMillis / (1000 * 60 * 60 * 24)
                    if (diffInDays > 0) {
                        "$diffInDays Days ago"
                    } else {
                        val diffInMillis = today.time - date.time
                        val diffInMinutes = diffInMillis / (1000 * 60)
                        val diffInHours = diffInMinutes / 60

                        if (diffInHours > 0) {
                            "$diffInHours hours ago"
                        } else if (diffInMinutes > 0) {
                            val amPm = if (dateCalendar.get(Calendar.AM_PM) == Calendar.AM) "AM" else "PM"
                            SimpleDateFormat("hh:mm aa", Locale.getDefault()).format(date)
                        } else {
                            "Just now"
                        }
                    }
                } else {
                    SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(date)
                }
            } else {
                SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(date)
            }
        }


        fun getConvertDateTiemFormatForList(dateFormat: String): String? {
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val date = simpleDateFormat.parse(dateFormat) ?: return null

            val calendar = Calendar.getInstance()
            val today = calendar.time

            val dateCalendar = Calendar.getInstance()
            dateCalendar.time = date

            return if (calendar.get(Calendar.YEAR) == dateCalendar.get(Calendar.YEAR)) {
                if (calendar.get(Calendar.DAY_OF_YEAR) == dateCalendar.get(Calendar.DAY_OF_YEAR)) {
                    val diffInMillis = today.time - date.time
                    val diffInMinutes = diffInMillis / (1000 * 60)
                    val diffInHours = diffInMinutes / 60

                    if (diffInHours > 0) {
                        "$diffInHours hours ago"
                    } else {
                        "$diffInMinutes minutes ago"
                    }
                } else if (calendar.get(Calendar.MONTH) == dateCalendar.get(Calendar.MONTH)) {
                    val diffInMillis = today.time - date.time
                    val diffInDays = diffInMillis / (1000 * 60 * 60 * 24)

                    if (diffInDays > 0) {
                        "$diffInDays days ago"
                    } else {
                        val diffInMinutes = diffInMillis / (1000 * 60)
                        val diffInHours = diffInMinutes / 60

                        if (diffInHours > 0) {
                            "$diffInHours hours ago"
                        } else {
                            "$diffInMinutes minutes ago"
                        }
                    }
                } else {
                    SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(date)
                }
            } else {
                SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(date)
            }
        }



    }
}