package com.example.task5

import com.example.task5.data.db.DatabaseFactory
import com.example.task5.data.external.NobelApiClient
import com.example.task5.data.repository.ExposedFavoriteRepository
import com.example.task5.data.repository.ExposedPrizeRepository
import com.example.task5.data.repository.ExposedUserRepository
import com.example.task5.plugins.configureLogging
import com.example.task5.plugins.configureRouting
import com.example.task5.plugins.configureSecurity
import com.example.task5.plugins.configureSerialization
import com.example.task5.usecase.AuthUseCase
import com.example.task5.usecase.FavoriteUseCase
import com.example.task5.usecase.PrizeUseCase
import io.ktor.server.application.Application
import kotlinx.coroutines.runBlocking

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    val databaseFactory = DatabaseFactory(environment)
    databaseFactory.init()

    val userRepository = ExposedUserRepository()
    val prizeRepository = ExposedPrizeRepository()
    val favoriteRepository = ExposedFavoriteRepository()
    val authUseCase = AuthUseCase(userRepository, environment)
    val prizeUseCase = PrizeUseCase(prizeRepository, NobelApiClient())
    val favoriteUseCase = FavoriteUseCase(favoriteRepository, prizeRepository)

    runBlocking {
        authUseCase.ensureDefaultAdminUser()
    }

    configureLogging()
    configureSerialization()
    configureSecurity(authUseCase)
    configureRouting(authUseCase, prizeUseCase, favoriteUseCase)
}
