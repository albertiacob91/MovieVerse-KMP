package com.albertiacob91.movieversekmp.presentation.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(Dimens.largeSpacing))

        when {
            isLoading -> {
                Text(
                    text = "Cargando perfil...",
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            errorMessage.isNotBlank() -> {
                Text(
                    text = "Error: $errorMessage",
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            profile != null -> {
                val initial = profile!!.username
                    .trim()
                    .take(1)
                    .uppercase()

                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = initial,
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = profile!!.username,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = profile!!.email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(24.dp))

                ProfileInfoCard(
                    title = "Nombre de usuario",
                    value = profile!!.username
                )

                Spacer(modifier = Modifier.height(12.dp))

                ProfileInfoCard(
                    title = "Correo electrónico",
                    value = profile!!.email
                )

                Spacer(modifier = Modifier.height(12.dp))

                ProfileInfoCard(
                    title = "ID de usuario",
                    value = profile!!.id
                )

                Spacer(modifier = Modifier.height(28.dp))

                Button(
                    onClick = onLogoutClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cerrar sesión")
                }
            }
        }
    }
}

@Composable
private fun ProfileInfoCard(
    title: String,
    value: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}