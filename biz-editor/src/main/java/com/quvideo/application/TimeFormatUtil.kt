package com.quvideo.application

object TimeFormatUtil {

  fun formatTime(time: Long): String? {
    val timeSec = time / 1000
    val timeStr = StringBuilder()
    val hour: Long
    var minute: Long
    val second: Long
    if (timeSec < 0) {
      return "00:00"
    } else {
      minute = timeSec / 60
      if (minute < 60) {
        second = timeSec % 60
        timeStr.append(unitFormat(minute))
            .append(":")
            .append(unitFormat(second))
      } else {
        hour = minute / 60
        if (hour > 99) {
          return "99:59:59"
        }
        minute %= 60
        second = time - hour * 3600 - minute * 60
        timeStr.append(unitFormat(hour))
            .append(":")
            .append(unitFormat(minute))
            .append(":")
            .append(unitFormat(second))
      }
    }
    return timeStr.toString()
  }

  private fun unitFormat(i: Long): String? {
    val retStr = java.lang.StringBuilder()
    if (i in 0..9) {
      retStr.append("0")
          .append(i)
    } else {
      retStr.append(i)
    }
    return retStr.toString()
  }
}