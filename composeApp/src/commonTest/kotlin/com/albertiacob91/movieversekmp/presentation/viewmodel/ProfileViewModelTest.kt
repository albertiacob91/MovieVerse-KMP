package com.albertiacob91.movieversekmp.presentation.viewmodel

import com.albertiacob91.movieversekmp.data.remote.MeResponseDto
import com.albertiacob91.movieversekmp.domain.usecase.auth.GetCurrentUserUseCase
import com.albertiacob91.movieversekmp.domain.usecase.auth.UploadAvatarUseCase
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
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()
    private val repo = FakeAuthRepository()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun buildVm(token: String? = null) = ProfileViewModel(
        GetCurrentUserUseCase(repo, buildSessionStorage(token = token)),
        UploadAvatarUseCase(repo, buildSessionStorage(token = token))
    )

    @Test
    fun `loadProfile carga los datos del usuario`() = runTest {
        repo.getMeResult = MeResponseDto("user-1", "testuser", "test@test.com", null)
        val vm = buildVm(token = "token")

        vm.loadProfile()

        val state = vm.state.value
        assertNotNull(state.profile)
        assertEquals("testuser", state.profile!!.username)
        assertEquals("test@test.com", state.profile!!.email)
        assertFalse(state.isLoading)
    }

    @Test
    fun `loadProfile sin token establece error`() = runTest {
        val vm = buildVm()

        vm.loadProfile()

        assertEquals("No se pudo cargar el perfil", vm.state.value.error)
        assertFalse(vm.state.value.isLoading)
    }

    @Test
    fun `loadProfile cuando getMe devuelve null establece error`() = runTest {
        repo.getMeResult = null
        val vm = buildVm(token = "token")

        vm.loadProfile()

        assertNull(vm.state.value.profile)
        assertEquals("No se pudo cargar el perfil", vm.state.value.error)
    }

    @Test
    fun `loadProfile con error del repositorio establece mensaje de error`() = runTest {
        repo.shouldThrow = true
        repo.thrownException = RuntimeException("Sin conexión")
        val vm = buildVm(token = "token")

        vm.loadProfile()

        assertEquals("Sin conexión", vm.state.value.error)
        assertFalse(vm.state.value.isLoading)
    }

    @Test
    fun `uploadAvatar sin token establece error de foto no subida`() = runTest {
        // Sin token, UploadAvatarUseCase retorna false (no lanza), el VM lo trata como fallo
        val vm = buildVm()

        vm.uploadAvatar(ByteArray(10) { it.toByte() })

        assertFalse(vm.state.value.isUploadingAvatar)
        assertEquals("No se pudo subir la foto", vm.state.value.error)
    }

    @Test
    fun `uploadAvatar exitoso recarga el perfil`() = runTest {
        repo.uploadAvatarResult = true
        repo.getMeResult = MeResponseDto("user-1", "testuser", "test@test.com", "avatar-url")
        val vm = buildVm(token = "token")

        vm.uploadAvatar(ByteArray(10) { it.toByte() })

        assertNotNull(vm.state.value.profile)
        assertEquals("avatar-url", vm.state.value.profile!!.avatarUrl)
        assertFalse(vm.state.value.isUploadingAvatar)
    }

    @Test
    fun `uploadAvatar con resultado false establece error`() = runTest {
        repo.uploadAvatarResult = false
        val vm = buildVm(token = "token")

        vm.uploadAvatar(ByteArray(10) { it.toByte() })

        assertEquals("No se pudo subir la foto", vm.state.value.error)
        assertFalse(vm.state.value.isUploadingAvatar)
    }

    @Test
    fun `uploadAvatar con error del repositorio establece mensaje de error`() = runTest {
        repo.shouldThrow = true
        repo.thrownException = RuntimeException("Error al subir")
        val vm = buildVm(token = "token")

        vm.uploadAvatar(ByteArray(10) { it.toByte() })

        assertTrue(vm.state.value.error.isNotEmpty())
        assertFalse(vm.state.value.isUploadingAvatar)
    }
}
