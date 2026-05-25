package com.example.task5.presentation.routes

import com.example.task5.presentation.dto.LoginRequestDto
import com.example.task5.presentation.dto.LoginResponseDto
import com.example.task5.usecase.AuthUseCase
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post

fun Route.authRoutes(authUseCase: AuthUseCase) {
    post("/login") {
        val request = runCatching { call.receive<LoginRequestDto>() }.getOrNull()
        if (request == null || request.username.isBlank() || request.password.isBlank()) {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        val result = authUseCase.login(request.username, request.password)
        if (result == null) {
            call.respond(HttpStatusCode.Unauthorized)
            return@post
        }

        call.respond(
            HttpStatusCode.OK,
            LoginResponseDto(
                token = result.token,
                expiresInSeconds = result.expiresInSeconds,
            ),
        )
    }
}
