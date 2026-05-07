package com.albertiacob91.movieversekmp.domain.repository

import com.albertiacob91.movieversekmp.data.remote.ForumChatDto
import com.albertiacob91.movieversekmp.data.remote.ForumMessageDto

interface ForumRepository {
    suspend fun getForumChats(): List<ForumChatDto>
    suspend fun createForumChat(token: String, title: String): ForumChatDto?
    suspend fun getForumMessages(chatId: String): List<ForumMessageDto>
    suspend fun createForumMessage(token: String, chatId: String, content: String): ForumMessageDto?
}
