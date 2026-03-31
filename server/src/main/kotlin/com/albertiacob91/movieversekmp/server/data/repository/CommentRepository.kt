package com.albertiacob91.movieversekmp.server.data.repository

import com.albertiacob91.movieversekmp.server.data.model.CommentsTable
import com.albertiacob91.movieversekmp.server.data.model.UsersTable
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant
import java.util.UUID

class CommentRepository {

    fun getCommentsByMovie(movieId: Int): List<CommentRecord> = transaction {
        CommentsTable
            .join(
                UsersTable,
                JoinType.INNER,
                onColumn = CommentsTable.userId,
                otherColumn = UsersTable.id
            )
            .selectAll()
            .where { CommentsTable.movieId eq movieId }
            .map {
                CommentRecord(
                    id = it[CommentsTable.id].toString(),
                    movieId = it[CommentsTable.movieId],
                    userId = it[CommentsTable.userId].toString(),
                    username = it[UsersTable.username],
                    content = it[CommentsTable.content]
                )
            }
    }

    fun addComment(userId: UUID, movieId: Int, content: String): UUID {
        val commentId = UUID.randomUUID()
        val now = Instant.now()

        transaction {
            CommentsTable.insert {
                it[id] = commentId
                it[CommentsTable.userId] = userId
                it[CommentsTable.movieId] = movieId
                it[CommentsTable.content] = content
                it[createdAt] = now
                it[updatedAt] = now
            }
        }

        return commentId
    }
}

data class CommentRecord(
    val id: String,
    val movieId: Int,
    val userId: String,
    val username: String,
    val content: String
)