package com.example.task5.plugins

import com.example.task5.usecase.AuthUseCase
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.jwt.jwt

fun Application.configureSecurity(authUseCase: AuthUseCase) {
    install(Authentication) {
        jwt("auth-jwt") {
            realm = "Task5 API"
            verifier(authUseCase.verifier())
            validate { credential ->
                if (credential.payload.getClaim("userId").asInt() != null) {
                    io.ktor.server.auth.jwt.JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
        }
    }
}
