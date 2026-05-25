package com.example.task5.data.db

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.ApplicationEnvironment
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

class DatabaseFactory(
    private val environment: ApplicationEnvironment,
) {
    fun init() {
        val dbUrl = System.getenv("DB_URL") ?: error("DB_URL environment variable is required")
        val dbUser = System.getenv("DB_USER") ?: error("DB_USER environment variable is required")
        val dbPassword = System.getenv("DB_PASSWORD") ?: error("DB_PASSWORD environment variable is required")

        val hikariConfig = HikariConfig().apply {
            jdbcUrl = dbUrl
            username = dbUser
            password = dbPassword
            driverClassName = "org.postgresql.Driver"
            maximumPoolSize = 5
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        }

        val dataSource = HikariDataSource(hikariConfig)
        Database.connect(dataSource)
        environment.log.info("Connected to database")

        transaction {
            SchemaUtils.create(UsersTable, PrizesTable, LaureatesTable, UserPrizesTable)
        }
    }
}
