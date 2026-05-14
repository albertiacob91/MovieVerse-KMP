package com.albertiacob91.movieversekmp.presentation.viewmodel

import com.albertiacob91.movieversekmp.domain.usecase.movies.CheckIsFavoriteUseCase
import com.albertiacob91.movieversekmp.domain.usecase.movies.GetCommentsUseCase
import com.albertiacob91.movieversekmp.domain.usecase.movies.GetMovieDetailUseCase
import com.albertiacob91.movieversekmp.domain.usecase.movies.PostCommentUseCase
import com.albertiacob91.movieversekmp.domain.usecase.movies.ToggleFavoriteUseCase
import com.albertiacob91.movieversekmp.fake.FakeMovieRepository
import com.albertiacob91.movieversekmp.fake.buildSessionStorage
import com.albertiacob91.movieversekmp.fake.commentDto
import com.albertiacob91.movieversekmp.fake.movieDto
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
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class MovieDetailViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()
    private val repo = FakeMovieRepository()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun buildVm(token: String? = null) = MovieDetailViewModel(
        GetMovieDetailUseCase(repo),
        GetCommentsUseCase(repo),
        ToggleFavoriteUseCase(repo),
        CheckIsFavoriteUseCase(repo),
        PostCommentUseCase(repo),
        buildSessionStorage(token = token)
    )

    @Test
    fun `load carga película y comentarios`() = runTest {
        repo.movieDetailResult = movieDto(1, "Dune")
        repo.commentsResult = listOf(commentDto("c-1", 1, "Épica"))
        val vm = buildVm()

        vm.load(1)

        val state = vm.state.value
        assertNotNull(state.movie)
        assertEquals("Dune", state.movie!!.title)
        assertEquals(1, state.comments.size)
        assertFalse(state.isLoading)
    }

    @Test
    fun `load con película no encontrada establece error`() = runTest {
        repo.movieDetailResult = null
        val vm = buildVm()

        vm.load(999)

        assertEquals("Película no encontrada", vm.state.value.error)
    }

    @Test
    fun `load con token verifica si es favorito`() = runTest {
        repo.movieDetailResult = movieDto(1)
        repo.isFavoriteResult = true
        val vm = buildVm(token = "mi-token")

        vm.load(1)

        assertTrue(vm.state.value.isFavorite)
    }

    @Test
    fun `load sin token no verifica favorito`() = runTest {
        repo.movieDetailResult = movieDto(1)
        repo.isFavoriteResult = true
        val vm = buildVm()

        vm.load(1)

        assertFalse(vm.state.value.isFavorite)
    }

    @Test
    fun `load establece error si falla la carga`() = runTest {
        repo.shouldThrow = true
        repo.thrownException = RuntimeException("Sin conexión")
        val vm = buildVm()

        vm.load(1)

        assertEquals("Sin conexión", vm.state.value.error)
    }

    @Test
    fun `toggleFavorite sin token establece mensaje de sesión inválida`() = runTest {
        val vm = buildVm()
        vm.toggleFavorite(1)
        assertEquals("Sesión no válida", vm.state.value.favoriteMessage)
    }

    @Test
    fun `toggleFavorite añade a favoritos si no lo era`() = runTest {
        repo.movieDetailResult = movieDto(1)
        repo.addFavoriteResult = true
        val vm = buildVm(token = "token")
        vm.load(1)

        vm.toggleFavorite(1)

        assertTrue(vm.state.value.isFavorite)
        assertEquals("Añadida a favoritas", vm.state.value.favoriteMessage)
    }

    @Test
    fun `toggleFavorite quita de favoritos si ya lo era`() = runTest {
        repo.movieDetailResult = movieDto(1)
        repo.isFavoriteResult = true
        repo.removeFavoriteResult = true
        val vm = buildVm(token = "token")
        vm.load(1)

        vm.toggleFavorite(1)

        assertFalse(vm.state.value.isFavorite)
        assertEquals("Quitada de favoritas", vm.state.value.favoriteMessage)
    }

    @Test
    fun `toggleFavorite establece mensaje de error si la operación falla`() = runTest {
        repo.movieDetailResult = movieDto(1)
        repo.addFavoriteResult = false
        val vm = buildVm(token = "token")
        vm.load(1)

        vm.toggleFavorite(1)

        assertEquals("No se pudo actualizar", vm.state.value.favoriteMessage)
    }

    @Test
    fun `postComment sin token establece mensaje de sesión inválida`() = runTest {
        val vm = buildVm()
        vm.postComment(1, "comentario")
        assertEquals("Sesión no válida", vm.state.value.commentMessage)
    }

    @Test
    fun `postComment con contenido vacío establece mensaje de validación`() = runTest {
        val vm = buildVm(token = "token")
        vm.postComment(1, "   ")
        assertEquals("El comentario no puede estar vacío", vm.state.value.commentMessage)
    }

    @Test
    fun `postComment exitoso actualiza la lista de comentarios`() = runTest {
        repo.movieDetailResult = movieDto(1)
        repo.addCommentResult = true
        repo.commentsResult = listOf(commentDto("c-1", 1, "Nuevo comentario"))
        val vm = buildVm(token = "token")

        vm.postComment(1, "Nuevo comentario")

        assertEquals("Comentario publicado", vm.state.value.commentMessage)
        assertEquals(1, vm.state.value.comments.size)
    }
}
