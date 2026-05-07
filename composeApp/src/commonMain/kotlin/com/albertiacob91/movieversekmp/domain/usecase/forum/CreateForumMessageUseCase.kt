package com.albertiacob91.movieversekmp.domain.usecase.forum

import com.albertiacob91.movieversekmp.data.remote.ForumMessageDto
import com.albertiacob91.movieversekmp.domain.repository.ForumRepository

class CreateForumMessageUseCase(private val repository: ForumRepository) {
    suspend operator fun invoke(token: String, chatId: String, content: String): ForumMessageDto? =
        repository.createForumMessage(token, chatId, content)
}
