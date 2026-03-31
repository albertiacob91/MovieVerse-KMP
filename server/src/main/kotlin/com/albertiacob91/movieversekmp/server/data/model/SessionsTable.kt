package com.albertiacob91.movieversekmp.server.data.model

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp

object SessionsTable : Table("sessions") {
    val id = uuid("id")
    val userId = uuid("user_id").references(UsersTable.id)
    val token = text("token").uniqueIndex()
    val expiresAt = timestamp("expires_at")
    val createdAt = timestamp("created_at")

    override val primaryKey = PrimaryKey(id)
}