package com.example.task5.data.repository

import com.example.task5.data.db.PrizesTable
import com.example.task5.data.db.UserPrizesTable
import com.example.task5.domain.model.Prize
import com.example.task5.domain.repository.FavoriteRepository
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertIgnore
import org.jetbrains.exposed.sql.selectAll
import java.time.LocalDateTime

class ExposedFavoriteRepository : FavoriteRepository {
    override suspend fun listFavoritePrizes(userId: Int): List<Prize> = dbQuery {
        (UserPrizesTable innerJoin PrizesTable)
            .selectAll()
            .where { UserPrizesTable.userId eq userId }
            .orderBy(UserPrizesTable.addedAt to org.jetbrains.exposed.sql.SortOrder.DESC)
            .map { it.toPrize() }
    }

    override suspend fun addFavorite(userId: Int, prizeId: Int) {
        dbQuery {
            UserPrizesTable.insertIgnore {
                it[UserPrizesTable.userId] = userId
                it[UserPrizesTable.prizeId] = prizeId
                it[addedAt] = LocalDateTime.now()
            }
        }
    }

    override suspend fun removeFavorite(userId: Int, prizeId: Int) {
        dbQuery {
            UserPrizesTable.deleteWhere { (UserPrizesTable.userId eq userId) and (UserPrizesTable.prizeId eq prizeId) }
        }
    }

    private fun ResultRow.toPrize(): Prize = Prize(
        id = this[PrizesTable.id].value,
        awardYear = this[PrizesTable.awardYear],
        category = this[PrizesTable.category],
        fullName = this[PrizesTable.fullName],
        motivation = this[PrizesTable.motivation],
        detailLink = this[PrizesTable.detailLink],
    )
}
