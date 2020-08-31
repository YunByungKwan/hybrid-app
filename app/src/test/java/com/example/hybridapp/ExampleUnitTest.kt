package com.example.hybridapp

import app.dvkyun.flexhybridand.FlexData
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        val b = FlexData(100)
        val a: Int = b.to()
        val c = b.asInt()
    }
}
