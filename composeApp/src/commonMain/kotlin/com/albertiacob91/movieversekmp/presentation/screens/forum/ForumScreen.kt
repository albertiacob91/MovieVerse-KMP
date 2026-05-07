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
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.albertiacob91.movieversekmp.data.remote.ForumChatDto
import com.albertiacob91.movieversekmp.presentation.theme.Dimens
import com.albertiacob91.movieversekmp.presentation.viewmodel.ForumViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ForumScreen(
    contentPadding: PaddingValues,
    onChatClick: (ForumChatDto) -> Unit
) {
    val viewModel: ForumViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    var showCreateDialog by remember { mutableStateOf(false) }
    var newChatTitle by remember { mutableStateOf("") }

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
                    items(state.chats) { chat ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onChatClick(chat) },
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(chat.title)
                                Text(text = "Creado por: ${chat.createdBy}", modifier = Modifier.padding(top = 6.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}
