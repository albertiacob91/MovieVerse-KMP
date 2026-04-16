package com.albertiacob91.movieversekmp.presentation.screens.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.albertiacob91.movieversekmp.data.local.SessionStorage
import com.albertiacob91.movieversekmp.data.remote.AuthApi
import com.albertiacob91.movieversekmp.presentation.model.ProfileUi

@Composable
fun ProfileScreen(
    onBackClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    val authApi = remember { AuthApi() }
    val sessionStorage = remember { SessionStorage() }

    var profile by remember { mutableStateOf<ProfileUi?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val token = sessionStorage.getToken()

        if (token.isNullOrBlank()) {
            errorMessage = "Sesión no válida"
            isLoading = false
            return@LaunchedEffect
        }

        runCatching {
            authApi.getMe(token)
        }.onSuccess { me ->
            if (me != null) {
                profile = ProfileUi(
                    id = me.id,
                    username = me.username,
                    email = me.email
                )
                errorMessage = ""
            } else {
                errorMessage = "No se pudo cargar el perfil"
            }
        }.onFailure {
            errorMessage = it.message ?: "Error cargando perfil"
        }

        isLoading = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Button(onClick = onBackClick) {
            Text("Volver")
        }

        when {
            isLoading -> {
                Text(
                    text = "Cargando perfil...",
                    modifier = Modifier.padding(top = 16.dp)
                )
            }

            errorMessage.isNotBlank() -> {
                Text(
                    text = "Error: $errorMessage",
                    modifier = Modifier.padding(top = 16.dp)
                )
            }

            profile != null -> {
                Text(
                    text = "Perfil",
                    modifier = Modifier.padding(top = 16.dp)
                )

                Text(
                    text = "Username: ${profile!!.username}",
                    modifier = Modifier.padding(top = 12.dp)
                )

                Text(
                    text = "Email: ${profile!!.email}",
                    modifier = Modifier.padding(top = 8.dp)
                )

                Text(
                    text = "User ID: ${profile!!.id}",
                    modifier = Modifier.padding(top = 8.dp)
                )

                Button(
                    onClick = onLogoutClick,
                    modifier = Modifier.padding(top = 24.dp)
                ) {
                    Text("Logout")
                }
            }
        }
    }
}