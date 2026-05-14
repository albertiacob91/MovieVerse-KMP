package com.albertiacob91.movieversekmp.presentation.viewmodel

import com.albertiacob91.movieversekmp.domain.usecase.forum.CreateForumChatUseCase
import com.albertiacob91.movieversekmp.domain.usecase.forum.CreateForumMessageUseCase
import com.albertiacob91.movieversekmp.domain.usecase.forum.DeleteForumChatUseCase
import com.albertiacob91.movieversekmp.domain.usecase.forum.GetForumChatsUseCase
import com.albertiacob91.movieversekmp.domain.usecase.forum.GetForumMessagesUseCase
import com.albertiacob91.movieversekmp.fake.FakeForumRepository
import com.albertiacob91.movieversekmp.fake.buildSessionStorage
import com.albertiacob91.movieversekmp.fake.forumChatDto
import com.albertiacob91.movieversekmp.fake.forumMessageDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ForumViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()
    private val repo = FakeForumRepository()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun buildVm(token: String? = null, userId: String? = null) = ForumViewModel(
        GetForumChatsUseCase(repo),
        CreateForumChatUseCase(repo),
        DeleteForumChatUseCase(repo),
        buildSessionStorage(token = token, userId = userId)
    )

    @Test
    fun `loadChats carga la lista de chats`() = runTest {
        repo.chatsResult = listOf(forumChatDto("c-1", "General"), forumChatDto("c-2", "Cine"))
        val vm = buildVm()

        vm.loadChats()

        val state = vm.state.value
        assertEquals(2, state.chats.size)
        assertFalse(state.isLoading)
        assertEquals("", state.error)
    }

    @Test
    fun `loadChats carga el userId en el estado`() = runTest {
        repo.chatsResult = emptyList()
        val vm = buildVm(userId = "user-42")

        vm.loadChats()

        assertEquals("user-42", vm.state.value.currentUserId)
    }

    @Test
    fun `loadChats con error establece mensaje de error`() = runTest {
        repo.shouldThrow = true
        repo.thrownException = RuntimeException("Sin conexión")
        val vm = buildVm()

        vm.loadChats()

        assertEquals("Sin conexión", vm.state.value.error)
        assertFalse(vm.state.value.isLoading)
    }

    @Test
    fun `createChat sin token establece error de sesión`() = runTest {
        val vm = buildVm()
        vm.createChat("Nuevo chat")
        assertEquals("Sesión no válida", vm.state.value.error)
    }

    @Test
    fun `createChat exitoso recarga los chats`() = runTest {
        repo.createChatResult = forumChatDto("nuevo", "Nuevo Chat")
        repo.chatsResult = listOf(forumChatDto("nuevo", "Nuevo Chat"))
        val vm = buildVm(token = "token")

        vm.createChat("Nuevo Chat")

        assertEquals(1, vm.state.value.chats.size)
        assertEquals("Nuevo Chat", vm.state.value.chats[0].title)
    }

    @Test
    fun `createChat con resultado null establece error`() = runTest {
        repo.createChatResult = null
        val vm = buildVm(token = "token")

        vm.createChat("título")

        assertEquals("No se pudo crear el chat", vm.state.value.error)
    }

    @Test
    fun `deleteChat elimina el chat de la lista`() = runTest {
        repo.chatsResult = listOf(forumChatDto("c-1"), forumChatDto("c-2"))
        val vm = buildVm(token = "token")
        vm.loadChats()

        repo.deleteChatResult = true
        vm.deleteChat("c-1")

        assertEquals(1, vm.state.value.chats.size)
        assertEquals("c-2", vm.state.value.chats[0].id)
    }

    @Test
    fun `deleteChat sin token no hace nada`() = runTest {
        repo.chatsResult = listOf(forumChatDto("c-1"))
        val vm = buildVm()
        vm.loadChats()

        vm.deleteChat("c-1")

        assertEquals(1, vm.state.value.chats.size)
    }

    @Test
    fun `deleteChat con error del repositorio establece mensaje de error`() = runTest {
        repo.chatsResult = listOf(forumChatDto("c-1"))
        val vm = buildVm(token = "token")
        vm.loadChats()

        repo.shouldThrow = true
        repo.thrownException = RuntimeException("Error al eliminar")
        vm.deleteChat("c-1")

        assertEquals("Error al eliminar", vm.state.value.error)
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class ForumChatDetailViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()
    private val repo = FakeForumRepository()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun buildVm(token: String? = null) = ForumChatDetailViewModel(
        GetForumMessagesUseCase(repo),
        CreateForumMessageUseCase(repo),
        buildSessionStorage(token = token)
    )

    @Test
    fun `loadMessages carga los mensajes del chat`() = runTest {
        repo.messagesResult = listOf(
            forumMessageDto("m-1", "chat-1", "Hola"),
            forumMessageDto("m-2", "chat-1", "¿Qué tal?")
        )
        val vm = buildVm()

        vm.loadMessages("chat-1")

        val state = vm.state.value
        assertEquals(2, state.messages.size)
        assertEquals("Hola", state.messages[0].content)
        assertFalse(state.isLoading)
    }

    @Test
    fun `loadMessages con error establece mensaje de error`() = runTest {
        repo.shouldThrow = true
        repo.thrownException = RuntimeException("Error cargando")
        val vm = buildVm()

        vm.loadMessages("chat-1")

        assertEquals("Error cargando", vm.state.value.error)
        assertFalse(vm.state.value.isLoading)
    }

    @Test
    fun `sendMessage con contenido vacío no hace nada`() = runTest {
        val vm = buildVm(token = "token")
        vm.sendMessage("chat-1", "   ")
        assertTrue(vm.state.value.messages.isEmpty())
    }

    @Test
    fun `sendMessage sin token no hace nada`() = runTest {
        val vm = buildVm()
        vm.sendMessage("chat-1", "Hola")
        assertTrue(vm.state.value.messages.isEmpty())
    }

    @Test
    fun `sendMessage exitoso recarga los mensajes`() = runTest {
        repo.createMessageResult = forumMessageDto("nuevo", "chat-1", "Hola")
        repo.messagesResult = listOf(forumMessageDto("nuevo", "chat-1", "Hola"))
        val vm = buildVm(token = "token")

        vm.sendMessage("chat-1", "Hola")

        assertEquals(1, vm.state.value.messages.size)
        assertEquals("Hola", vm.state.value.messages[0].content)
    }

    @Test
    fun `sendMessage con resultado null establece error`() = runTest {
        repo.createMessageResult = null
        val vm = buildVm(token = "token")

        vm.sendMessage("chat-1", "Hola")

        assertEquals("No se pudo enviar el mensaje", vm.state.value.error)
    }

    @Test
    fun `sendMessage con error del repositorio establece mensaje de error`() = runTest {
        repo.shouldThrow = true
        repo.thrownException = RuntimeException("Error enviando")
        val vm = buildVm(token = "token")

        vm.sendMessage("chat-1", "Hola")

        assertEquals("Error enviando", vm.state.value.error)
    }
}
