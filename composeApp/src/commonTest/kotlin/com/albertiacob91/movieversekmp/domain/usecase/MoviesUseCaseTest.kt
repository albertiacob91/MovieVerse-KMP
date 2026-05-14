package com.albertiacob91.movieversekmp.domain.usecase

import com.albertiacob91.movieversekmp.domain.usecase.movies.CheckIsFavoriteUseCase
import com.albertiacob91.movieversekmp.domain.usecase.movies.GetCommentsUseCase
import com.albertiacob91.movieversekmp.domain.usecase.movies.GetFavoritesUseCase
import com.albertiacob91.movieversekmp.domain.usecase.movies.GetMovieDetailUseCase
import com.albertiacob91.movieversekmp.domain.usecase.movies.GetPopularMoviesUseCase
import com.albertiacob91.movieversekmp.domain.usecase.movies.PostCommentUseCase
import com.albertiacob91.movieversekmp.domain.usecase.movies.SearchMoviesUseCase
import com.albertiacob91.movieversekmp.domain.usecase.movies.ToggleFavoriteUseCase
import com.albertiacob91.movieversekmp.fake.FakeMovieRepository
import com.albertiacob91.movieversekmp.fake.commentDto
import com.albertiacob91.movieversekmp.fake.favoriteDto
import com.albertiacob91.movieversekmp.fake.movieDto
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class GetPopularMoviesUseCaseTest {
    private val repo = FakeMovieRepository()
    private val useCase = GetPopularMoviesUseCase(repo)

    @Test
    fun `devuelve la lista de películas populares`() = runTest {
        repo.popularMoviesResult = listOf(movieDto(1), movieDto(2))
        val result = useCase(1)
        assertEquals(2, result.size)
        assertEquals(1, result[0].id)
    }

    @Test
    fun `pasa el número de página al repositorio`() = runTest {
        useCase(3)
        assertEquals(3, repo.lastPopularPage)
    }

    @Test
    fun `devuelve lista vacía si no hay películas`() = runTest {
        repo.popularMoviesResult = emptyList()
        val result = useCase(1)
        assertTrue(result.isEmpty())
    }

    @Test
    fun `propaga excepción del repositorio`() = runTest {
        repo.shouldThrow = true
        var threw = false
        try { useCase(1) } catch (e: Exception) { threw = true }
        assertTrue(threw)
    }
}

class SearchMoviesUseCaseTest {
    private val repo = FakeMovieRepository()
    private val useCase = SearchMoviesUseCase(repo)

    @Test
    fun `devuelve resultados de búsqueda`() = runTest {
        repo.searchMoviesResult = listOf(movieDto(5, "Batman"))
        val result = useCase("Batman", 1)
        assertEquals(1, result.size)
        assertEquals("Batman", result[0].title)
    }

    @Test
    fun `pasa query y página al repositorio`() = runTest {
        useCase("Inception", 2)
        assertEquals("Inception", repo.lastSearchQuery)
        assertEquals(2, repo.lastSearchPage)
    }

    @Test
    fun `devuelve lista vacía si no hay resultados`() = runTest {
        repo.searchMoviesResult = emptyList()
        val result = useCase("xyz-inexistente", 1)
        assertTrue(result.isEmpty())
    }
}

class GetMovieDetailUseCaseTest {
    private val repo = FakeMovieRepository()
    private val useCase = GetMovieDetailUseCase(repo)

    @Test
    fun `devuelve el detalle de la película`() = runTest {
        repo.movieDetailResult = movieDto(42, "Oppenheimer")
        val result = useCase(42)
        assertNotNull(result)
        assertEquals(42, result.id)
        assertEquals("Oppenheimer", result.title)
    }

    @Test
    fun `devuelve null si la película no existe`() = runTest {
        repo.movieDetailResult = null
        val result = useCase(999)
        assertNull(result)
    }
}

class GetFavoritesUseCaseTest {
    private val repo = FakeMovieRepository()
    private val useCase = GetFavoritesUseCase(repo)

    @Test
    fun `devuelve la lista de favoritos`() = runTest {
        repo.favoritesResult = listOf(favoriteDto("fav-1", 10), favoriteDto("fav-2", 20))
        val result = useCase("token-123")
        assertEquals(2, result.size)
        assertEquals(10, result[0].movieId)
    }

    @Test
    fun `pasa el token al repositorio`() = runTest {
        useCase("mi-token")
        assertEquals("mi-token", repo.lastFavoriteToken)
    }
}

class ToggleFavoriteUseCaseTest {
    private val repo = FakeMovieRepository()
    private val useCase = ToggleFavoriteUseCase(repo)

    @Test
    fun `cuando es favorito llama a removeFavorite`() = runTest {
        repo.removeFavoriteResult = true
        val result = useCase("token", 5, currentlyFavorite = true)
        assertTrue(result)
        assertEquals(5, repo.lastRemovedMovieId)
        assertNull(repo.lastAddedMovieId)
    }

    @Test
    fun `cuando no es favorito llama a addFavorite`() = runTest {
        repo.addFavoriteResult = true
        val result = useCase("token", 5, currentlyFavorite = false)
        assertTrue(result)
        assertEquals(5, repo.lastAddedMovieId)
        assertNull(repo.lastRemovedMovieId)
    }

    @Test
    fun `devuelve false si la operación falla`() = runTest {
        repo.addFavoriteResult = false
        val result = useCase("token", 5, currentlyFavorite = false)
        assertFalse(result)
    }
}

class CheckIsFavoriteUseCaseTest {
    private val repo = FakeMovieRepository()
    private val useCase = CheckIsFavoriteUseCase(repo)

    @Test
    fun `devuelve true si es favorito`() = runTest {
        repo.isFavoriteResult = true
        val result = useCase("token", 1)
        assertTrue(result)
    }

    @Test
    fun `devuelve false si no es favorito`() = runTest {
        repo.isFavoriteResult = false
        val result = useCase("token", 1)
        assertFalse(result)
    }

    @Test
    fun `pasa token y movieId al repositorio`() = runTest {
        useCase("mi-token", 99)
        assertEquals("mi-token", repo.lastFavoriteToken)
        assertEquals(99, repo.lastCheckedMovieId)
    }
}

class GetCommentsUseCaseTest {
    private val repo = FakeMovieRepository()
    private val useCase = GetCommentsUseCase(repo)

    @Test
    fun `devuelve comentarios de la película`() = runTest {
        repo.commentsResult = listOf(commentDto("c-1", 1, "Genial"), commentDto("c-2", 1, "Regular"))
        val result = useCase(1)
        assertEquals(2, result.size)
        assertEquals("Genial", result[0].content)
    }

    @Test
    fun `devuelve lista vacía si no hay comentarios`() = runTest {
        repo.commentsResult = emptyList()
        val result = useCase(1)
        assertTrue(result.isEmpty())
    }
}

class PostCommentUseCaseTest {
    private val repo = FakeMovieRepository()
    private val useCase = PostCommentUseCase(repo)

    @Test
    fun `devuelve true al publicar comentario exitoso`() = runTest {
        repo.addCommentResult = true
        val result = useCase("token", 1, "Excelente película")
        assertTrue(result)
    }

    @Test
    fun `pasa el contenido al repositorio`() = runTest {
        useCase("token", 1, "Mi comentario")
        assertEquals("Mi comentario", repo.lastCommentContent)
    }

    @Test
    fun `devuelve false si el repositorio falla`() = runTest {
        repo.addCommentResult = false
        val result = useCase("token", 1, "comentario")
        assertFalse(result)
    }
}
