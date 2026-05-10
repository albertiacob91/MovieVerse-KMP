package com.albertiacob91.movieversekmp.presentation.screens.forum

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.albertiacob91.movieversekmp.data.remote.ForumChatDto
import com.albertiacob91.movieversekmp.presentation.theme.Dimens
import com.albertiacob91.movieversekmp.presentation.viewmodel.ForumViewModel
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForumScreen(
    contentPadding: PaddingValues,
    onChatClick: (ForumChatDto) -> Unit
) {
    val viewModel: ForumViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    var showCreateDialog by remember { mutableStateOf(false) }
    var newChatTitle by remember { mutableStateOf("") }
    var chatToDelete by remember { mutableStateOf<ForumChatDto?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadChats()
    }

    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = { showCreateDialog = false; newChatTitle = "" },
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
                        if (newChatTitle.isNotBlank()) {
                            val title = newChatTitle
                            showCreateDialog = false
                            newChatTitle = ""
                            viewModel.createChat(title)
                        }
                    }
                ) { Text("Crear") }
            },
            dismissButton = {
                TextButton(onClick = { showCreateDialog = false; newChatTitle = "" }) { Text("Cancelar") }
            }
        )
    }

    chatToDelete?.let { chat ->
        AlertDialog(
            onDismissRequest = { chatToDelete = null },
            title = { Text("Eliminar chat") },
            text = { Text("¿Eliminar \"${chat.title}\"? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteChat(chat.id)
                    chatToDelete = null
                }) { Text("Eliminar", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { chatToDelete = null }) { Text("Cancelar") }
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
            state.isLoading -> {
                Text(text = "Cargando chats...", modifier = Modifier.padding(top = Dimens.mediumSpacing))
            }

            state.error.isNotBlank() -> {
                Text(text = "Error: ${state.error}", modifier = Modifier.padding(top = Dimens.mediumSpacing))
            }

            state.chats.isEmpty() -> {
                Text(text = "Todavía no hay chats creados", modifier = Modifier.padding(top = Dimens.mediumSpacing))
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = Dimens.mediumSpacing),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.chats, key = { it.id }) { chat ->
                        val isOwner = chat.userId == state.currentUserId

                        if (isOwner) {
                            val dismissState = rememberSwipeToDismissBoxState(
                                confirmValueChange = { value ->
                                    if (value == SwipeToDismissBoxValue.EndToStart) {
                                        chatToDelete = chat
                                    }
                                    false
                                }
                            )

                            val bgColor by animateColorAsState(
                                if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart)
                                    MaterialTheme.colorScheme.errorContainer
                                else
                                    MaterialTheme.colorScheme.surfaceVariant
                            )

                            SwipeToDismissBox(
                                state = dismissState,
                                enableDismissFromStartToEnd = false,
                                backgroundContent = {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(bgColor, shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
                                            .padding(end = 20.dp),
                                        contentAlignment = Alignment.CenterEnd
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Eliminar",
                                            tint = MaterialTheme.colorScheme.onErrorContainer
                                        )
                                    }
                                }
                            ) {
                                ChatCard(chat = chat, onClick = { onChatClick(chat) })
                            }
                        } else {
                            ChatCard(chat = chat, onClick = { onChatClick(chat) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ChatCard(chat: ForumChatDto, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(chat.title)
            Text(
                text = "Creado por: ${chat.createdBy}",
                modifier = Modifier.padding(top = 6.dp)
            )
        }
    }
}
