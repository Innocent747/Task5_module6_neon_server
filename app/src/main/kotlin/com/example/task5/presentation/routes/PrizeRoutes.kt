package com.example.task5.presentation.routes

import com.example.task5.presentation.dto.PrizeDto
import com.example.task5.presentation.dto.MessageDto
import com.example.task5.usecase.PrizeSeedException
import com.example.task5.usecase.PrizeUseCase
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get

fun Route.prizeRoutes(prizeUseCase: PrizeUseCase) {
    get("/prizes") {
        val year = call.request.queryParameters["year"]?.toIntOrNull()
        val category = call.request.queryParameters["category"]
        val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 50
        val offset = call.request.queryParameters["offset"]?.toIntOrNull() ?: 0

        if (limit <= 0 || offset < 0) {
            call.respond(HttpStatusCode.BadRequest)
            return@get
        }

        val prizes = runCatching { prizeUseCase.getPrizes(year, category, limit, offset) }
            .getOrElse { error ->
                if (error is PrizeSeedException) {
                    call.respond(HttpStatusCode.ServiceUnavailable, MessageDto(error.message ?: "Nobel API is unavailable"))
                    return@get
                }
                throw error
            }
            .map {
                PrizeDto(
                    id = it.id,
                    awardYear = it.awardYear,
                    category = it.category,
                    fullName = it.fullName,
                    motivation = it.motivation,
                    detailLink = it.detailLink,
                )
            }

        call.respond(HttpStatusCode.OK, prizes)
    }
}
