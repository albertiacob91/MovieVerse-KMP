package com.albertiacob91.movieversekmp.presentation.screens.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.albertiacob91.movieversekmp.data.local.SessionStorage
import com.albertiacob91.movieversekmp.data.remote.AuthApi
import com.albertiacob91.movieversekmp.presentation.model.ProfileUi
import com.albertiacob91.movieversekmp.presentation.theme.Dimens

@Composable
fun ProfileScreen(
    contentPadding: PaddingValues,
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
            .padding(contentPadding)
            .padding(horizontal = Dimens.screenPadding),
        verticalArrangement = Arrangement.Top
    ) {
        when {
            isLoading -> {
                Text(
                    text = "Cargando perfil...",
                    modifier = Modifier.padding(top = Dimens.mediumSpacing)
                )
            }

            errorMessage.isNotBlank() -> {
                Text(
                    text = "Error: $errorMessage",
                    modifier = Modifier.padding(top = Dimens.mediumSpacing)
                )
            }

            profile != null -> {
                Card(
                    modifier = Modifier.padding(top = Dimens.mediumSpacing)
                ) {
                    Column(
                        modifier = Modifier.padding(Dimens.mediumSpacing)
                    ) {
                        Text(
                            text = "Username",
                            style = MaterialTheme.typography.labelMedium
                        )
                        Text(
                            text = profile!!.username,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(top = Dimens.smallSpacing)
                        )

                        Text(
                            text = "Email",
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.padding(top = Dimens.mediumSpacing)
                        )
                        Text(
                            text = profile!!.email,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(top = Dimens.smallSpacing)
                        )

                        Text(
                            text = "User ID",
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.padding(top = Dimens.mediumSpacing)
                        )
                        Text(
                            text = profile!!.id,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(top = Dimens.smallSpacing)
                        )
                    }
                }

                Button(
                    onClick = onLogoutClick,
                    modifier = Modifier.padding(top = Dimens.largeSpacing)
                ) {
                    Text("Logout")
                }
            }
        }
    }
}