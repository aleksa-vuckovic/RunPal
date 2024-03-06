package com.example.runpal

/**
 * Converts a measured value to a String pair.
 * The first string is the formatted number,
 * the second string is the formatted unit.
 */
interface Formatter<T> {
    fun format(value: T): Pair<String, String>
}
object TimeFormatter: Formatter<Long> {
    override fun format(value: Long): Pair<String, String> {
        var t = value/1000
        val secs = t%60
        t/=60
        val mins = t%60
        val hours = t/60

        if (hours == 0L) return "%02d:%02d".format(mins, secs) to ""
        else return "%02d:%02d:%02d".format(hours, mins, secs) to ""
    }
}
object MetricDistanceFormatter: Formatter<Double> {
    override fun format(value: Double): Pair<String, String> {
        if (value < 1000) return value.toInt().toString() to "m"
        else return "%.2f".format(value/1000) to "km"
    }
}
object ImperialDistanceFormatter: Formatter<Double> {
    override fun format(value: Double): Pair<String, String> {
        val miles = value/1000 * KM_TO_MILE
        if (miles >= 1) return "%.2f".format(miles) to "mi"
        val feet = (miles* MILE_TO_FT).toInt()
        return "%d".format(feet) to "ft"
    }
}
object MetricSpeedFormatter: Formatter<Double> {
    override fun format(value: Double): Pair<String, String> {
        return "%.1f".format(value) to "m/s"
    }
}
object MetricPaceFormatter: Formatter<Double> {
    override fun format(value: Double): Pair<String, String> {
        if (value < 0.001) return "-" to "min/km"
        val pace = 1.0 / value / 60.0 * 1000.0
        val mins = pace.toInt()
        val secs = ((pace - mins)*60.0).toInt()
        return "%02d:%02d".format(mins, secs) to "min/km"
    }
}
object ImperialSpeedFormatter: Formatter<Double> {
    override fun format(value: Double): Pair<String, String> {
        val res = value / 1000 * 3600 * KM_TO_MILE
        return "%.1f".format(res) to "mph"
    }
}
object ImperialPaceFormatter: Formatter<Double> {
    override fun format(value: Double): Pair<String, String> {
        val res = value / 1000 * 3600 * KM_TO_MILE //mph
        val pace = 1/res * 60
        val mins = pace.toInt()
        val secs = ((pace - mins)*60.0).toInt()
        return "%02d:%02d".format(mins, secs) to "min/mi"
    }
}
object AltitudeFormatter: Formatter<Double> {
    override fun format(value: Double): Pair<String, String> {
        val meters = value.toInt()
        if (meters >= 1000) return "%.1f".format(value/1000) to "km"
        else return meters.toString() to "m"
    }
}
object KcalFormatter: Formatter<Double> {
    override fun format(value: Double): Pair<String, String> {
        return value.toInt().toString() to "kcal"
    }
}
object EmptyFormatter: Formatter<Double> {
    override fun format(value: Double): Pair<String, String> {
        return "" to ""
    }
}

fun Pair<String, String>.join(sep: String = ""): String = first + sep + second