package com.albertiacob91.movieversekmp.domain.usecase.movies

import com.albertiacob91.movieversekmp.domain.repository.MovieRepository

class CheckIsFavoriteUseCase(private val repository: MovieRepository) {
    suspend operator fun invoke(token: String, movieId: Int): Boolean = repository.isFavorite(token, movieId)
}
