package com.albertiacob91.movieversekmp.presentation.viewmodel

import com.albertiacob91.movieversekmp.domain.usecase.movies.GetFavoritesUseCase
import com.albertiacob91.movieversekmp.domain.usecase.movies.GetMovieDetailUseCase
import com.albertiacob91.movieversekmp.fake.FakeMovieRepository
import com.albertiacob91.movieversekmp.fake.buildSessionStorage
import com.albertiacob91.movieversekmp.fake.favoriteDto
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
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class FavoritesViewModelTest {
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

    private fun buildVm(token: String? = null) = FavoritesViewModel(
        GetFavoritesUseCase(repo),
        GetMovieDetailUseCase(repo),
        buildSessionStorage(token = token)
    )

    @Test
    fun `loadFavorites sin token establece error de sesión`() = runTest {
        val vm = buildVm()
        vm.loadFavorites()
        assertEquals("Sesión no válida", vm.state.value.error)
        assertFalse(vm.state.value.isLoading)
    }

    @Test
    fun `loadFavorites carga las películas favoritas`() = runTest {
        repo.favoritesResult = listOf(favoriteDto("fav-1", 10))
        repo.movieDetailResult = movieDto(10, "Inception")
        val vm = buildVm(token = "token")

        vm.loadFavorites()

        val state = vm.state.value
        assertEquals(1, state.movies.size)
        assertEquals("Inception", state.movies[0].title)
        assertFalse(state.isLoading)
    }

    @Test
    fun `loadFavorites con lista vacía devuelve movies vacío`() = runTest {
        repo.favoritesResult = emptyList()
        val vm = buildVm(token = "token")

        vm.loadFavorites()

        assertTrue(vm.state.value.movies.isEmpty())
        assertFalse(vm.state.value.isLoading)
    }

    @Test
    fun `loadFavorites omite favoritos sin detalle de película`() = runTest {
        repo.favoritesResult = listOf(favoriteDto("fav-1", 10))
        repo.movieDetailResult = null
        val vm = buildVm(token = "token")

        vm.loadFavorites()

        assertTrue(vm.state.value.movies.isEmpty())
    }

    @Test
    fun `loadFavorites con error del repositorio establece mensaje de error`() = runTest {
        repo.shouldThrow = true
        repo.thrownException = RuntimeException("Error de red")
        val vm = buildVm(token = "token")

        vm.loadFavorites()

        assertEquals("Error de red", vm.state.value.error)
        assertFalse(vm.state.value.isLoading)
    }
}
