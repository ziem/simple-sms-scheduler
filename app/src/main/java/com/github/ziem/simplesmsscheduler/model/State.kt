package com.github.ziem.simplesmsscheduler.model

enum class State {
    Scheduled,
    Sent,
    Unknown;

    companion object {
        fun fromInt(int: Int): State {
            return when (int) {
                1 -> Scheduled
                2 -> Sent
                else -> Unknown
            }
        }

        fun toInt(state: State): Int {
            return when (state) {
                Scheduled -> 1
                Sent -> 2
                else -> -1
            }
        }
    }
}