package com.albertiacob91.movieversekmp.presentation.screens.forum

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.albertiacob91.movieversekmp.data.remote.ForumChatDto
import com.albertiacob91.movieversekmp.presentation.theme.Dimens
import kotlinx.coroutines.launch

@Composable
fun ForumScreen(
    contentPadding: PaddingValues,
    onChatClick: (ForumChatDto) -> Unit
) {
    val authApi = remember { AuthApi() }
    val sessionStorage = remember { SessionStorage() }
    val scope = rememberCoroutineScope()

    var chats by remember { mutableStateOf<List<ForumChatDto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }

    var showCreateDialog by remember { mutableStateOf(false) }
    var newChatTitle by remember { mutableStateOf("") }

    suspend fun loadChats() {
        isLoading = true
        errorMessage = ""
        chats = authApi.getForumChats()
        isLoading = false
    }

    LaunchedEffect(Unit) {
        runCatching {
            loadChats()
        }.onFailure {
            errorMessage = it.message ?: "Error cargando chats"
            isLoading = false
        }
    }

    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = {
                showCreateDialog = false
                newChatTitle = ""
            },
            title = { Text("Nuevo chat global") },
            text = {
                OutlinedTextField(
                    value = newChatTitle,
                    onValueChange = { newChatTitle = it },
                    label = { Text("Título del chat") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val token = sessionStorage.getToken()
                        if (!token.isNullOrBlank() && newChatTitle.isNotBlank()) {
                            showCreateDialog = false
                            val title = newChatTitle
                            newChatTitle = ""

                            scope.launch {
                                val created = authApi.createForumChat(token, title)
                                if (created != null) {
                                    loadChats()
                                } else {
                                    errorMessage = "No se pudo crear el chat"
                                }
                            }
                        }
                    }
                ) {
                    Text("Crear")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showCreateDialog = false
                        newChatTitle = ""
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(horizontal = Dimens.screenPadding)
    ) {
        Button(
            onClick = { showCreateDialog = true },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Dimens.mediumSpacing)
        ) {
            Text("Crear nuevo chat global")
        }

        when {
            isLoading -> {
                Text(
                    text = "Cargando chats...",
                    modifier = Modifier.padding(top = Dimens.mediumSpacing)
                )
            }

            errorMessage.isNotBlank() -> {
                Text(
                    text = "Error: $errorMessage",
                    modifier = Modifier.padding(top = Dimens.mediumSpacing)
                )
            }

            chats.isEmpty() -> {
                Text(
                    text = "Todavía no hay chats creados",
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
                    items(chats) { chat ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onChatClick(chat) },
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(chat.title)
                                Text(
                                    text = "Creado por: ${chat.createdBy}",
                                    modifier = Modifier.padding(top = 6.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}