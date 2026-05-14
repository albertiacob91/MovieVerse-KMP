package com.albertiacob91.movieversekmp.domain.usecase

import com.albertiacob91.movieversekmp.data.remote.AuthResponseDto
import com.albertiacob91.movieversekmp.domain.usecase.auth.GetCurrentUserUseCase
import com.albertiacob91.movieversekmp.domain.usecase.auth.LoginUseCase
import com.albertiacob91.movieversekmp.domain.usecase.auth.RegisterUseCase
import com.albertiacob91.movieversekmp.domain.usecase.auth.UploadAvatarUseCase
import com.albertiacob91.movieversekmp.fake.FakeAuthRepository
import com.albertiacob91.movieversekmp.fake.buildSessionStorage
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class LoginUseCaseTest {
    private val repo = FakeAuthRepository()

    @Test
    fun `login exitoso guarda el token en sesión`() = runTest {
        val session = buildSessionStorage()
        val useCase = LoginUseCase(repo, session)
        repo.loginResult = AuthResponseDto("ok", "user-1", "testuser", "token-abc")

        useCase("test@test.com", "password")

        assertEquals("token-abc", session.getToken())
    }

    @Test
    fun `login sin token no guarda nada en sesión`() = runTest {
        val session = buildSessionStorage()
        val useCase = LoginUseCase(repo, session)
        repo.loginResult = AuthResponseDto("Usuario ya existe", token = null)

        useCase("test@test.com", "password")

        assertNull(session.getToken())
    }

    @Test
    fun `login retorna el dto del repositorio`() = runTest {
        val session = buildSessionStorage()
        val useCase = LoginUseCase(repo, session)

        val result = useCase("test@test.com", "password")

        assertEquals("Login exitoso", result.message)
        assertEquals("user-1", result.userId)
    }

    @Test
    fun `login pasa email y password al repositorio`() = runTest {
        val session = buildSessionStorage()
        val useCase = LoginUseCase(repo, session)

        useCase("usuario@correo.com", "1234")

        assertEquals("usuario@correo.com", repo.lastLoginEmail)
        assertEquals("1234", repo.lastLoginPassword)
    }

    @Test
    fun `login con token vacío no guarda sesión`() = runTest {
        val session = buildSessionStorage()
        val useCase = LoginUseCase(repo, session)
        repo.loginResult = AuthResponseDto("ok", token = "")

        useCase("test@test.com", "password")

        assertNull(session.getToken())
    }
}

class RegisterUseCaseTest {
    private val repo = FakeAuthRepository()
    private val useCase = RegisterUseCase(repo)

    @Test
    fun `registro exitoso retorna dto con mensaje`() = runTest {
        repo.registerResult = AuthResponseDto("Registro exitoso", "user-2", "newuser", "token-xyz")
        val result = useCase("newuser", "new@test.com", "pass123")
        assertEquals("Registro exitoso", result.message)
    }

    @Test
    fun `pasa username al repositorio`() = runTest {
        useCase("miusuario", "email@test.com", "pass")
        assertEquals("miusuario", repo.lastRegisterUsername)
    }

    @Test
    fun `propaga excepción si el registro falla`() = runTest {
        repo.shouldThrow = true
        var threw = false
        try { useCase("u", "e", "p") } catch (e: Exception) { threw = true }
        assertTrue(threw)
    }
}

class GetCurrentUserUseCaseTest {
    private val repo = FakeAuthRepository()

    @Test
    fun `devuelve el usuario si hay token en sesión`() = runTest {
        val session = buildSessionStorage(token = "valid-token")
        val useCase = GetCurrentUserUseCase(repo, session)

        val result = useCase()

        assertNotNull(result)
        assertEquals("user-1", result.id)
        assertEquals("testuser", result.username)
    }

    @Test
    fun `devuelve null si no hay token en sesión`() = runTest {
        val session = buildSessionStorage()
        val useCase = GetCurrentUserUseCase(repo, session)

        val result = useCase()

        assertNull(result)
    }

    @Test
    fun `devuelve null si el repositorio no encuentra el usuario`() = runTest {
        val session = buildSessionStorage(token = "token-invalido")
        val useCase = GetCurrentUserUseCase(repo, session)
        repo.getMeResult = null

        val result = useCase()

        assertNull(result)
    }

    @Test
    fun `propaga excepción si el repositorio falla`() = runTest {
        val session = buildSessionStorage(token = "token")
        val useCase = GetCurrentUserUseCase(repo, session)
        repo.shouldThrow = true

        var threw = false
        try { useCase() } catch (e: Exception) { threw = true }
        assertTrue(threw)
    }
}

class UploadAvatarUseCaseTest {
    private val repo = FakeAuthRepository()

    @Test
    fun `sube avatar exitosamente si hay token`() = runTest {
        val session = buildSessionStorage(token = "mi-token")
        val useCase = UploadAvatarUseCase(repo, session)
        repo.uploadAvatarResult = true

        val result = useCase("base64data==")

        assertTrue(result)
        assertEquals("base64data==", repo.lastUploadedAvatarBase64)
    }

    @Test
    fun `devuelve false si no hay token`() = runTest {
        val session = buildSessionStorage()
        val useCase = UploadAvatarUseCase(repo, session)

        val result = useCase("base64data==")

        assertFalse(result)
    }

    @Test
    fun `devuelve false si el repositorio falla al subir`() = runTest {
        val session = buildSessionStorage(token = "token")
        val useCase = UploadAvatarUseCase(repo, session)
        repo.uploadAvatarResult = false

        val result = useCase("data")

        assertFalse(result)
    }
}
