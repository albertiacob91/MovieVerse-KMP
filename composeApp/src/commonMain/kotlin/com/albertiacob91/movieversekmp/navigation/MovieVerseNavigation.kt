package com.albertiacob91.movieversekmp.navigation

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.albertiacob91.movieversekmp.data.local.SessionStorage
import com.albertiacob91.movieversekmp.data.remote.AuthApi
import com.albertiacob91.movieversekmp.presentation.components.LoadingScreen
import com.albertiacob91.movieversekmp.presentation.screens.auth.LoginScreen
import com.albertiacob91.movieversekmp.presentation.screens.auth.RegisterScreen
import com.albertiacob91.movieversekmp.presentation.screens.movies.FavoritesScreen
import com.albertiacob91.movieversekmp.presentation.screens.movies.MovieDetailScreen
import com.albertiacob91.movieversekmp.presentation.screens.movies.MoviesScreen
import com.albertiacob91.movieversekmp.presentation.screens.profile.ProfileScreen

private enum class RootScreen {
    Auth,
    Home
}

private enum class AuthFlowScreen {
    Login,
    Register
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieVerseNavigation() {
    val authApi = remember { AuthApi() }
    val sessionStorage = remember { SessionStorage() }

    var rootScreen by remember { mutableStateOf<RootScreen?>(null) }
    var authFlowScreen by remember { mutableStateOf(AuthFlowScreen.Login) }
    var movieScreen by remember { mutableStateOf<MovieScreen>(MovieScreen.Home) }

    var lastListScreen by remember { mutableStateOf<MovieScreen>(MovieScreen.Home) }

    var searchVisible by rememberSaveable { mutableStateOf(false) }
    var searchQuery by rememberSaveable { mutableStateOf("") }

    val homeListState = rememberLazyListState()

    LaunchedEffect(Unit) {
        val token = sessionStorage.getToken()

        if (token.isNullOrBlank()) {
            rootScreen = RootScreen.Auth
        } else {
            val me = runCatching { authApi.getMe(token) }.getOrNull()

            if (me != null) {
                rootScreen = RootScreen.Home
            } else {
                sessionStorage.clearSession()
                rootScreen = RootScreen.Auth
            }
        }
    }

    when (rootScreen) {
        null -> {
            LoadingScreen()
        }

        RootScreen.Auth -> {
            when (authFlowScreen) {
                AuthFlowScreen.Login -> {
                    LoginScreen(
                        onNavigateToRegister = {
                            authFlowScreen = AuthFlowScreen.Register
                        },
                        onLoginClick = { email, password ->
                            runCatching {
                                val response = authApi.login(email, password)

                                if (!response.token.isNullOrBlank()) {
                                    sessionStorage.saveToken(response.token)
                                    movieScreen = MovieScreen.Home
                                    lastListScreen = MovieScreen.Home
                                    searchVisible = false
                                    searchQuery = ""
                                    rootScreen = RootScreen.Home
                                }

                                response.message
                            }.getOrElse {
                                "Login error: ${it.message}"
                            }
                        }
                    )
                }

                AuthFlowScreen.Register -> {
                    RegisterScreen(
                        onNavigateToLogin = {
                            authFlowScreen = AuthFlowScreen.Login
                        },
                        onRegisterClick = { username, email, password ->
                            runCatching {
                                val response = authApi.register(username, email, password)
                                response.message
                            }.getOrElse {
                                "Register error: ${it.message}"
                            }
                        }
                    )
                }
            }
        }

        RootScreen.Home -> {
            when (val currentMovieScreen = movieScreen) {
                is MovieScreen.Detail -> {
                    MovieDetailScreen(
                        movieId = currentMovieScreen.movieId,
                        onBackClick = {
                            movieScreen = lastListScreen
                        }
                    )
                }

                MovieScreen.Home,
                MovieScreen.Favorites,
                MovieScreen.Profile -> {
                    Scaffold(
                        topBar = {
                            TopAppBar(
                                colors = TopAppBarDefaults.topAppBarColors(
                                    containerColor = Color.Black,
                                    titleContentColor = Color.White,
                                    actionIconContentColor = Color.White,
                                    navigationIconContentColor = Color.White
                                ),
                                title = {
                                    if (movieScreen == MovieScreen.Home && searchVisible) {
                                        OutlinedTextField(
                                            value = searchQuery,
                                            onValueChange = { searchQuery = it },
                                            modifier = Modifier.fillMaxWidth(),
                                            singleLine = true,
                                            placeholder = {
                                                Text("Buscar película...")
                                            },
                                            colors = TextFieldDefaults.colors(
                                                focusedContainerColor = Color.Transparent,
                                                unfocusedContainerColor = Color.Transparent,
                                                focusedTextColor = Color.White,
                                                unfocusedTextColor = Color.White,
                                                focusedPlaceholderColor = Color.LightGray,
                                                unfocusedPlaceholderColor = Color.LightGray,
                                                focusedIndicatorColor = Color.White,
                                                unfocusedIndicatorColor = Color.LightGray,
                                                cursorColor = Color.White
                                            )
                                        )
                                    } else {
                                        Text(
                                            when (movieScreen) {
                                                MovieScreen.Home -> "MOVIEVERSE"
                                                MovieScreen.Favorites -> "FAVORITAS"
                                                MovieScreen.Profile -> "PERFIL"
                                                is MovieScreen.Detail -> ""
                                            }
                                        )
                                    }
                                },
                                actions = {
                                    if (movieScreen == MovieScreen.Home) {
                                        if (searchVisible) {
                                            IconButton(
                                                onClick = {
                                                    searchVisible = false
                                                    searchQuery = ""
                                                }
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Close,
                                                    contentDescription = "Cerrar búsqueda"
                                                )
                                            }
                                        } else {
                                            IconButton(
                                                onClick = {
                                                    searchVisible = true
                                                }
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Search,
                                                    contentDescription = "Buscar"
                                                )
                                            }
                                        }
                                    }
                                }
                            )
                        },
                        bottomBar = {
                            NavigationBar(
                                containerColor = Color.Black,
                                contentColor = Color.White
                            ) {
                                NavigationBarItem(
                                    selected = movieScreen == MovieScreen.Home,
                                    onClick = {
                                        movieScreen = MovieScreen.Home
                                        lastListScreen = MovieScreen.Home
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = Color.White,
                                        selectedTextColor = Color.White,
                                        unselectedIconColor = Color.LightGray,
                                        unselectedTextColor = Color.LightGray,
                                        indicatorColor = Color.DarkGray
                                    ),
                                    icon = {
                                        Icon(Icons.Default.Home, contentDescription = "Home")
                                    },
                                    label = { Text("Home") }
                                )

                                NavigationBarItem(
                                    selected = movieScreen == MovieScreen.Favorites,
                                    onClick = {
                                        movieScreen = MovieScreen.Favorites
                                        lastListScreen = MovieScreen.Favorites
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = Color.White,
                                        selectedTextColor = Color.White,
                                        unselectedIconColor = Color.LightGray,
                                        unselectedTextColor = Color.LightGray,
                                        indicatorColor = Color.DarkGray
                                    ),
                                    icon = {
                                        Icon(Icons.Default.Favorite, contentDescription = "Favoritos")
                                    },
                                    label = { Text("Favoritos") }
                                )

                                NavigationBarItem(
                                    selected = movieScreen == MovieScreen.Profile,
                                    onClick = {
                                        movieScreen = MovieScreen.Profile
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = Color.White,
                                        selectedTextColor = Color.White,
                                        unselectedIconColor = Color.LightGray,
                                        unselectedTextColor = Color.LightGray,
                                        indicatorColor = Color.DarkGray
                                    ),
                                    icon = {
                                        Icon(Icons.Default.AccountCircle, contentDescription = "Perfil")
                                    },
                                    label = { Text("Perfil") }
                                )
                            }
                        }
                    ) { innerPadding ->
                        when (movieScreen) {
                            MovieScreen.Home -> {
                                MoviesScreen(
                                    contentPadding = innerPadding,
                                    searchQuery = searchQuery,
                                    listState = homeListState,
                                    onMovieClick = { movieId ->
                                        lastListScreen = MovieScreen.Home
                                        movieScreen = MovieScreen.Detail(movieId)
                                    }
                                )
                            }

                            MovieScreen.Favorites -> {
                                FavoritesScreen(
                                    contentPadding = innerPadding,
                                    onMovieClick = { movieId ->
                                        lastListScreen = MovieScreen.Favorites
                                        movieScreen = MovieScreen.Detail(movieId)
                                    }
                                )
                            }

                            MovieScreen.Profile -> {
                                ProfileScreen(
                                    contentPadding = innerPadding,
                                    onLogoutClick = {
                                        sessionStorage.clearSession()
                                        authFlowScreen = AuthFlowScreen.Login
                                        movieScreen = MovieScreen.Home
                                        lastListScreen = MovieScreen.Home
                                        searchVisible = false
                                        searchQuery = ""
                                        rootScreen = RootScreen.Auth
                                    }
                                )
                            }

                            is MovieScreen.Detail -> Unit
                        }
                    }
                }
            }
        }
    }
}