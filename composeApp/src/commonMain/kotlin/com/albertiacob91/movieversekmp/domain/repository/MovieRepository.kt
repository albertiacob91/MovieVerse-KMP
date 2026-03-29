package com.albertiacob91.movieversekmp.domain.repository

import com.albertiacob91.movieversekmp.domain.model.Movie

interface MovieRepository {
    suspend fun getPopularMovies(): List<Movie>
}