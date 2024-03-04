package com.example.racepal.models

/**
 * This is a helper class which includes all
 * fields from run, and additionally distance and kcal.
 */
class RunInfo(
    val id: Long = 0,
    val user: String = "",
    val event: String? = null,
    val room: String? = null,
    val start: Long? = null,
    val running: Long? = null,
    val end: Long? = null,
    val distance: Long,
    val kcal: Long
) {
}