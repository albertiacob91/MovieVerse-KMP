package com.albertiacob91.movieversekmp.fake

import com.albertiacob91.movieversekmp.data.remote.CommentDto
import com.albertiacob91.movieversekmp.data.remote.FavoriteDto
import com.albertiacob91.movieversekmp.data.remote.MovieDto
import com.albertiacob91.movieversekmp.domain.repository.MovieRepository

class FakeMovieRepository : MovieRepository {
    var popularMoviesResult: List<MovieDto> = emptyList()
    var searchMoviesResult: List<MovieDto> = emptyList()
    var movieDetailResult: MovieDto? = null
    var favoritesResult: List<FavoriteDto> = emptyList()
    var addFavoriteResult: Boolean = true
    var removeFavoriteResult: Boolean = true
    var isFavoriteResult: Boolean = false
    var commentsResult: List<CommentDto> = emptyList()
    var addCommentResult: Boolean = true

    var shouldThrow: Boolean = false
    var thrownException: Throwable = RuntimeException("Error de red")

    var lastSearchQuery: String? = null
    var lastSearchPage: Int? = null
    var lastPopularPage: Int? = null
    var lastFavoriteToken: String? = null
    var lastAddedMovieId: Int? = null
    var lastRemovedMovieId: Int? = null
    var lastCheckedMovieId: Int? = null
    var lastCommentContent: String? = null

    private val popularQueue = ArrayDeque<List<MovieDto>>()
    private val searchQueue = ArrayDeque<List<MovieDto>>()

    fun setPopularSequence(vararg pages: List<MovieDto>) {
        popularQueue.clear()
        popularQueue.addAll(pages)
    }

    fun setSearchSequence(vararg pages: List<MovieDto>) {
        searchQueue.clear()
        searchQueue.addAll(pages)
    }

    override suspend fun getPopularMovies(page: Int): List<MovieDto> {
        lastPopularPage = page
        if (shouldThrow) throw thrownException
        return if (popularQueue.isNotEmpty()) popularQueue.removeFirst() else popularMoviesResult
    }

    override suspend fun searchMovies(query: String, page: Int): List<MovieDto> {
        lastSearchQuery = query
        lastSearchPage = page
        if (shouldThrow) throw thrownException
        return if (searchQueue.isNotEmpty()) searchQueue.removeFirst() else searchMoviesResult
    }

    override suspend fun getMovieDetail(movieId: Int): MovieDto? {
        if (shouldThrow) throw thrownException
        return movieDetailResult
    }

    override suspend fun getFavorites(token: String): List<FavoriteDto> {
        lastFavoriteToken = token
        if (shouldThrow) throw thrownException
        return favoritesResult
    }

    override suspend fun addFavorite(token: String, movieId: Int): Boolean {
        lastFavoriteToken = token
        lastAddedMovieId = movieId
        if (shouldThrow) throw thrownException
        return addFavoriteResult
    }

    override suspend fun removeFavorite(token: String, movieId: Int): Boolean {
        lastFavoriteToken = token
        lastRemovedMovieId = movieId
        if (shouldThrow) throw thrownException
        return removeFavoriteResult
    }

    override suspend fun isFavorite(token: String, movieId: Int): Boolean {
        lastFavoriteToken = token
        lastCheckedMovieId = movieId
        if (shouldThrow) throw thrownException
        return isFavoriteResult
    }

    override suspend fun getComments(movieId: Int): List<CommentDto> {
        if (shouldThrow) throw thrownException
        return commentsResult
    }

    override suspend fun addComment(token: String, movieId: Int, content: String): Boolean {
        lastCommentContent = content
        if (shouldThrow) throw thrownException
        return addCommentResult
    }
}
