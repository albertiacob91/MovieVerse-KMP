package com.albertiacob91.movieversekmp.data.repository

import com.albertiacob91.movieversekmp.data.remote.AuthApi
import com.albertiacob91.movieversekmp.data.remote.ForumChatDto
import com.albertiacob91.movieversekmp.data.remote.ForumMessageDto
import com.albertiacob91.movieversekmp.domain.repository.ForumRepository

class ForumRepositoryImpl(private val api: AuthApi) : ForumRepository {
    override suspend fun getForumChats(): List<ForumChatDto> = api.getForumChats()
    override suspend fun createForumChat(token: String, title: String): ForumChatDto? = api.createForumChat(token, title)
    override suspend fun deleteForumChat(token: String, chatId: String): Boolean = api.deleteForumChat(token, chatId)
    override suspend fun getForumMessages(chatId: String): List<ForumMessageDto> = api.getForumMessages(chatId)
    override suspend fun createForumMessage(token: String, chatId: String, content: String): ForumMessageDto? = api.createForumMessage(token, chatId, content)
}
