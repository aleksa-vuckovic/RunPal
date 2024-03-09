package com.example.runpal

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

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
object LongTimeFormatter: Formatter<Long> {
    override fun format(value: Long): Pair<String, String> {
        var t = value/1000
        val secs = t%60
        t/=60
        val mins = t%60
        t/=60
        val hours = t%24
        val days = t/24

        if (days > 1L) return "%d days".format(days)  to ""
        else if (days == 1L) return "1 day %d hours".format(hours) to ""
        else if (hours > 1L) return "%d hours %d minutes".format(hours, mins) to ""
        else if (hours == 1L) return "1 hour %d minutes".format(mins) to ""
        else return "%d minutes".format(mins) to ""
    }
}
object UTCDateTimeFormatter: Formatter<Long> {
    override fun format(value: Long): Pair<String, String> {
        val date = Instant.ofEpochMilli(value)
        val formatted = ZonedDateTime.ofInstant(date, ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
        return (formatted + " UTC") to ""
    }
}
object LocalDateTimeFormatter: Formatter<Long> {
    override fun format(value: Long): Pair<String, String> {
        val date = Instant.ofEpochMilli(value)
        return LocalDateTime.ofInstant(date, ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) to ""
    }
}
object DateOnlyFormatter: Formatter<Long> {
    override fun format(value: Long): Pair<String, String> {
        val date = Instant.ofEpochMilli(value)
        return LocalDateTime.ofInstant(date, ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) to ""
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
        if (miles >= 0.5) return "%.2f".format(miles) to "mi"
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
object MetricWeightFormatter: Formatter<Double> {
    override fun format(value: Double): Pair<String, String> {
        return value.toInt().toString() to "kg"
    }
}
object ImperialWeightFormatter: Formatter<Double> {
    override fun format(value: Double): Pair<String, String> {
        return value.toInt().toString() to "lb"
    }
}
fun Pair<String, String>.join(sep: String = ""): String = first + sep + second

enum class Units(
    val speedFormatter: Formatter<Double>,
    val paceFormatter: Formatter<Double>,
    val distanceFormatter: Formatter<Double>,
    val weightFormatter: Formatter<Double>
) {
    METRIC(MetricSpeedFormatter,
        MetricPaceFormatter,
        MetricDistanceFormatter,
        MetricWeightFormatter
    ), IMPERIAL(ImperialSpeedFormatter,
        ImperialPaceFormatter,
        ImperialDistanceFormatter,
        ImperialWeightFormatter
    ) {
        override fun toStandardWeightInput(value: Double) = value / LB_TO_KG
        override fun fromStandardWeightInput(value: Double) = value * LB_TO_KG
        override fun fromStandardDistanceInput(value: Double) = value / KM_TO_MILE * KM_TO_M
        override val standardWeightInput = "lb"
        override val standardDistanceInput = "mi"
      };

    val next: Units
        get() = if (this == METRIC) IMPERIAL else METRIC

    open fun toStandardWeightInput(value: Double) = value
    open fun fromStandardWeightInput(value: Double) = value
    open val standardWeightInput = "kg"

    open fun fromStandardDistanceInput(value: Double) = value* KM_TO_M
    open val standardDistanceInput = "km"
}