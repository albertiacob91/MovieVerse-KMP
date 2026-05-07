package com.albertiacob91.movieversekmp.data.repository

import com.albertiacob91.movieversekmp.data.remote.AuthApi
import com.albertiacob91.movieversekmp.data.remote.CommentDto
import com.albertiacob91.movieversekmp.data.remote.FavoriteDto
import com.albertiacob91.movieversekmp.data.remote.MovieDto
import com.albertiacob91.movieversekmp.domain.repository.MovieRepository

class MovieRepositoryImpl(private val api: AuthApi) : MovieRepository {
    override suspend fun getPopularMovies(page: Int): List<MovieDto> = api.getPopularMovies(page)
    override suspend fun searchMovies(query: String, page: Int): List<MovieDto> = api.searchMovies(query, page)
    override suspend fun getMovieDetail(movieId: Int): MovieDto? = api.getMovieDetail(movieId)
    override suspend fun getFavorites(token: String): List<FavoriteDto> = api.getFavorites(token)
    override suspend fun addFavorite(token: String, movieId: Int): Boolean = api.addFavorite(token, movieId)
    override suspend fun removeFavorite(token: String, movieId: Int): Boolean = api.removeFavorite(token, movieId)
    override suspend fun isFavorite(token: String, movieId: Int): Boolean = api.isFavorite(token, movieId)
    override suspend fun getComments(movieId: Int): List<CommentDto> = api.getComments(movieId)
    override suspend fun addComment(token: String, movieId: Int, content: String): Boolean = api.addComment(token, movieId, content)
}
