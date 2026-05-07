package com.albertiacob91.movieversekmp.domain.usecase.forum

import com.albertiacob91.movieversekmp.data.remote.ForumChatDto
import com.albertiacob91.movieversekmp.domain.repository.ForumRepository

class GetForumChatsUseCase(private val repository: ForumRepository) {
    suspend operator fun invoke(): List<ForumChatDto> = repository.getForumChats()
}
