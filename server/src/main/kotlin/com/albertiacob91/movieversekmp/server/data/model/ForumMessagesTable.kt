package com.albertiacob91.movieversekmp.server.data.model

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp

object ForumMessagesTable : Table("forum_messages") {
    val id = uuid("id")
    val chatId = uuid("chat_id").references(ForumChatsTable.id)
    val userId = uuid("user_id").references(UsersTable.id)
    val content = text("content")
    val createdAt = timestamp("created_at")

    override val primaryKey = PrimaryKey(id)
}