package com.albertiacob91.movieversekmp.presentation.viewmodel

import com.albertiacob91.movieversekmp.domain.usecase.movies.GetPopularMoviesUseCase
import com.albertiacob91.movieversekmp.domain.usecase.movies.SearchMoviesUseCase
import com.albertiacob91.movieversekmp.fake.FakeMovieRepository
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
class MoviesViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()
    private val repo = FakeMovieRepository()
    private lateinit var vm: MoviesViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        vm = MoviesViewModel(
            GetPopularMoviesUseCase(repo),
            SearchMoviesUseCase(repo)
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `estado inicial es vacío y sin carga`() {
        val state = vm.state.value
        assertTrue(state.movies.isEmpty())
        assertFalse(state.isLoading)
        assertEquals("", state.error)
    }

    @Test
    fun `loadForQuery con query vacío carga películas populares`() = runTest {
        // El VM carga página 1 y página 2 al inicio — configuramos respuesta distinta por llamada
        repo.setPopularSequence(listOf(movieDto(1), movieDto(2)), emptyList())
        vm.loadForQuery("")
        val state = vm.state.value
        assertEquals(2, state.movies.size)
        assertFalse(state.isLoading)
        assertEquals("", state.error)
    }

    @Test
    fun `loadForQuery con query no vacío usa búsqueda`() = runTest {
        repo.setSearchSequence(listOf(movieDto(10, "Batman")), emptyList())
        vm.loadForQuery("Batman")
        val state = vm.state.value
        assertEquals(1, state.movies.size)
        assertEquals("Batman", state.movies[0].title)
        assertEquals("Batman", state.currentQuery)
    }

    @Test
    fun `loadForQuery con dos páginas concatena resultados`() = runTest {
        repo.setPopularSequence(listOf(movieDto(1)), listOf(movieDto(2)))
        vm.loadForQuery("")
        val state = vm.state.value
        assertEquals(2, state.movies.size)
        assertEquals(1, state.movies[0].id)
        assertEquals(2, state.movies[1].id)
    }

    @Test
    fun `loadForQuery no relanza si ya está cargando la misma query`() = runTest {
        repo.popularMoviesResult = listOf(movieDto(1))
        vm.loadForQuery("")
        val firstState = vm.state.value
        vm.loadForQuery("")
        assertEquals(firstState.movies, vm.state.value.movies)
    }

    @Test
    fun `loadForQuery establece error si el repositorio falla`() = runTest {
        repo.shouldThrow = true
        repo.thrownException = RuntimeException("Sin conexión")
        vm.loadForQuery("")
        val state = vm.state.value
        assertEquals("Sin conexión", state.error)
        assertFalse(state.isLoading)
    }

    @Test
    fun `loadForQuery con lista vacía marca endReached`() = runTest {
        repo.popularMoviesResult = emptyList()
        vm.loadForQuery("")
        assertTrue(vm.state.value.endReached)
    }

    @Test
    fun `loadNextPage añade nuevas películas al estado`() = runTest {
        // Carga inicial: página 1=[1], página 2=[2] (no marca endReached)
        repo.setPopularSequence(listOf(movieDto(1)), listOf(movieDto(2)))
        vm.loadForQuery("")
        assertEquals(2, vm.state.value.movies.size)

        // loadNextPage carga página 3
        repo.setPopularSequence(listOf(movieDto(3)))
        vm.loadNextPage()

        assertEquals(3, vm.state.value.movies.size)
        assertEquals(3, vm.state.value.movies[2].id)
    }

    @Test
    fun `loadNextPage no hace nada si endReached es true`() = runTest {
        repo.popularMoviesResult = emptyList()
        vm.loadForQuery("")
        assertTrue(vm.state.value.endReached)

        vm.loadNextPage()
        assertTrue(vm.state.value.movies.isEmpty())
    }

    @Test
    fun `loadNextPage marca endReached si no hay más películas`() = runTest {
        repo.popularMoviesResult = listOf(movieDto(1))
        vm.loadForQuery("")

        repo.popularMoviesResult = emptyList()
        vm.loadNextPage()

        assertTrue(vm.state.value.endReached)
    }

    @Test
    fun `loadNextPage establece error si el repositorio falla`() = runTest {
        repo.popularMoviesResult = listOf(movieDto(1))
        vm.loadForQuery("")

        repo.shouldThrow = true
        repo.thrownException = RuntimeException("Error de página")
        vm.loadNextPage()

        assertEquals("Error de página", vm.state.value.error)
    }
}
