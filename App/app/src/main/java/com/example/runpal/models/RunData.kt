package com.example.runpal.models

/**
 * This class is used when sending and receiving updates,
 * in which case the path consists of newly added path points,
 * and the location can be the current user location,
 * but also it can represent the entire run,
 * in which case the path will be the entire run path from the beginning,
 * while the location is usually irrelevant.
 */
class RunData(
    var run: Run = Run(),
    var location: PathPoint? = null,
    var path: List<PathPoint> = listOf()
) {
}