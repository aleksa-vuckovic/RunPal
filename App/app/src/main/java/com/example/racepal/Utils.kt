package com.example.racepal

class Utils {

    companion object {
        fun formatDistanceMetric(distance: Double): String {
            if (distance < 1000) return "${distance.toInt()}|m"
            else return "%.2f|km".format(distance/1000)
        }
        fun formatDistanceImperial(distance: Double): String {
            val miles = distance/1000 * 0.621371
            if (miles >= 1) return "%.2f|mi".format(miles)
            val feet = (miles*5280).toInt()
            return "%d|ft".format(feet)
        }
        fun formatSpeed(speed: Double): String {
            return "%.1f|m/s".format(speed)
        }
        fun formatPace(speed: Double): String {
            if (speed < 0.001) return "-|min/km"
            val pace = 1.0/speed/60.0*1000.0
            return "%.2f|min/km".format(pace)
        }
        fun formatTime(time: Long): String {
            var t = time/1000
            val secs = t%60
            t/=60
            val mins = t%60
            val hours = t/60

            if (hours == 0L) return "%02d:%02d".format(mins, secs)
            else return "%02d:%02d:%02d".format(hours, mins, secs)
        }
    }

}