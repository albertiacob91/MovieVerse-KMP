package com.albertiacob91.movieversekmp.fake

import com.albertiacob91.movieversekmp.data.remote.ForumChatDto
import com.albertiacob91.movieversekmp.data.remote.ForumMessageDto
import com.albertiacob91.movieversekmp.domain.repository.ForumRepository

class FakeForumRepository : ForumRepository {
    var chatsResult: List<ForumChatDto> = emptyList()
    var createChatResult: ForumChatDto? = ForumChatDto(
        id = "chat-1",
        title = "Chat de prueba",
        createdBy = "testuser",
        userId = "user-1",
        createdAt = "2024-01-01T00:00:00"
    )
    var deleteChatResult: Boolean = true
    var messagesResult: List<ForumMessageDto> = emptyList()
    var createMessageResult: ForumMessageDto? = ForumMessageDto(
        id = "msg-1",
        chatId = "chat-1",
        content = "Hola",
        username = "testuser",
        userId = "user-1",
        createdAt = "2024-01-01T00:00:00"
    )

    var shouldThrow: Boolean = false
    var thrownException: Throwable = RuntimeException("Error de red")

    var lastDeletedChatId: String? = null
    var lastCreatedTitle: String? = null
    var lastSentContent: String? = null

    override suspend fun getForumChats(): List<ForumChatDto> {
        if (shouldThrow) throw thrownException
        return chatsResult
    }

    override suspend fun createForumChat(token: String, title: String): ForumChatDto? {
        lastCreatedTitle = title
        if (shouldThrow) throw thrownException
        return createChatResult
    }

    override suspend fun deleteForumChat(token: String, chatId: String): Boolean {
        lastDeletedChatId = chatId
        if (shouldThrow) throw thrownException
        return deleteChatResult
    }

    override suspend fun getForumMessages(chatId: String): List<ForumMessageDto> {
        if (shouldThrow) throw thrownException
        return messagesResult
    }

    override suspend fun createForumMessage(token: String, chatId: String, content: String): ForumMessageDto? {
        lastSentContent = content
        if (shouldThrow) throw thrownException
        return createMessageResult
    }
}
