package com.example.task5.domain.model

data class User(
    val id: Int,
    val username: String,
    val passwordHash: String,
    val role: String,
)

data class Prize(
    val id: Int,
    val awardYear: Int,
    val category: String,
    val fullName: String?,
    val motivation: String?,
    val detailLink: String?,
)

data class Laureate(
    val id: Int,
    val prizeId: Int,
    val fullName: String,
    val portion: String?,
    val motivation: String?,
    val portraitUrl: String?,
)

data class NewPrize(
    val awardYear: Int,
    val category: String,
    val fullName: String?,
    val motivation: String?,
    val detailLink: String?,
    val laureates: List<NewLaureate>,
)

data class NewLaureate(
    val fullName: String,
    val portion: String?,
    val motivation: String?,
    val portraitUrl: String?,
)
