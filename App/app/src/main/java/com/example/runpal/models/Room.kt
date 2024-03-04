package com.example.runpal.models


/**
 * Represents a group run room.
 *
 * @param _id The room _id.
 * @param members Emails of member users.
 * @param ready Users who are ready to start.
 * (Must only include values from the members set)
 */
data class Room(
    val _id: String = "",
    val members: Set<String> = setOf(),
    val ready: Set<String> = setOf()
) {
}