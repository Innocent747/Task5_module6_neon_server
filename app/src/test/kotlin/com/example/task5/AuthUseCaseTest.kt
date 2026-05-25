package com.example.task5

import com.example.task5.domain.model.User
import com.example.task5.domain.repository.UserRepository
import com.example.task5.usecase.AuthUseCase
import io.ktor.server.config.MapApplicationConfig
import io.ktor.server.engine.CommandLineConfig
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class AuthUseCaseTest {
    @Test
    fun `login returns token for valid credentials`() = runBlocking {
        val repository = InMemoryUserRepository()
        val hash = org.mindrot.jbcrypt.BCrypt.hashpw("admin", org.mindrot.jbcrypt.BCrypt.gensalt())
        repository.createUser("admin", hash, "admin")

        val authUseCase = AuthUseCase(repository, CommandLineConfig(arrayOf()).environment)
        val result = authUseCase.login("admin", "admin")

        assertNotNull(result)
        assertEquals(1800, result?.expiresInSeconds)
    }

    @Test
    fun `login fails for wrong password`() = runBlocking {
        val repository = InMemoryUserRepository()
        val hash = org.mindrot.jbcrypt.BCrypt.hashpw("admin", org.mindrot.jbcrypt.BCrypt.gensalt())
        repository.createUser("admin", hash, "admin")

        val authUseCase = AuthUseCase(repository, CommandLineConfig(arrayOf()).environment)
        val result = authUseCase.login("admin", "wrong")

        assertNull(result)
    }
}

private class InMemoryUserRepository : UserRepository {
    private val users = mutableListOf<User>()

    override suspend fun findByUsername(username: String): User? = users.firstOrNull { it.username == username }

    override suspend fun findById(id: Int): User? = users.firstOrNull { it.id == id }

    override suspend fun createUser(username: String, passwordHash: String, role: String): User {
        val user = User(users.size + 1, username, passwordHash, role)
        users += user
        return user
    }
}
