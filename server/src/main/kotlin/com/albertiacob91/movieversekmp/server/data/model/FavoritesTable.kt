package com.albertiacob91.movieversekmp.server.data.model

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp

object FavoritesTable : Table("favorites") {
    val id = uuid("id")
    val userId = uuid("user_id").references(UsersTable.id)
    val movieId = integer("movie_id")
    val createdAt = timestamp("created_at")

    override val primaryKey = PrimaryKey(id)
}