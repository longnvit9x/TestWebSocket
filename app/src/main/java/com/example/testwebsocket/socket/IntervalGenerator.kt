package com.example.testwebsocket.socket

import org.parceler.Parcel
import java.util.*

@Parcel(Parcel.Serialization.BEAN)
class IntervalGenerator {

    private val maxInterval: Long
    var attempts: Int = 1

    constructor(): this(60000)

    constructor(maxInterval: Long){
        this.maxInterval = maxInterval
    }

    fun next(isRandom: Boolean = true) : Long {
        val ret = generateInterval(attempts, isRandom)
        if(ret <= maxInterval){
            ++attempts
        }
        return ret
    }

    fun reset() {
        this.attempts = 1
    }

    private fun generateInterval(k: Int, isRandom: Boolean): Long {
        if (!isRandom) {
            return maxInterval
        }

        val mean = pow(2, k) * 1000

        val ratio = mean / 3.0
        val gaussian = Random().nextGaussian()
        val revise = gaussian * ratio

        var ret = Math.min(maxInterval, (mean + revise).toLong())
        if (ret < 0) ret = 0
        return ret
    }

    private fun pow(a: Int, b: Int): Int {
        var ret = a
        var k = b
        while(k-- > 1){
            ret *= a
        }
        return ret
    }
}