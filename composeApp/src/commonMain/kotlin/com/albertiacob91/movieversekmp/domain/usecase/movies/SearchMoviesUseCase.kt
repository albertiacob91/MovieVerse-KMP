package com.albertiacob91.movieversekmp.domain.usecase.movies

import com.albertiacob91.movieversekmp.data.remote.MovieDto
import com.albertiacob91.movieversekmp.domain.repository.MovieRepository

class SearchMoviesUseCase(private val repository: MovieRepository) {
    suspend operator fun invoke(query: String, page: Int): List<MovieDto> = repository.searchMovies(query, page)
}
