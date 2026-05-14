package com.albertiacob91.movieversekmp.fake

import com.albertiacob91.movieversekmp.data.local.SessionStorage
import com.albertiacob91.movieversekmp.data.remote.CommentDto
import com.albertiacob91.movieversekmp.data.remote.FavoriteDto
import com.albertiacob91.movieversekmp.data.remote.ForumChatDto
import com.albertiacob91.movieversekmp.data.remote.ForumMessageDto
import com.albertiacob91.movieversekmp.data.remote.MovieDto
import com.russhwolf.settings.MapSettings

fun buildSessionStorage(token: String? = null, userId: String? = null): SessionStorage {
    val storage = SessionStorage(MapSettings())
    if (token != null) storage.saveToken(token)
    if (userId != null) storage.saveUserId(userId)
    return storage
}

fun movieDto(id: Int = 1, title: String = "Película $id") = MovieDto(
    id = id,
    title = title,
    overview = "Sinopsis de $title",
    voteAverage = 7.5,
    releaseDate = "2024-01-01"
)

fun commentDto(id: String = "c-1", movieId: Int = 1, content: String = "Muy buena") = CommentDto(
    id = id,
    movieId = movieId,
    userId = "user-1",
    username = "testuser",
    content = content
)

fun favoriteDto(id: String = "fav-1", movieId: Int = 1) = FavoriteDto(
    id = id,
    movieId = movieId,
    userId = "user-1"
)

fun forumChatDto(id: String = "chat-1", title: String = "Chat $id") = ForumChatDto(
    id = id,
    title = title,
    createdBy = "testuser",
    userId = "user-1",
    createdAt = "2024-01-01T00:00:00"
)

fun forumMessageDto(id: String = "msg-1", chatId: String = "chat-1", content: String = "Hola") = ForumMessageDto(
    id = id,
    chatId = chatId,
    content = content,
    username = "testuser",
    userId = "user-1",
    createdAt = "2024-01-01T00:00:00"
)
