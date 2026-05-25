package com.example.task5.data.external

import com.example.task5.domain.model.NewLaureate
import com.example.task5.domain.model.NewPrize
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject

class NobelApiClient(
    private val client: HttpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    },
) {
    suspend fun fetchPrizes(): List<NewPrize> {
        val response = client.get("https://api.nobelprize.org/2.1/nobelPrizes?limit=1000").body<NobelResponse>()
        return response.nobelPrizes.mapNotNull { it.toNewPrize() }
    }
}

@Serializable
private data class NobelResponse(
    val nobelPrizes: List<JsonObject> = emptyList(),
)

private fun JsonObject.toNewPrize(): NewPrize? {
    val awardYear = getPrimitiveContent("awardYear")?.toIntOrNull() ?: return null
    val category = getLocalizedString("category") ?: return null

    val laureatesArray = (this["laureates"] as? JsonArray).orEmpty()
    val laureates = laureatesArray.mapNotNull { it.jsonObject.toNewLaureate() }
    val fullName = laureates.takeIf { it.isNotEmpty() }?.joinToString(",") { it.fullName }
    val motivation = laureates.mapNotNull { it.motivation }.takeIf { it.isNotEmpty() }?.joinToString("; ")
    val detailLink = (this["links"] as? JsonArray)
        ?.firstOrNull()
        ?.jsonObject
        ?.getPrimitiveContent("href")

    return NewPrize(
        awardYear = awardYear,
        category = category,
        fullName = fullName,
        motivation = motivation,
        detailLink = detailLink,
        laureates = laureates,
    )
}

private fun JsonObject.toNewLaureate(): NewLaureate? {
    val fullName = getLocalizedString("fullName")
        ?: getLocalizedString("knownName")
        ?: return null

    return NewLaureate(
        fullName = fullName,
        portion = getPrimitiveContent("portion"),
        motivation = getLocalizedString("motivation"),
        portraitUrl = getPrimitiveContent("thumbnail")
            ?: this["image"]?.jsonObject?.getPrimitiveContent("url"),
    )
}

private fun JsonObject.getLocalizedString(key: String): String? {
    return when (val value = this[key]) {
        is JsonObject -> value.getPrimitiveContent("en")
        is JsonPrimitive -> value.content
        else -> null
    }
}

private fun JsonObject.getPrimitiveContent(key: String): String? {
    return (this[key] as? JsonPrimitive)?.content
}

private fun JsonElement?.orEmpty(): JsonArray = this as? JsonArray ?: JsonArray(emptyList())
