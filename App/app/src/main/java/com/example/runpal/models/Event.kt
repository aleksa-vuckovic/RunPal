package com.example.runpal.models

import android.net.Uri

/**
 * Represents a running event.
 *
 * @param _id The event identifier.
 * @param time The UNIX timestamp of event start.
 * @param followers Number of followers.
 * @param following True if the logged in user is following the event.
 */
data class Event(
    val _id: String = "",
    val name: String = "",
    val description: String = "",
    var image: String = "",
    val time: Long = 0L,
    val followers: Long = 0L,
    val following: Boolean = false
) {

    enum class Status {
        UPCOMING, CURRENT, PAST
    }

    var imageUri: Uri
        get() = Uri.parse(image)
        set(value) {image = value.toString()}

    val status: Status
        get() {
            val now = System.currentTimeMillis()
            if (now < time - 60*60*1000) return Status.UPCOMING
            else if (now < time + 60*60*1000) return Status.CURRENT
            else return Status.PAST
        }
}