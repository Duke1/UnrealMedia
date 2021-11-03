package com.qfleng.um

import org.junit.Test

import org.junit.Assert.*
import kotlin.reflect.KProperty

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
class ExampleUnitTest {
    @Test
    @Throws(Exception::class)
    fun addition_isCorrect() {
        assertEquals(4, (2 + 2).toLong())
    }

    class DDD {
        operator fun getValue(thisRef: Any?, property: KProperty<*>): String {
            return "$thisRef, thank you for delegating '${property.name}' to me!"
        }

        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: String) {
            println("$value has been assigned to '${property.name}' in $thisRef.")
        }
    }

    class Example {
        var p: String by DDD()
    }


    @Test
    fun testKotlin() {
        var aa: String? = "";

        aa = null

        aa.let {
            println("$aa not null")
        }
        println(aa?.length)
    }


    @Test
    fun testKotlin2() {
        val result = 89889864.toDouble() / 100
        println(result)
    }
}