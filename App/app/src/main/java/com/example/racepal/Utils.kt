package com.example.racepal

import androidx.compose.ui.graphics.Color




class Utils {

    companion object {
        /**
         * The list should be already sorted in ascending order.
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