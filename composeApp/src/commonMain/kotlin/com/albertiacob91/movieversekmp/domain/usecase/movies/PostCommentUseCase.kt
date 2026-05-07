package com.albertiacob91.movieversekmp.domain.usecase.movies

import com.albertiacob91.movieversekmp.domain.repository.MovieRepository

class PostCommentUseCase(private val repository: MovieRepository) {
    suspend operator fun invoke(token: String, movieId: Int, content: String): Boolean =
        repository.addComment(token, movieId, content)
}
