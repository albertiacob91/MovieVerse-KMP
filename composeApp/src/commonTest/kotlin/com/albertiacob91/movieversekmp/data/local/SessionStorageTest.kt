package com.albertiacob91.movieversekmp.data.local

import com.russhwolf.settings.MapSettings
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SessionStorageTest {
    private lateinit var storage: SessionStorage

    @BeforeTest
    fun setUp() {
        storage = SessionStorage(MapSettings())
    }

    @Test
    fun `saveToken y getToken persisten el token`() {
        storage.saveToken("mi-token-123")
        assertEquals("mi-token-123", storage.getToken())
    }

    @Test
    fun `getToken devuelve null si no hay token guardado`() {
        assertNull(storage.getToken())
    }

    @Test
    fun `saveUserId y getUserId persisten el userId`() {
        storage.saveUserId("user-42")
        assertEquals("user-42", storage.getUserId())
    }

    @Test
    fun `getUserId devuelve null si no hay userId guardado`() {
        assertNull(storage.getUserId())
    }

    @Test
    fun `clearSession elimina token y userId`() {
        storage.saveToken("token")
        storage.saveUserId("user-1")

        storage.clearSession()

        assertNull(storage.getToken())
        assertNull(storage.getUserId())
    }

    @Test
    fun `isLoggedIn devuelve true si hay token válido`() {
        storage.saveToken("token-valido")
        assertTrue(storage.isLoggedIn())
    }

    @Test
    fun `isLoggedIn devuelve false si no hay token`() {
        assertFalse(storage.isLoggedIn())
    }

    @Test
    fun `isLoggedIn devuelve false tras clearSession`() {
        storage.saveToken("token")
        storage.clearSession()
        assertFalse(storage.isLoggedIn())
    }

    @Test
    fun `sobreescribir token guarda el nuevo valor`() {
        storage.saveToken("token-viejo")
        storage.saveToken("token-nuevo")
        assertEquals("token-nuevo", storage.getToken())
    }

    @Test
    fun `clearSession no lanza excepción si la sesión ya estaba vacía`() {
        storage.clearSession()
        assertNull(storage.getToken())
        assertNull(storage.getUserId())
    }
}
