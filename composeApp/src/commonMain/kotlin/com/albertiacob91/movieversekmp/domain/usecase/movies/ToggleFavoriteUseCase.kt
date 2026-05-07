package com.albertiacob91.movieversekmp.domain.usecase.movies

import com.albertiacob91.movieversekmp.domain.repository.MovieRepository

class ToggleFavoriteUseCase(private val repository: MovieRepository) {
    suspend operator fun invoke(token: String, movieId: Int, currentlyFavorite: Boolean): Boolean {
        return if (currentlyFavorite) {
            repository.removeFavorite(token, movieId)
        } else {
            repository.addFavorite(token, movieId)
        }
    }
}
