package com.example.task5.presentation.dto

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequestDto(
    val username: String,
    val password: String,
)

@Serializable
data class LoginResponseDto(
    val token: String,
    val expiresInSeconds: Long,
)

@Serializable
data class UserMeDto(
    val id: Int,
    val username: String,
    val role: String,
)

@Serializable
data class PrizeDto(
    val id: Int,
    val awardYear: Int,
    val category: String,
    val fullName: String?,
    val motivation: String?,
    val detailLink: String?,
)

@Serializable
data class MessageDto(
    val message: String,
)
