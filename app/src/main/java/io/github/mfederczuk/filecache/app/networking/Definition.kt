package io.github.mfederczuk.filecache.app.networking

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Definition(
	val word: String,
	val phonetics: List<Phonetic>,
	val meanings: List<Meaning>,
)

@JsonClass(generateAdapter = true)
data class Phonetic(
	val text: String,
	@Json(name = "audio") val audioUrl: String,
)

@JsonClass(generateAdapter = true)
data class Meaning(
	val partOfSpeech: String,
)
