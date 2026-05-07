package com.albertiacob91.movieversekmp.domain.usecase.movies

import com.albertiacob91.movieversekmp.data.remote.MovieDto
import com.albertiacob91.movieversekmp.domain.repository.MovieRepository

class GetMovieDetailUseCase(private val repository: MovieRepository) {
    suspend operator fun invoke(movieId: Int): MovieDto? = repository.getMovieDetail(movieId)
}
