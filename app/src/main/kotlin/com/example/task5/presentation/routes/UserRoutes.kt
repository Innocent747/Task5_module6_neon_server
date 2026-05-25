package com.example.task5.presentation.routes

import com.auth0.jwt.interfaces.Claim
import com.example.task5.presentation.dto.MessageDto
import com.example.task5.presentation.dto.PrizeDto
import com.example.task5.presentation.dto.UserMeDto
import com.example.task5.usecase.AuthUseCase
import com.example.task5.usecase.FavoriteUseCase
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.userRoutes(
    authUseCase: AuthUseCase,
    favoriteUseCase: FavoriteUseCase,
) {
    authenticate("auth-jwt") {
        route("/users/me") {
            get {
                val userId = call.currentUserId() ?: run {
                    call.respond(HttpStatusCode.Unauthorized)
                    return@get
                }

                val user = authUseCase.getUser(userId) ?: run {
                    call.respond(HttpStatusCode.Unauthorized)
                    return@get
                }

                call.respond(
                    HttpStatusCode.OK,
                    UserMeDto(
                        id = user.id,
                        username = user.username,
                        role = user.role,
                    ),
                )
            }

            route("/prizes") {
                get {
                    val userId = call.currentUserId() ?: run {
                        call.respond(HttpStatusCode.Unauthorized)
                        return@get
                    }

                    val favorites = favoriteUseCase.listFavorites(userId)
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

                    call.respond(HttpStatusCode.OK, favorites)
                }

                post("/{prizeId}") {
                    val userId = call.currentUserId() ?: run {
                        call.respond(HttpStatusCode.Unauthorized)
                        return@post
                    }
                    val prizeId = call.parameters["prizeId"]?.toIntOrNull() ?: run {
                        call.respond(HttpStatusCode.BadRequest)
                        return@post
                    }

                    val added = favoriteUseCase.addFavorite(userId, prizeId)
                    if (!added) {
                        call.respond(HttpStatusCode.NotFound, MessageDto("Prize not found"))
                        return@post
                    }

                    call.respond(HttpStatusCode.OK, MessageDto("Favorite added"))
                }

                delete("/{prizeId}") {
                    val userId = call.currentUserId() ?: run {
                        call.respond(HttpStatusCode.Unauthorized)
                        return@delete
                    }
                    val prizeId = call.parameters["prizeId"]?.toIntOrNull() ?: run {
                        call.respond(HttpStatusCode.BadRequest)
                        return@delete
                    }

                    favoriteUseCase.removeFavorite(userId, prizeId)
                    call.respond(HttpStatusCode.OK, MessageDto("Favorite removed"))
                }
            }
        }
    }
}

private fun io.ktor.server.application.ApplicationCall.currentUserId(): Int? {
    val principal = principal<JWTPrincipal>() ?: return null
    return principal.payload.getClaim("userId").asInt()
}
