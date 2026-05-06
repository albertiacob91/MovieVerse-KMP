package com.albertiacob91.movieversekmp.presentation.screens.forum

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.albertiacob91.movieversekmp.data.local.SessionStorage
import com.albertiacob91.movieversekmp.data.remote.AuthApi
import com.albertiacob91.movieversekmp.data.remote.ForumMessageDto
import com.albertiacob91.movieversekmp.presentation.theme.Dimens
import kotlinx.coroutines.launch

@Composable
fun ForumChatDetailScreen(
    contentPadding: PaddingValues,
    chatId: String
) {
    val authApi = remember { AuthApi() }
    val sessionStorage = remember { SessionStorage() }
    val scope = rememberCoroutineScope()

    var messages by remember { mutableStateOf<List<ForumMessageDto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }
    var messageText by remember { mutableStateOf("") }

    suspend fun loadMessages() {
        isLoading = true
        errorMessage = ""
        messages = authApi.getForumMessages(chatId)
        isLoading = false
    }

    LaunchedEffect(chatId) {
        runCatching {
            loadMessages()
        }.onFailure {
            errorMessage = it.message ?: "Error cargando mensajes"
            isLoading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(horizontal = Dimens.screenPadding)
    ) {
        OutlinedTextField(
            value = messageText,
            onValueChange = { messageText = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Dimens.mediumSpacing),
            label = { Text("Escribe un mensaje") }
        )

        Button(
            onClick = {
                val token = sessionStorage.getToken()
                if (!token.isNullOrBlank() && messageText.isNotBlank()) {
                    val content = messageText
                    messageText = ""

                    scope.launch {
                        val created = authApi.createForumMessage(token, chatId, content)
                        if (created != null) {
                            loadMessages()
                        } else {
                            errorMessage = "No se pudo enviar el mensaje"
                        }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            Text("Enviar")
        }

        when {
            isLoading -> {
                Text(
                    text = "Cargando mensajes...",
                    modifier = Modifier.padding(top = Dimens.mediumSpacing)
                )
            }

            errorMessage.isNotBlank() -> {
                Text(
                    text = "Error: $errorMessage",
                    modifier = Modifier.padding(top = Dimens.mediumSpacing)
                )
            }

            messages.isEmpty() -> {
                Text(
                    text = "Todavía no hay mensajes",
                    modifier = Modifier.padding(top = Dimens.mediumSpacing)
                )
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = Dimens.mediumSpacing),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(messages) { message ->
                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(message.username)
                            Text(
                                text = message.content,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}