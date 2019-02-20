package com.example.test2antplus

import java.math.BigDecimal

/**
 * [Program] - data set class for trainings program
 * @param targetPower - target power (BigDecimal) in W;
 * @param duringTime - time of target power working (Int) in minutes.
 */
class Program(
    private var targetPower: BigDecimal,
    private var duringTime: Int
) {
    fun getTargetPower() = this.targetPower

    fun getDuringTime() = this.duringTime
}