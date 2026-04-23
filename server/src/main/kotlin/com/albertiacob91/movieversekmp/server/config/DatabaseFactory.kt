package com.albertiacob91.movieversekmp.server.config

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database

object DatabaseFactory {

    fun init() {
        val dataSource = hikari()
        Database.connect(dataSource)
    }

    private fun hikari(): HikariDataSource {
        val rawUrl = System.getenv("JDBC_DATABASE_URL")
            ?: "jdbc:postgresql://localhost:5432/movieverse_db"

        val jdbcUrl = when {
            rawUrl.startsWith("jdbc:postgresql://") -> rawUrl
            rawUrl.startsWith("postgresql://") -> "jdbc:$rawUrl"
            rawUrl.startsWith("postgres://") -> "jdbc:postgresql://${rawUrl.removePrefix("postgres://")}"
            else -> rawUrl
        }

        val dbUser = System.getenv("DB_USER")
            ?: "movieverse_user"

        val dbPassword = System.getenv("DB_PASSWORD")
            ?: "123456"

        val config = HikariConfig().apply {
            driverClassName = "org.postgresql.Driver"
            this.jdbcUrl = jdbcUrl
            username = dbUser.trim()
            password = dbPassword.trim()
            maximumPoolSize = 10
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        }

        return HikariDataSource(config)
    }
}