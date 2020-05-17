package utils

import java.text.SimpleDateFormat
import java.util.*

class TimeFormat {
    companion object {
        fun getFormattedTimestamp(timestamp: Long): String? {
            val date = Date(timestamp)
            val format = SimpleDateFormat("yyyy.MM.dd HH:mm:ss.SSS")
            return format.format(date)
        }
    }
}