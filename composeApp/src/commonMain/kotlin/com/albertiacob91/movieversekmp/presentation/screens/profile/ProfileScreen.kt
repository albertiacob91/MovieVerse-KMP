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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.albertiacob91.movieversekmp.presentation.components.rememberImagePickerLauncher
import com.albertiacob91.movieversekmp.presentation.theme.Dimens
import com.albertiacob91.movieversekmp.presentation.viewmodel.ProfileViewModel
import org.koin.compose.viewmodel.koinViewModel
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalEncodingApi::class)
@Composable
fun ProfileScreen(
    contentPadding: PaddingValues,
    onLogoutClick: () -> Unit
) {
    val viewModel: ProfileViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    val pickImage = rememberImagePickerLauncher { bytes ->
        viewModel.uploadAvatar(bytes)
    }

    LaunchedEffect(Unit) {
        viewModel.loadProfile()
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
            state.isLoading -> {
                Text(text = "Cargando perfil...", style = MaterialTheme.typography.bodyLarge)
            }

            state.error.isNotBlank() && state.profile == null -> {
                Text(text = "Error: ${state.error}", style = MaterialTheme.typography.bodyLarge)
            }

            state.profile != null -> {
                val profile = state.profile!!

                Box(contentAlignment = Alignment.Center) {
                    if (profile.avatarUrl != null) {
                        val avatarBytes = remember(profile.avatarUrl) {
                            runCatching { Base64.decode(profile.avatarUrl) }.getOrNull()
                        }
                        AsyncImage(
                            model = avatarBytes,
                            contentDescription = "Avatar",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(88.dp)
                                .clip(CircleShape)
                        )
                    } else {
                        val initial = profile.username.trim().take(1).uppercase()
                        Box(
                            modifier = Modifier
                                .size(88.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = initial,
                                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.SemiBold)
                            )
                        }
                    }

                    if (state.isUploadingAvatar) {
                        CircularProgressIndicator(modifier = Modifier.size(88.dp))
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(
                    onClick = { if (!state.isUploadingAvatar) pickImage() },
                    modifier = Modifier
                ) {
                    Text(if (state.isUploadingAvatar) "Subiendo..." else "Cambiar foto")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = profile.username,
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.SemiBold)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = profile.email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(24.dp))

                ProfileInfoCard(title = "Nombre de usuario", value = profile.username)
                Spacer(modifier = Modifier.height(12.dp))
                ProfileInfoCard(title = "Correo electrónico", value = profile.email)
                Spacer(modifier = Modifier.height(12.dp))
                ProfileInfoCard(title = "ID de usuario", value = profile.id)

                Spacer(modifier = Modifier.height(28.dp))

                Button(onClick = onLogoutClick, modifier = Modifier.fillMaxWidth()) {
                    Text("Cerrar sesión")
                }
            }
        }
    }
}

@Composable
private fun ProfileInfoCard(title: String, value: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = value, style = MaterialTheme.typography.bodyLarge)
        }
    }
}
