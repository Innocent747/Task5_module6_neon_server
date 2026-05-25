package com.example.task5.data.repository

import com.example.task5.data.db.UsersTable
import com.example.task5.domain.model.User
import com.example.task5.domain.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class ExposedUserRepository : UserRepository {
    override suspend fun findByUsername(username: String): User? = dbQuery {
        UsersTable.selectAll()
            .where { UsersTable.username eq username }
            .singleOrNull()
            ?.toUser()
    }

    override suspend fun findById(id: Int): User? = dbQuery {
        UsersTable.selectAll()
            .where { UsersTable.id eq id }
            .singleOrNull()
            ?.toUser()
    }

    override suspend fun createUser(username: String, passwordHash: String, role: String): User = dbQuery {
        val inserted = UsersTable.insert {
            it[UsersTable.username] = username
            it[UsersTable.passwordHash] = passwordHash
            it[UsersTable.role] = role
        }.resultedValues?.single() ?: error("Unable to create user")

        inserted.toUser()
    }

    private fun ResultRow.toUser(): User = User(
        id = this[UsersTable.id].value,
        username = this[UsersTable.username],
        passwordHash = this[UsersTable.passwordHash],
        role = this[UsersTable.role],
    )
}

suspend fun <T> dbQuery(block: suspend () -> T): T =
    newSuspendedTransaction(Dispatchers.IO) { block() }
