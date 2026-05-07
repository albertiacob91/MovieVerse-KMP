package com.albertiacob91.movieversekmp.domain.repository

import com.albertiacob91.movieversekmp.data.remote.CommentDto
import com.albertiacob91.movieversekmp.data.remote.FavoriteDto
import com.albertiacob91.movieversekmp.data.remote.MovieDto

interface MovieRepository {
    suspend fun getPopularMovies(page: Int): List<MovieDto>
    suspend fun searchMovies(query: String, page: Int): List<MovieDto>
    suspend fun getMovieDetail(movieId: Int): MovieDto?
    suspend fun getFavorites(token: String): List<FavoriteDto>
    suspend fun addFavorite(token: String, movieId: Int): Boolean
    suspend fun removeFavorite(token: String, movieId: Int): Boolean
    suspend fun isFavorite(token: String, movieId: Int): Boolean
    suspend fun getComments(movieId: Int): List<CommentDto>
    suspend fun addComment(token: String, movieId: Int, content: String): Boolean
}
