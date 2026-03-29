package com.albertiacob91.movieversekmp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform