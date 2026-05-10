package com.albertiacob91.movieversekmp.di

import com.albertiacob91.movieversekmp.data.local.SessionStorage
import com.albertiacob91.movieversekmp.data.remote.AuthApi
import com.albertiacob91.movieversekmp.data.repository.AuthRepositoryImpl
import com.albertiacob91.movieversekmp.data.repository.ForumRepositoryImpl
import com.albertiacob91.movieversekmp.data.repository.MovieRepositoryImpl
import com.albertiacob91.movieversekmp.domain.repository.AuthRepository
import com.albertiacob91.movieversekmp.domain.repository.ForumRepository
import com.albertiacob91.movieversekmp.domain.repository.MovieRepository
import com.albertiacob91.movieversekmp.domain.usecase.auth.GetCurrentUserUseCase
import com.albertiacob91.movieversekmp.domain.usecase.auth.LoginUseCase
import com.albertiacob91.movieversekmp.domain.usecase.auth.RegisterUseCase
import com.albertiacob91.movieversekmp.domain.usecase.auth.UploadAvatarUseCase
import com.albertiacob91.movieversekmp.domain.usecase.forum.CreateForumChatUseCase
import com.albertiacob91.movieversekmp.domain.usecase.forum.CreateForumMessageUseCase
import com.albertiacob91.movieversekmp.domain.usecase.forum.DeleteForumChatUseCase
import com.albertiacob91.movieversekmp.domain.usecase.forum.GetForumChatsUseCase
import com.albertiacob91.movieversekmp.domain.usecase.forum.GetForumMessagesUseCase
import com.albertiacob91.movieversekmp.domain.usecase.movies.CheckIsFavoriteUseCase
import com.albertiacob91.movieversekmp.domain.usecase.movies.GetCommentsUseCase
import com.albertiacob91.movieversekmp.domain.usecase.movies.GetFavoritesUseCase
import com.albertiacob91.movieversekmp.domain.usecase.movies.GetMovieDetailUseCase
import com.albertiacob91.movieversekmp.domain.usecase.movies.GetPopularMoviesUseCase
import com.albertiacob91.movieversekmp.domain.usecase.movies.PostCommentUseCase
import com.albertiacob91.movieversekmp.domain.usecase.movies.SearchMoviesUseCase
import com.albertiacob91.movieversekmp.domain.usecase.movies.ToggleFavoriteUseCase
import com.albertiacob91.movieversekmp.presentation.viewmodel.AuthViewModel
import com.albertiacob91.movieversekmp.presentation.viewmodel.FavoritesViewModel
import com.albertiacob91.movieversekmp.presentation.viewmodel.ForumChatDetailViewModel
import com.albertiacob91.movieversekmp.presentation.viewmodel.ForumViewModel
import com.albertiacob91.movieversekmp.presentation.viewmodel.MovieDetailViewModel
import com.albertiacob91.movieversekmp.presentation.viewmodel.MoviesViewModel
import com.albertiacob91.movieversekmp.presentation.viewmodel.ProfileViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val networkModule = module {
    single { AuthApi() }
    single { SessionStorage() }
}

val repositoryModule = module {
    single<MovieRepository> { MovieRepositoryImpl(get()) }
    single<AuthRepository> { AuthRepositoryImpl(get()) }
    single<ForumRepository> { ForumRepositoryImpl(get()) }
}

val useCaseModule = module {
    factory { GetPopularMoviesUseCase(get()) }
    factory { SearchMoviesUseCase(get()) }
    factory { GetMovieDetailUseCase(get()) }
    factory { GetFavoritesUseCase(get()) }
    factory { ToggleFavoriteUseCase(get()) }
    factory { CheckIsFavoriteUseCase(get()) }
    factory { GetCommentsUseCase(get()) }
    factory { PostCommentUseCase(get()) }
    factory { GetForumChatsUseCase(get()) }
    factory { CreateForumChatUseCase(get()) }
    factory { DeleteForumChatUseCase(get()) }
    factory { GetForumMessagesUseCase(get()) }
    factory { CreateForumMessageUseCase(get()) }
    factory { LoginUseCase(get(), get()) }
    factory { RegisterUseCase(get()) }
    factory { GetCurrentUserUseCase(get(), get()) }
    factory { UploadAvatarUseCase(get(), get()) }
}

val viewModelModule = module {
    viewModelOf(::MoviesViewModel)
    viewModelOf(::MovieDetailViewModel)
    viewModelOf(::FavoritesViewModel)
    viewModelOf(::ForumViewModel)
    viewModelOf(::ForumChatDetailViewModel)
    viewModelOf(::ProfileViewModel)
    viewModelOf(::AuthViewModel)
}

val appModules = listOf(networkModule, repositoryModule, useCaseModule, viewModelModule)
