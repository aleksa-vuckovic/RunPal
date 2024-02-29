package com.example.racepal.models

import androidx.room.Entity

/**
 * This model represents a running session, and is used as-is
 * in the room database, as well as for server communication.
 *
 * @property id An identifier which must uniquely
 * identify a run for this user (probably a timestamp).
 * @property user The email of the user.
 * @property event The event identifier, if this run
 * is part of an event, else null.
 * @property room The room id, if this run is part
 * of a group running session, else null.
 * @property start The start timestamp, null if run did not start.
 * @property running Total running milliseconds.
 * @property end The end timestamp, null if run did not end (properly).
 */
@Entity(tableName = "run", primaryKeys = ["id", "user"])
class Run(
    val id: Long = 0,
    val user: String = "",
    val event: String? = null,
    val room: String? = null,
    val start: Long? = null,
    val running: Long? = null,
    val end: Long? = null
) {
}