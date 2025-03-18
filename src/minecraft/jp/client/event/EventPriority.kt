package jp.client.event

class EventPriority {
    companion object {
        const val VERY_LOW: Byte = 4
        const val LOW: Byte = 3
        const val NORMAL: Byte = 2
        const val HIGH: Byte = 1
        const val VERY_HIGH: Byte = 0

        val VALUE_ARRAY: ByteArray = byteArrayOf(
            VERY_HIGH,
            HIGH,
            NORMAL,
            LOW,
            VERY_LOW
        )
    }
}

