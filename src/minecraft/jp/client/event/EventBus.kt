package jp.client.event

import jp.client.event.annotations.SubscribeEvent
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.util.concurrent.CopyOnWriteArrayList

class EventBus {
    private val LISTENERS = hashMapOf<Class<out Event>, MutableList<MethodData>>()

    fun register(`object`: Any) {
        for (method in `object`.javaClass.declaredMethods) {
            if (isMethodBad(method)) continue

            register(method, `object`)
        }
    }

    fun register(`object`: Any, eventClass: Class<out Event>) {
        for (method in `object`.javaClass.declaredMethods) {
            if (isMethodBad(method, eventClass)) continue

            register(method, `object`)
        }
    }

    fun unregister(`object`: Any) {
        for (dataList in LISTENERS.values) {
            dataList.removeIf { data -> data.source == `object` }
        }
        cleanMap(true)
    }

    @Suppress("UNCHECKED_CAST")
    private fun register(method: Method, `object`: Any) {
        val indexClass = method.parameterTypes[0] as Class<out Event>

        val data = MethodData(`object`, method, method.getAnnotation(SubscribeEvent::class.java).priority)

        if (!data.target.isAccessible) {
            data.target.isAccessible = true
        }

        if (LISTENERS.containsKey(indexClass)) {
            if (!LISTENERS[indexClass]!!.contains(data)) {
                LISTENERS[indexClass]!!.add(data)
                sortListValue(indexClass)
            }
        } else {
            LISTENERS[indexClass] = CopyOnWriteArrayList<MethodData>().apply {
                add(data)
            }
        }
    }

    fun cleanMap(onlyEmptyEntries: Boolean) {
        val mapIterator = LISTENERS.entries.iterator()

        while (mapIterator.hasNext()) {
            if (!onlyEmptyEntries || mapIterator.next().value.isEmpty()) {
                mapIterator.remove()
            }
        }
    }

    private fun sortListValue(indexClass: Class<out Event>) {
        val sortedList = CopyOnWriteArrayList<MethodData>()

        for (priority in EventPriority.VALUE_ARRAY) {
            for (data in LISTENERS[indexClass] ?: emptyList()) {
                if (data.byte == priority) {
                    sortedList.add(data)
                }
            }
        }

        LISTENERS[indexClass] = sortedList
    }

    private fun isMethodBad(method: Method): Boolean {
        return method.parameterTypes.size != 1 || !method.isAnnotationPresent(SubscribeEvent::class.java)
    }

    private fun isMethodBad(method: Method, eventClass: Class<out Event>): Boolean {
        return isMethodBad(method) || method.parameterTypes[0] != eventClass
    }

    fun post(event: Event): Event {
        val dataList = LISTENERS[event::class.java]

        dataList?.forEach { data ->
            invoke(data, event)
        }

        return event
    }

    private fun invoke(data: MethodData, argument: Event) {
        try {
            data.target.invoke(data.source, argument)
        } catch (ignored: IllegalAccessException) {
            // Ignored
        } catch (ignored: IllegalArgumentException) {
            // Ignored
        } catch (ignored: InvocationTargetException) {
            // Ignored
        }
    }

    data class MethodData(val source: Any, val target: Method, val byte: Byte)
}