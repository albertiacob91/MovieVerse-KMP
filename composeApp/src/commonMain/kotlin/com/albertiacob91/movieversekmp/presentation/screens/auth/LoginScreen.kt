package com.albertiacob91.movieversekmp.presentation.screens.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onLoginClick: suspend (email: String, password: String) -> String
) {
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    var resultMessage by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Login")

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email.value,
            onValueChange = { email.value = it },
            label = { Text("Email") }
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password.value,
            onValueChange = { password.value = it },
            label = { Text("Password") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                scope.launch {
                    resultMessage = onLoginClick(email.value, password.value)
                }
            }
        ) {
            Text("Login")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = onNavigateToRegister
        ) {
            Text("Go to Register")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(resultMessage)
    }
}