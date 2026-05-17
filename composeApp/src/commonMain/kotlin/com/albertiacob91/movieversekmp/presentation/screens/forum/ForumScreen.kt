package com.albertiacob91.movieversekmp.presentation.screens.forum

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.albertiacob91.movieversekmp.data.remote.ForumChatDto
import com.albertiacob91.movieversekmp.presentation.theme.Dimens
import com.albertiacob91.movieversekmp.presentation.viewmodel.ForumViewModel
import org.koin.compose.viewmodel.koinViewModel
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalEncodingApi::class)
@Composable
fun ForumScreen(
    contentPadding: PaddingValues,
    searchQuery: String = "",
    showCreateDialog: Boolean = false,
    onCreateDialogDismiss: () -> Unit = {},
    onChatClick: (ForumChatDto) -> Unit
) {
    val viewModel: ForumViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    var newChatTitle by remember { mutableStateOf("") }
    var chatToDelete by remember { mutableStateOf<ForumChatDto?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadChats()
    }

    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = { onCreateDialogDismiss(); newChatTitle = "" },
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
                            onCreateDialogDismiss()
                            newChatTitle = ""
                            viewModel.createChat(title)
                        }
                    }
                ) { Text("Crear") }
            },
            dismissButton = {
                TextButton(onClick = { onCreateDialogDismiss(); newChatTitle = "" }) { Text("Cancelar") }
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

    val filteredChats = remember(state.chats, searchQuery) {
        if (searchQuery.isBlank()) state.chats
        else state.chats.filter { it.title.contains(searchQuery, ignoreCase = true) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(horizontal = Dimens.screenPadding)
    ) {
        when {
            state.isLoading -> {
                Text(text = "Cargando chats...", modifier = Modifier.padding(top = Dimens.mediumSpacing))
            }

            state.error.isNotBlank() -> {
                Text(text = "Error: ${state.error}", modifier = Modifier.padding(top = Dimens.mediumSpacing))
            }

            filteredChats.isEmpty() -> {
                Text(
                    text = if (searchQuery.isNotBlank()) "Sin resultados para \"$searchQuery\"" else "Todavía no hay chats creados",
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
                    items(filteredChats) { chat ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onChatClick(chat) },
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(chat.title)

                                Row(
                                    modifier = Modifier.padding(top = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val avatarBytes = remember(chat.avatarUrl) {
                                        chat.avatarUrl?.let { runCatching { Base64.decode(it) }.getOrNull() }
                                    }

                                    if (avatarBytes != null) {
                                        AsyncImage(
                                            model = avatarBytes,
                                            contentDescription = "Avatar de ${chat.createdBy}",
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .size(20.dp)
                                                .clip(CircleShape)
                                        )
                                    } else {
                                        val initial = chat.createdBy.trim().take(1).uppercase()
                                        Box(
                                            modifier = Modifier
                                                .size(20.dp)
                                                .clip(CircleShape)
                                                .background(MaterialTheme.colorScheme.surfaceVariant),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = initial,
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(6.dp))

                                    Text(
                                        text = "Creado por: ${chat.createdBy}",
                                        modifier = Modifier.padding(top = 0.dp)
                                    )
                                }
                            }
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
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
            Text(
                text = chat.title,
                style = MaterialTheme.typography.titleSmall
            )
            Text(
                text = "Creado por ${chat.createdBy}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}
