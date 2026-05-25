package com.example.task5.usecase

import com.example.task5.domain.model.Prize
import com.example.task5.domain.repository.FavoriteRepository
import com.example.task5.domain.repository.PrizeRepository

class FavoriteUseCase(
    private val favoriteRepository: FavoriteRepository,
    private val prizeRepository: PrizeRepository,
) {
    suspend fun listFavorites(userId: Int): List<Prize> = favoriteRepository.listFavoritePrizes(userId)

    suspend fun addFavorite(userId: Int, prizeId: Int): Boolean {
        if (prizeRepository.findPrizeById(prizeId) == null) return false
        favoriteRepository.addFavorite(userId, prizeId)
        return true
    }

    suspend fun removeFavorite(userId: Int, prizeId: Int) {
        favoriteRepository.removeFavorite(userId, prizeId)
    }
}
