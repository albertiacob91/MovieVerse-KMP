package com.albertiacob91.movieversekmp.domain.usecase.movies

import com.albertiacob91.movieversekmp.data.remote.CommentDto
import com.albertiacob91.movieversekmp.domain.repository.MovieRepository

class GetCommentsUseCase(private val repository: MovieRepository) {
    suspend operator fun invoke(movieId: Int): List<CommentDto> = repository.getComments(movieId)
}
