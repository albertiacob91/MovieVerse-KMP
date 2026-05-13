package com.albertiacob91.movieversekmp.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.albertiacob91.movieversekmp.presentation.components.LoadingScreen
import com.albertiacob91.movieversekmp.presentation.screens.auth.LoginScreen
import com.albertiacob91.movieversekmp.presentation.screens.auth.RegisterScreen
import com.albertiacob91.movieversekmp.presentation.screens.forum.ForumChatDetailScreen
import com.albertiacob91.movieversekmp.presentation.screens.forum.ForumScreen
import com.albertiacob91.movieversekmp.presentation.screens.movies.FavoritesScreen
import com.albertiacob91.movieversekmp.presentation.screens.movies.MovieDetailScreen
import com.albertiacob91.movieversekmp.presentation.screens.movies.MoviesScreen
import com.albertiacob91.movieversekmp.presentation.screens.profile.ProfileScreen
import com.albertiacob91.movieversekmp.presentation.viewmodel.AuthViewModel
import org.koin.compose.viewmodel.koinViewModel

private enum class AuthFlowScreen { Login, Register }

private val MovieScreenSaver = Saver<MovieScreen, String>(
    save = { screen ->
        when (screen) {
            MovieScreen.Home -> "Home"
            MovieScreen.Favorites -> "Favorites"
            MovieScreen.Forum -> "Forum"
            MovieScreen.Profile -> "Profile"
            is MovieScreen.Detail -> "Detail:${screen.movieId}"
            is MovieScreen.ForumChatDetail -> "ForumChat:${screen.chatId}:${screen.title}"
        }
    },
    restore = { saved ->
        when {
            saved == "Home" -> MovieScreen.Home
            saved == "Favorites" -> MovieScreen.Favorites
            saved == "Forum" -> MovieScreen.Forum
            saved == "Profile" -> MovieScreen.Profile
            saved.startsWith("Detail:") -> MovieScreen.Detail(saved.removePrefix("Detail:").toInt())
            saved.startsWith("ForumChat:") -> {
                val rest = saved.removePrefix("ForumChat:")
                val colon = rest.indexOf(':')
                MovieScreen.ForumChatDetail(rest.substring(0, colon), rest.substring(colon + 1))
            }
            else -> MovieScreen.Home
        }
    }
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieVerseNavigation(
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit
) {
    val authViewModel: AuthViewModel = koinViewModel()
    val authState by authViewModel.state.collectAsStateWithLifecycle()

    var authFlowScreen by remember { mutableStateOf(AuthFlowScreen.Login) }
    var movieScreen by rememberSaveable(stateSaver = MovieScreenSaver) { mutableStateOf<MovieScreen>(MovieScreen.Home) }
    var lastListScreen by rememberSaveable(stateSaver = MovieScreenSaver) { mutableStateOf<MovieScreen>(MovieScreen.Home) }
    var searchVisible by rememberSaveable { mutableStateOf(false) }
    var searchQuery by rememberSaveable { mutableStateOf("") }
    val homeListState = rememberLazyGridState()

    LaunchedEffect(Unit) {
        authViewModel.checkSession()
    }

    val backEnabled = when {
        !authState.isLoggedIn && authFlowScreen == AuthFlowScreen.Register -> true
        authState.isLoggedIn && movieScreen is MovieScreen.Detail -> true
        authState.isLoggedIn && movieScreen is MovieScreen.ForumChatDetail -> true
        authState.isLoggedIn && searchVisible && movieScreen == MovieScreen.Home -> true
        else -> false
    }
    BackHandler(enabled = backEnabled) {
        when {
            !authState.isLoggedIn && authFlowScreen == AuthFlowScreen.Register ->
                authFlowScreen = AuthFlowScreen.Login
            authState.isLoggedIn && movieScreen is MovieScreen.Detail ->
                movieScreen = lastListScreen
            authState.isLoggedIn && movieScreen is MovieScreen.ForumChatDetail ->
                movieScreen = MovieScreen.Forum
            authState.isLoggedIn && searchVisible -> {
                searchVisible = false
                searchQuery = ""
            }
        }
    }

    when {
        authState.isCheckingSession -> {
            LoadingScreen()
        }

        !authState.isLoggedIn -> {
            when (authFlowScreen) {
                AuthFlowScreen.Login -> {
                    LoginScreen(
                        isDarkTheme = isDarkTheme,
                        onThemeToggle = onThemeToggle,
                        onNavigateToRegister = { authFlowScreen = AuthFlowScreen.Register },
                        onLoginClick = { email, password ->
                            authViewModel.login(email, password)
                        }
                    )
                }
                AuthFlowScreen.Register -> {
                    RegisterScreen(
                        isDarkTheme = isDarkTheme,
                        onThemeToggle = onThemeToggle,
                        onNavigateToLogin = { authFlowScreen = AuthFlowScreen.Login },
                        onRegisterClick = { username, email, password ->
                            authViewModel.register(username, email, password)
                        }
                    )
                }
            }
        }

        else -> {
            when (val currentMovieScreen = movieScreen) {
                is MovieScreen.Detail -> {
                    MovieDetailScreen(
                        movieId = currentMovieScreen.movieId,
                        onBackClick = { movieScreen = lastListScreen }
                    )
                }

                is MovieScreen.ForumChatDetail -> {
                    ForumChatDetailScreen(
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(),
                        chatId = currentMovieScreen.chatId,
                        title = currentMovieScreen.title,
                        onBackClick = { movieScreen = MovieScreen.Forum }
                    )
                }

                MovieScreen.Home,
                MovieScreen.Favorites,
                MovieScreen.Forum,
                MovieScreen.Profile -> {
                    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
                    var isScrollingDown by remember { mutableStateOf(false) }
                    val bottomScrollConnection = remember {
                        object : NestedScrollConnection {
                            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                                if (available.y < -5f) isScrollingDown = true
                                else if (available.y > 5f) isScrollingDown = false
                                return Offset.Zero
                            }
                        }
                    }
                    LaunchedEffect(movieScreen) {
                        isScrollingDown = false
                        scrollBehavior.state.heightOffset = 0f
                    }

                    Scaffold(
                        modifier = Modifier
                            .nestedScroll(scrollBehavior.nestedScrollConnection)
                            .nestedScroll(bottomScrollConnection),
                        topBar = {
                            TopAppBar(
                                scrollBehavior = scrollBehavior,
                                colors = TopAppBarDefaults.topAppBarColors(
                                    containerColor = MaterialTheme.colorScheme.surface,
                                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                                    actionIconContentColor = MaterialTheme.colorScheme.onSurface,
                                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                                ),
                                title = {
                                    if (movieScreen == MovieScreen.Home && searchVisible) {
                                        OutlinedTextField(
                                            value = searchQuery,
                                            onValueChange = { searchQuery = it },
                                            modifier = Modifier.fillMaxWidth(),
                                            singleLine = true,
                                            placeholder = { Text("Buscar película...") },
                                            colors = TextFieldDefaults.colors(
                                                focusedContainerColor = androidx.compose.ui.graphics.Color.Transparent,
                                                unfocusedContainerColor = androidx.compose.ui.graphics.Color.Transparent,
                                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                                focusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                                unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                                                unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                                cursorColor = MaterialTheme.colorScheme.primary
                                            )
                                        )
                                    } else {
                                        Text(
                                            text = when (movieScreen) {
                                                MovieScreen.Home -> "MOVIEVERSE"
                                                MovieScreen.Favorites -> "FAVORITAS"
                                                MovieScreen.Forum -> "FORO"
                                                MovieScreen.Profile -> "PERFIL"
                                                else -> ""
                                            },
                                            style = MaterialTheme.typography.titleLarge
                                        )
                                    }
                                },
                                actions = {
                                    if (movieScreen == MovieScreen.Home) {
                                        if (searchVisible) {
                                            IconButton(onClick = { searchVisible = false; searchQuery = "" }) {
                                                Icon(imageVector = Icons.Default.Close, contentDescription = "Cerrar búsqueda")
                                            }
                                        } else {
                                            IconButton(onClick = { searchVisible = true }) {
                                                Icon(imageVector = Icons.Default.Search, contentDescription = "Buscar")
                                            }
                                        }
                                    }
                                    IconButton(onClick = onThemeToggle) {
                                        Icon(
                                            imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                                            contentDescription = if (isDarkTheme) "Cambiar a modo claro" else "Cambiar a modo oscuro"
                                        )
                                    }
                                }
                            )
                        },
                        bottomBar = {
                            AnimatedVisibility(
                                visible = !isScrollingDown,
                                enter = slideInVertically(initialOffsetY = { it }),
                                exit = slideOutVertically(targetOffsetY = { it })
                            ) {
                            NavigationBar(
                                containerColor = MaterialTheme.colorScheme.surface,
                                contentColor = MaterialTheme.colorScheme.onSurface
                            ) {
                                val itemColors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MaterialTheme.colorScheme.primary,
                                    selectedTextColor = MaterialTheme.colorScheme.primary,
                                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                                )
                                NavigationBarItem(
                                    selected = movieScreen == MovieScreen.Home,
                                    onClick = { movieScreen = MovieScreen.Home; lastListScreen = MovieScreen.Home },
                                    colors = itemColors,
                                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                                    label = { Text("Home") }
                                )
                                NavigationBarItem(
                                    selected = movieScreen == MovieScreen.Favorites,
                                    onClick = { movieScreen = MovieScreen.Favorites; lastListScreen = MovieScreen.Favorites },
                                    colors = itemColors,
                                    icon = { Icon(Icons.Default.Favorite, contentDescription = "Favoritos") },
                                    label = { Text("Favoritos") }
                                )
                                NavigationBarItem(
                                    selected = movieScreen == MovieScreen.Forum,
                                    onClick = { movieScreen = MovieScreen.Forum },
                                    colors = itemColors,
                                    icon = { Icon(Icons.Default.Forum, contentDescription = "Foro") },
                                    label = { Text("Foro") }
                                )
                                NavigationBarItem(
                                    selected = movieScreen == MovieScreen.Profile,
                                    onClick = { movieScreen = MovieScreen.Profile },
                                    colors = itemColors,
                                    icon = { Icon(Icons.Default.AccountCircle, contentDescription = "Perfil") },
                                    label = { Text("Perfil") }
                                )
                            }
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
                            MovieScreen.Forum -> {
                                ForumScreen(
                                    contentPadding = innerPadding,
                                    onChatClick = { chat ->
                                        lastListScreen = MovieScreen.Forum
                                        movieScreen = MovieScreen.ForumChatDetail(chatId = chat.id, title = chat.title)
                                    }
                                )
                            }
                            MovieScreen.Profile -> {
                                ProfileScreen(
                                    contentPadding = innerPadding,
                                    onLogoutClick = {
                                        authViewModel.logout()
                                        movieScreen = MovieScreen.Home
                                        lastListScreen = MovieScreen.Home
                                        searchVisible = false
                                        searchQuery = ""
                                        authFlowScreen = AuthFlowScreen.Login
                                    }
                                )
                            }
                            is MovieScreen.Detail -> Unit
                            is MovieScreen.ForumChatDetail -> Unit
                        }
                    }
                }
            }
        }
    }
}
