package com.example.task5.usecase

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.example.task5.domain.model.User
import com.example.task5.domain.repository.UserRepository
import io.ktor.server.application.ApplicationEnvironment
import org.mindrot.jbcrypt.BCrypt
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.Date

data class LoginResult(
    val token: String,
    val expiresInSeconds: Long,
)

class AuthUseCase(
    private val userRepository: UserRepository,
    environment: ApplicationEnvironment,
) {
    private val secret = (System.getenv("JWT_SECRET") ?: "change-me-please-at-least-32-chars-long")
        .also { require(it.length >= 32) { "JWT secret length must be >= 32" } }
    private val issuer = "task5-module6-server"
    private val audience = "task5-module6-client"
    private val ttlSeconds = 30 * 60L

    private val algorithm = Algorithm.HMAC256(secret)
    private val verifier = JWT.require(algorithm)
        .withIssuer(issuer)
        .withAudience(audience)
        .build()

    suspend fun ensureDefaultAdminUser() {
        if (userRepository.findByUsername("admin") == null) {
            val hash = BCrypt.hashpw("admin", BCrypt.gensalt())
            userRepository.createUser("admin", hash, "admin")
        }
    }

    suspend fun login(username: String, password: String): LoginResult? {
        val user = userRepository.findByUsername(username) ?: return null
        if (!BCrypt.checkpw(password, user.passwordHash)) {
            return null
        }

        return LoginResult(
            token = generateToken(user),
            expiresInSeconds = ttlSeconds,
        )
    }

    suspend fun getUser(userId: Int): User? = userRepository.findById(userId)

    fun verifier(): JWTVerifier = verifier

    private fun generateToken(user: User): String {
        val expiresAt = Instant.now().plus(ttlSeconds, ChronoUnit.SECONDS)

        return JWT.create()
            .withIssuer(issuer)
            .withAudience(audience)
            .withClaim("userId", user.id)
            .withSubject(user.username)
            .withClaim("role", user.role)
            .withExpiresAt(Date.from(expiresAt))
            .sign(algorithm)
    }
}
