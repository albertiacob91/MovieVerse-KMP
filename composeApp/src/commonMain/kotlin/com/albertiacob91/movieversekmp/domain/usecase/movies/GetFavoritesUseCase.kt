package com.albertiacob91.movieversekmp.domain.usecase.movies

import com.albertiacob91.movieversekmp.data.remote.FavoriteDto
import com.albertiacob91.movieversekmp.domain.repository.MovieRepository

class GetFavoritesUseCase(private val repository: MovieRepository) {
    suspend operator fun invoke(token: String): List<FavoriteDto> = repository.getFavorites(token)
}
