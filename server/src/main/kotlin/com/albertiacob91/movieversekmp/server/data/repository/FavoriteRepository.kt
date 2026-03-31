package com.albertiacob91.movieversekmp.server.data.repository

import com.albertiacob91.movieversekmp.server.data.model.FavoritesTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant
import java.util.UUID

class FavoriteRepository {

    fun getFavoritesByUser(userId: UUID): List<FavoriteRecord> = transaction {
        FavoritesTable.selectAll()
            .where { FavoritesTable.userId eq userId }
            .map {
                FavoriteRecord(
                    id = it[FavoritesTable.id].toString(),
                    userId = it[FavoritesTable.userId].toString(),
                    movieId = it[FavoritesTable.movieId]
                )
            }
    }

    fun exists(userId: UUID, movieId: Int): Boolean = transaction {
        FavoritesTable.selectAll()
            .where {
                (FavoritesTable.userId eq userId) and
                        (FavoritesTable.movieId eq movieId)
            }
            .count() > 0
    }

    fun addFavorite(userId: UUID, movieId: Int): UUID {
        val favoriteId = UUID.randomUUID()
        val now = Instant.now()

        transaction {
            FavoritesTable.insert {
                it[id] = favoriteId
                it[FavoritesTable.userId] = userId
                it[FavoritesTable.movieId] = movieId
                it[createdAt] = now
            }
        }

        return favoriteId
    }

    fun deleteFavorite(userId: UUID, movieId: Int): Boolean = transaction {
        FavoritesTable.deleteWhere {
            (FavoritesTable.userId eq userId) and
                    (FavoritesTable.movieId eq movieId)
        } > 0
    }
}

data class FavoriteRecord(
    val id: String,
    val userId: String,
    val movieId: Int
)