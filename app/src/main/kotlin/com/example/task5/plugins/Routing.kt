package com.example.task5.plugins

import com.example.task5.presentation.routes.authRoutes
import com.example.task5.presentation.routes.prizeRoutes
import com.example.task5.presentation.routes.userRoutes
import com.example.task5.usecase.AuthUseCase
import com.example.task5.usecase.FavoriteUseCase
import com.example.task5.usecase.PrizeUseCase
import io.ktor.server.application.Application
import io.ktor.server.plugins.openapi.openAPI
import io.ktor.server.plugins.swagger.swaggerUI
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing

fun Application.configureRouting(
    authUseCase: AuthUseCase,
    prizeUseCase: PrizeUseCase,
    favoriteUseCase: FavoriteUseCase,
) {
    routing {
        get("/") {
            call.respondText("Nobel Prize API is running")
        }

        openAPI(path = "openapi", swaggerFile = "openapi/documentation.yaml")
        swaggerUI(path = "swagger", swaggerFile = "openapi/documentation.yaml")

        authRoutes(authUseCase)
        prizeRoutes(prizeUseCase)
        userRoutes(authUseCase, favoriteUseCase)
    }
}
