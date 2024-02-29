package com.example.racepal.repositories

import com.example.racepal.models.User

/**
 * All methods may throw a GenericExcpetion.
 */
interface UserRepository {

    /**
     * Updates the user with the new data.
     * Fields that are set to null are unchanged.
     */
    fun update(user: User)

    /**
     * Retrieves user with given email.
     * (The password is always set to null)
     */
    fun getUser(email:String): User
}