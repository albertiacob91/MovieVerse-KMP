package com.albertiacob91.movieversekmp.presentation.screens.forum

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.albertiacob91.movieversekmp.presentation.theme.Dimens
import com.albertiacob91.movieversekmp.presentation.viewmodel.ForumChatDetailViewModel
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForumChatDetailScreen(
    contentPadding: PaddingValues,
    chatId: String,
    title: String,
    onBackClick: () -> Unit
) {
    val viewModel: ForumChatDetailViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    var newMessage by remember { mutableStateOf("") }

    LaunchedEffect(chatId) {
        viewModel.loadMessages(chatId)
    }

    LaunchedEffect(state.messages) {
        if (!state.isLoading) newMessage = ""
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                ),
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                title = { Text(title) }
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = Dimens.screenPadding, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = newMessage,
                    onValueChange = { newMessage = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Escribe un mensaje") },
                    singleLine = true
                )

                FloatingActionButton(
                    onClick = {
                        if (newMessage.isNotBlank() && !state.isSending) {
                            val msg = newMessage
                            newMessage = ""
                            viewModel.sendMessage(chatId, msg)
                        }
                    },
                    modifier = Modifier.size(52.dp),
                    containerColor = Color(0xFF6E56CF),
                    contentColor = Color.White
                ) {
                    if (state.isSending) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                    } else {
                        Icon(imageVector = Icons.Default.Send, contentDescription = "Enviar")
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(contentPadding)
                .padding(horizontal = Dimens.screenPadding)
        ) {
            when {
                state.isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                state.error.isNotBlank() && state.messages.isEmpty() -> {
                    Text(text = "Error: ${state.error}", modifier = Modifier.padding(top = Dimens.mediumSpacing))
                }

                state.messages.isEmpty() -> {
                    Text(text = "Todavía no hay mensajes en este chat", modifier = Modifier.padding(top = Dimens.mediumSpacing))
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = Dimens.mediumSpacing),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(state.messages) { message ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(text = message.username)
                                    Text(text = message.content, modifier = Modifier.padding(top = 4.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
