package com.example.task5.data.db

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

object UsersTable : IntIdTable("users") {
    val username = varchar("username", 100).uniqueIndex()
    val passwordHash = varchar("password_hash", 255)
    val role = varchar("role", 50)
}

object PrizesTable : IntIdTable("prizes") {
    val awardYear = integer("award_year")
    val category = varchar("category", 150)
    val fullName = text("full_name").nullable()
    val motivation = text("motivation").nullable()
    val detailLink = text("detail_link").nullable()
}

object LaureatesTable : IntIdTable("laureates") {
    val prizeId = reference("prize_id", PrizesTable)
    val fullName = text("full_name")
    val portion = varchar("portion", 20).nullable()
    val motivation = text("motivation").nullable()
    val portraitUrl = text("portrait_url").nullable()
}

object UserPrizesTable : Table("user_prizes") {
    val userId = reference("user_id", UsersTable)
    val prizeId = reference("prize_id", PrizesTable)
    val addedAt = datetime("added_at")

    init {
        uniqueIndex(userId, prizeId)
    }
}
