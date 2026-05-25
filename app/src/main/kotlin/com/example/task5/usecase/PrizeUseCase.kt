package com.example.task5.usecase

import com.example.task5.data.external.NobelApiClient
import com.example.task5.domain.model.Prize
import com.example.task5.domain.repository.PrizeRepository
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class PrizeUseCase(
    private val prizeRepository: PrizeRepository,
    private val nobelApiClient: NobelApiClient,
) {
    private val seedMutex = Mutex()

    suspend fun getPrizes(year: Int?, category: String?, limit: Int, offset: Int): List<Prize> {
        ensureSeededIfNeeded()
        return prizeRepository.listPrizes(year, category, limit, offset)
    }

    suspend fun prizeExists(prizeId: Int): Boolean = prizeRepository.findPrizeById(prizeId) != null

    private suspend fun ensureSeededIfNeeded() {
        if (prizeRepository.count() > 0) return

        seedMutex.withLock {
            if (prizeRepository.count() > 0) return
            try {
                val prizes = nobelApiClient.fetchPrizes()
                if (prizes.isNotEmpty()) {
                    prizeRepository.saveAll(prizes)
                }
            } catch (error: Exception) {
                throw PrizeSeedException("Unable to fetch Nobel prizes from remote API", error)
            }
        }
    }
}

class PrizeSeedException(
    message: String,
    cause: Throwable? = null,
) : RuntimeException(message, cause)
