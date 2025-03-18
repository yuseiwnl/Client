package jp.client.util

class Stopwatch {
    var millis: Long = 0

    init {
        reset()
    }

    fun finished(delay: Long, reset: Boolean = false): Boolean {
        if (System.currentTimeMillis() - delay >= millis) {
            if (reset) reset()
            return true
        }

        return false
    }

    fun reset() {
        millis = System.currentTimeMillis()
    }

    fun getElapsedTime(): Long {
        return System.currentTimeMillis() - this.millis
    }
}