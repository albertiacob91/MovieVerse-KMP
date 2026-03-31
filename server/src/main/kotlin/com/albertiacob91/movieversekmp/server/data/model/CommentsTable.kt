package com.albertiacob91.movieversekmp.server.data.model

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp

object CommentsTable : Table("comments") {
    val id = uuid("id")
    val userId = uuid("user_id").references(UsersTable.id)
    val movieId = integer("movie_id")
    val content = text("content")
    val createdAt = timestamp("created_at")
    val updatedAt = timestamp("updated_at")

    override val primaryKey = PrimaryKey(id)
}