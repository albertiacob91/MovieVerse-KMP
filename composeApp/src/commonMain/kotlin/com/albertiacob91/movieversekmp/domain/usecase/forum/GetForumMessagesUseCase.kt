package com.albertiacob91.movieversekmp.domain.usecase.forum

import com.albertiacob91.movieversekmp.data.remote.ForumMessageDto
import com.albertiacob91.movieversekmp.domain.repository.ForumRepository

class GetForumMessagesUseCase(private val repository: ForumRepository) {
    suspend operator fun invoke(chatId: String): List<ForumMessageDto> = repository.getForumMessages(chatId)
}
