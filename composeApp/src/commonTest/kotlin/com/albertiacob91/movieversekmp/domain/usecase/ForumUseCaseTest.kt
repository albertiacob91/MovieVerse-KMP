package com.albertiacob91.movieversekmp.domain.usecase

import com.albertiacob91.movieversekmp.domain.usecase.forum.CreateForumChatUseCase
import com.albertiacob91.movieversekmp.domain.usecase.forum.CreateForumMessageUseCase
import com.albertiacob91.movieversekmp.domain.usecase.forum.DeleteForumChatUseCase
import com.albertiacob91.movieversekmp.domain.usecase.forum.GetForumChatsUseCase
import com.albertiacob91.movieversekmp.domain.usecase.forum.GetForumMessagesUseCase
import com.albertiacob91.movieversekmp.fake.FakeForumRepository
import com.albertiacob91.movieversekmp.fake.forumChatDto
import com.albertiacob91.movieversekmp.fake.forumMessageDto
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class GetForumChatsUseCaseTest {
    private val repo = FakeForumRepository()
    private val useCase = GetForumChatsUseCase(repo)

    @Test
    fun `devuelve lista de chats del foro`() = runTest {
        repo.chatsResult = listOf(forumChatDto("c-1", "General"), forumChatDto("c-2", "Cine"))
        val result = useCase()
        assertEquals(2, result.size)
        assertEquals("General", result[0].title)
    }

    @Test
    fun `devuelve lista vacía si no hay chats`() = runTest {
        repo.chatsResult = emptyList()
        val result = useCase()
        assertTrue(result.isEmpty())
    }

    @Test
    fun `propaga excepción del repositorio`() = runTest {
        repo.shouldThrow = true
        var threw = false
        try { useCase() } catch (e: Exception) { threw = true }
        assertTrue(threw)
    }
}

class CreateForumChatUseCaseTest {
    private val repo = FakeForumRepository()
    private val useCase = CreateForumChatUseCase(repo)

    @Test
    fun `crea chat y devuelve el dto`() = runTest {
        repo.createChatResult = forumChatDto("chat-nuevo", "Mi Chat")
        val result = useCase("token", "Mi Chat")
        assertNotNull(result)
        assertEquals("chat-nuevo", result.id)
        assertEquals("Mi Chat", result.title)
    }

    @Test
    fun `pasa el título al repositorio`() = runTest {
        useCase("token", "Título del chat")
        assertEquals("Título del chat", repo.lastCreatedTitle)
    }

    @Test
    fun `devuelve null si el repositorio devuelve null`() = runTest {
        repo.createChatResult = null
        val result = useCase("token", "título")
        assertNull(result)
    }
}

class DeleteForumChatUseCaseTest {
    private val repo = FakeForumRepository()
    private val useCase = DeleteForumChatUseCase(repo)

    @Test
    fun `elimina chat y devuelve true`() = runTest {
        repo.deleteChatResult = true
        val result = useCase("token", "chat-1")
        assertTrue(result)
    }

    @Test
    fun `pasa el chatId al repositorio`() = runTest {
        useCase("token", "chat-abc")
        assertEquals("chat-abc", repo.lastDeletedChatId)
    }

    @Test
    fun `devuelve false si el repositorio falla`() = runTest {
        repo.deleteChatResult = false
        val result = useCase("token", "chat-1")
        assertFalse(result)
    }
}

class GetForumMessagesUseCaseTest {
    private val repo = FakeForumRepository()
    private val useCase = GetForumMessagesUseCase(repo)

    @Test
    fun `devuelve mensajes del chat`() = runTest {
        repo.messagesResult = listOf(
            forumMessageDto("m-1", "chat-1", "Hola"),
            forumMessageDto("m-2", "chat-1", "¿Qué tal?")
        )
        val result = useCase("chat-1")
        assertEquals(2, result.size)
        assertEquals("Hola", result[0].content)
    }

    @Test
    fun `devuelve lista vacía si no hay mensajes`() = runTest {
        repo.messagesResult = emptyList()
        val result = useCase("chat-vacio")
        assertTrue(result.isEmpty())
    }
}

class CreateForumMessageUseCaseTest {
    private val repo = FakeForumRepository()
    private val useCase = CreateForumMessageUseCase(repo)

    @Test
    fun `crea mensaje y devuelve el dto`() = runTest {
        repo.createMessageResult = forumMessageDto("msg-nuevo", "chat-1", "Mensaje enviado")
        val result = useCase("token", "chat-1", "Mensaje enviado")
        assertNotNull(result)
        assertEquals("msg-nuevo", result.id)
        assertEquals("Mensaje enviado", result.content)
    }

    @Test
    fun `pasa el contenido al repositorio`() = runTest {
        useCase("token", "chat-1", "mi mensaje")
        assertEquals("mi mensaje", repo.lastSentContent)
    }

    @Test
    fun `devuelve null si el repositorio devuelve null`() = runTest {
        repo.createMessageResult = null
        val result = useCase("token", "chat-1", "mensaje")
        assertNull(result)
    }
}
