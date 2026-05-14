package com.albertiacob91.movieversekmp.presentation.viewmodel

import com.albertiacob91.movieversekmp.data.remote.AuthResponseDto
import com.albertiacob91.movieversekmp.domain.usecase.auth.GetCurrentUserUseCase
import com.albertiacob91.movieversekmp.domain.usecase.auth.LoginUseCase
import com.albertiacob91.movieversekmp.domain.usecase.auth.RegisterUseCase
import com.albertiacob91.movieversekmp.fake.FakeAuthRepository
import com.albertiacob91.movieversekmp.fake.buildSessionStorage
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
class AuthViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()
    private val repo = FakeAuthRepository()
    private lateinit var vm: AuthViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun buildVm(token: String? = null, userId: String? = null): AuthViewModel {
        val session = buildSessionStorage(token, userId)
        return AuthViewModel(
            LoginUseCase(repo, session),
            RegisterUseCase(repo),
            GetCurrentUserUseCase(repo, session),
            session
        )
    }

    @Test
    fun `checkSession sin token marca sesión no iniciada`() = runTest {
        vm = buildVm()
        vm.checkSession()
        val state = vm.state.value
        assertFalse(state.isCheckingSession)
        assertFalse(state.isLoggedIn)
    }

    @Test
    fun `checkSession con token válido marca sesión iniciada`() = runTest {
        vm = buildVm(token = "valid-token")
        vm.checkSession()
        val state = vm.state.value
        assertFalse(state.isCheckingSession)
        assertTrue(state.isLoggedIn)
    }

    @Test
    fun `checkSession con token pero getMe falla cierra la sesión`() = runTest {
        vm = buildVm(token = "expired-token")
        repo.getMeResult = null
        vm.checkSession()
        val state = vm.state.value
        assertFalse(state.isLoggedIn)
    }

    @Test
    fun `login exitoso actualiza isLoggedIn a true`() = runTest {
        vm = buildVm()
        repo.loginResult = AuthResponseDto("ok", "user-1", "testuser", "new-token")
        vm.login("user@test.com", "pass")
        assertTrue(vm.state.value.isLoggedIn)
    }

    @Test
    fun `login sin token no actualiza isLoggedIn`() = runTest {
        vm = buildVm()
        repo.loginResult = AuthResponseDto("Error", token = null)
        vm.login("user@test.com", "pass")
        assertFalse(vm.state.value.isLoggedIn)
    }

    @Test
    fun `login retorna mensaje de respuesta`() = runTest {
        vm = buildVm()
        repo.loginResult = AuthResponseDto("Bienvenido", "u", "u", "t")
        val msg = vm.login("user@test.com", "pass")
        assertEquals("Bienvenido", msg)
    }

    @Test
    fun `login con error retorna mensaje de error`() = runTest {
        vm = buildVm()
        repo.shouldThrow = true
        repo.thrownException = RuntimeException("Credenciales inválidas")
        val msg = vm.login("user@test.com", "pass")
        assertTrue(msg.contains("Login error"))
    }

    @Test
    fun `register retorna el mensaje del dto`() = runTest {
        vm = buildVm()
        repo.registerResult = AuthResponseDto("Cuenta creada")
        val msg = vm.register("user", "user@test.com", "pass")
        assertEquals("Cuenta creada", msg)
    }

    @Test
    fun `register con error retorna mensaje de error`() = runTest {
        vm = buildVm()
        repo.shouldThrow = true
        repo.thrownException = RuntimeException("Email ya existe")
        val msg = vm.register("u", "e", "p")
        assertTrue(msg.contains("Register error"))
    }

    @Test
    fun `logout marca isLoggedIn como false`() = runTest {
        vm = buildVm(token = "token")
        repo.loginResult = AuthResponseDto("ok", "u", "u", "token")
        vm.login("u@u.com", "p")
        assertTrue(vm.state.value.isLoggedIn)

        vm.logout()
        assertFalse(vm.state.value.isLoggedIn)
    }
}
