package com.example.task5.domain.repository

import com.example.task5.domain.model.NewPrize
import com.example.task5.domain.model.Prize
import com.example.task5.domain.model.User

interface UserRepository {
    suspend fun findByUsername(username: String): User?
    suspend fun findById(id: Int): User?
    suspend fun createUser(username: String, passwordHash: String, role: String): User
}

interface PrizeRepository {
    suspend fun count(): Long
    suspend fun listPrizes(year: Int?, category: String?, limit: Int, offset: Int): List<Prize>
    suspend fun findPrizeById(prizeId: Int): Prize?
    suspend fun saveAll(prizes: List<NewPrize>)
}

interface FavoriteRepository {
    suspend fun listFavoritePrizes(userId: Int): List<Prize>
    suspend fun addFavorite(userId: Int, prizeId: Int)
    suspend fun removeFavorite(userId: Int, prizeId: Int)
}
