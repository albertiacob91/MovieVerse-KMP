package com.albertiacob91.movieversekmp.domain.usecase.forum

import com.albertiacob91.movieversekmp.domain.repository.ForumRepository

class DeleteForumChatUseCase(private val repository: ForumRepository) {
    suspend operator fun invoke(token: String, chatId: String): Boolean =
        repository.deleteForumChat(token, chatId)
}
