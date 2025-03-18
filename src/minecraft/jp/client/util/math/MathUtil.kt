package jp.client.util.math

import kotlin.math.max
import kotlin.math.min

object MathUtil {
    /**
     * Clamps a number, n, to be within a specified range
     *
     * @param min The minimum permitted value of the input
     * @param max The maximum permitted value of the input
     * @param n   The input number to clamp
     * @return The input, bounded by the specified minimum and maximum values
     */
    fun clamp(min: Int, max: Int, n: Int): Int {
        return max(min, min(max, n))
    }

    fun clamp(min: Float, max: Float, n: Float): Float {
        return max(min, min(max, n))
    }
}