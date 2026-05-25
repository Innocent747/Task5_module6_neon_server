package com.example.task5.data.repository

import com.example.task5.data.db.LaureatesTable
import com.example.task5.data.db.PrizesTable
import com.example.task5.domain.model.NewPrize
import com.example.task5.domain.model.Prize
import com.example.task5.domain.repository.PrizeRepository
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.selectAll

class ExposedPrizeRepository : PrizeRepository {
    override suspend fun count(): Long = dbQuery {
        PrizesTable.selectAll().count()
    }

    override suspend fun listPrizes(year: Int?, category: String?, limit: Int, offset: Int): List<Prize> = dbQuery {
        val query = PrizesTable.selectAll()
        if (year != null) {
            query.andWhere { PrizesTable.awardYear eq year }
        }
        if (!category.isNullOrBlank()) {
            val normalized = "%$category%"
            query.andWhere { PrizesTable.category like normalized }
        }

        query
            .orderBy(PrizesTable.awardYear to org.jetbrains.exposed.sql.SortOrder.DESC)
            .limit(limit.coerceAtLeast(1), offset.toLong().coerceAtLeast(0L))
            .map { it.toPrize() }
    }

    override suspend fun findPrizeById(prizeId: Int): Prize? = dbQuery {
        PrizesTable.selectAll()
            .where { PrizesTable.id eq prizeId }
            .singleOrNull()
            ?.toPrize()
    }

    override suspend fun saveAll(prizes: List<NewPrize>) {
        dbQuery {
            prizes.forEach { prize ->
                val prizeId = PrizesTable.insertAndGetId {
                    it[awardYear] = prize.awardYear
                    it[category] = prize.category
                    it[fullName] = prize.fullName
                    it[motivation] = prize.motivation
                    it[detailLink] = prize.detailLink
                }

                prize.laureates.forEach { laureate ->
                    LaureatesTable.insert {
                        it[LaureatesTable.prizeId] = prizeId
                        it[fullName] = laureate.fullName
                        it[portion] = laureate.portion
                        it[motivation] = laureate.motivation
                        it[portraitUrl] = laureate.portraitUrl
                    }
                }
            }
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
