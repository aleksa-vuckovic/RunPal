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

        /**
         * The should be already sorted in ascending order.
         */
        fun<T> binarySearch(data: List<T>, value: (T) -> Float, target: Float): T? {
            var i = 0
            var j = data.size
            if (j == 0) return null
            if (j == 1) return data[0]

            while (j - i > 1) {
                val k = (i + j) / 2
                val valueK = value(data[k])

                if (valueK > target) j = k
                else if (valueK < target) i = k
                else return data[k]
            }

            val valueI = value(data[i])
            val valueJ = value(data[j])
            if (target - valueI <  valueJ - target) return data[i]
            else return data[j]
        }

        /**
         * @param speed Running speed in m/s.
         * @param slope Slope as a percentage (postive or negative).
         * @param weight Runner's weight in kg.
         *
         * @return kcal per second.
         */
        fun kcalExpenditure(speed: Double, slope: Double, weight: Double): Double {
            var factor = 0.2 + 0.9*slope
            if (factor < 0.1) factor = 0.1
            val VO2 = speed * factor / 1000
            val PE = VO2 * weight * 5
            return PE
        }
    }

}