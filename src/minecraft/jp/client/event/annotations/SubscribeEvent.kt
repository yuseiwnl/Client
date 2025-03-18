package jp.client.event.annotations

import jp.client.event.EventPriority

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class SubscribeEvent(val priority: Byte = EventPriority.NORMAL)