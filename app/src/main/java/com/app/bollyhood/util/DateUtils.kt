package com.app.bollyhood.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

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

        fun dateToMilliseconds(dateString: String, format: String): String {
            val sdf = SimpleDateFormat(format, Locale.getDefault())
            sdf.timeZone = TimeZone.getTimeZone("UTC")
            val date = sdf.parse(dateString)
            return date?.time?.toString() ?: "0"
        }

        fun getTodayMilliseconds(): String {
            val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC")) // Use UTC for consistency
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)

            return calendar.timeInMillis.toString() // Convert to string
        }

        fun getMillisecondsFromDate(date: String): String {
            return try {
                val dateFormat = SimpleDateFormat(StaticData.dateFormate, Locale.ENGLISH)
                dateFormat.timeZone = TimeZone.getTimeZone("UTC") // Use UTC for consistency

                val parsedDate = dateFormat.parse(date) ?: return "Invalid Date"

                val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
                    time = parsedDate
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }

                calendar.timeInMillis.toString() // Convert to string
            } catch (e: Exception) {
                "Invalid Date"
            }
        }


        fun formatDate(date: String): String {
            val inputFormat = SimpleDateFormat(StaticData.dateFormate, Locale.ENGLISH)
            val parsedDate = inputFormat.parse(date) ?: return "Invalid Date"

            val calendar = Calendar.getInstance()
            val today = calendar.time

            calendar.add(Calendar.DAY_OF_YEAR, -1)
            val yesterday = calendar.time

            calendar.add(Calendar.DAY_OF_YEAR, 2)
            val tomorrow = calendar.time

            val dateFormat = SimpleDateFormat(StaticData.dateFormate, Locale.ENGLISH)
            val givenDateStr = dateFormat.format(parsedDate)
            val todayStr = dateFormat.format(today)
            val yesterdayStr = dateFormat.format(yesterday)
            val tomorrowStr = dateFormat.format(tomorrow)

            val dayFormat = SimpleDateFormat("d", Locale.ENGLISH)
            val day = dayFormat.format(parsedDate)
            val dayWithSuffix = when {
                day.endsWith("1") && day != "11" -> "${day}st"
                day.endsWith("2") && day != "12" -> "${day}nd"
                day.endsWith("3") && day != "13" -> "${day}rd"
                else -> "${day}th"
            }

            val monthYearFormat = SimpleDateFormat("MMM yyyy", Locale.ENGLISH)
            val monthYear = monthYearFormat.format(parsedDate)

            return when (givenDateStr) {
                todayStr -> "Today - $dayWithSuffix $monthYear"
                yesterdayStr -> "Yesterday - $dayWithSuffix $monthYear"
                tomorrowStr -> "Tomorrow - $dayWithSuffix $monthYear"
                else -> "  $dayWithSuffix $monthYear"
            }
        }


        fun getTodayDate(): String {
            val calendar = Calendar.getInstance()
            val dateFormat = SimpleDateFormat(StaticData.dateFormate, Locale.ENGLISH)
            return dateFormat.format(calendar.time)
        }

        fun getDateFromMilliseconds(milliseconds: String): String {
            return try {
                val millis = milliseconds.toLongOrNull() ?: return "Invalid Date"
                val dateFormat = SimpleDateFormat(StaticData.dateFormate, Locale.ENGLISH).apply {
                    timeZone = TimeZone.getDefault()
                }
                dateFormat.format(Date(millis))
            } catch (e: Exception) {
                "Invalid Date"
            }
        }
    }
}