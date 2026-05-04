package com.albertiacob91.movieversekmp.server.data.model

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp

object ForumChatsTable : Table("forum_chats") {
    val id = uuid("id")
    val title = varchar("title", 150)
    val userId = uuid("user_id").references(UsersTable.id)
    val createdAt = timestamp("created_at")

    override val primaryKey = PrimaryKey(id)
}