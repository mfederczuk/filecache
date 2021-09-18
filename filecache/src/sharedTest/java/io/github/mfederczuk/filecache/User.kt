/*
 * Copyright 2021 Michael Federczuk
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.mfederczuk.filecache

import io.github.mfederczuk.filecache.adapter.CacheAdapter
import io.github.mfederczuk.filecache.adapter.CacheReader
import io.github.mfederczuk.filecache.adapter.CacheWriter
import io.github.mfederczuk.filecache.adapter.GenericListCacheAdapter
import io.github.mfederczuk.filecache.adapter.readNullableSerializable
import io.github.mfederczuk.filecache.adapter.writeNullableSerializable
import java.io.Serializable
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlin.random.Random

// it's not recommended to serialize enum values using their ordinal
public enum class UserType(public val n: Int) {
	ADMIN(2),
	MODERATOR(1),
	REGULAR(0);

	public companion object {
		public fun fromN(n: Int): UserType {
			return values()
				.first { type ->
					type.n == n
				}
		}
	}
}

public data class User(
	val uid: Long,
	val name: String,
	val imageUrl: String,
	val birthdate: LocalDate?,
	val joinTime: Instant,
	val type: UserType,
	val comments: List<Comment>,
) : Serializable {

	public companion object {
		private val NAMES: Array<String> =
			arrayOf(
				"John Doe",
				"Jane Doe",
				"Ted Strange",
			)

		private val BIRTHDATE_DATE_RANGE: LongRange =
			run {
				val begin = LocalDate.of(1980, 1, 1)
					.atStartOfDay()
					.toEpochSecond(ZoneOffset.UTC)

				val end = LocalDate.of(2000, 12, 31)
					.atStartOfDay()
					.toEpochSecond(ZoneOffset.UTC)

				begin..end
			}

		private val JOIN_TIME_DATE_RANGE: LongRange =
			run {
				val begin = LocalDate.of(2015, 6, 15)
					.atStartOfDay()
					.toEpochSecond(ZoneOffset.UTC)

				val end = LocalDateTime.now()
					.toEpochSecond(ZoneOffset.UTC)

				begin..end
			}

		private val IMAGE_URLS: Array<String> =
			arrayOf(
				"",
				"file:",
				"https://cdn2.thecatapi.com/images/BeLXGvVD2.jpg",
				"https://cdn2.thecatapi.com/images/297.jpg",
				"https://cdn2.thecatapi.com/images/df4.jpg",
				"https://cdn2.thecatapi.com/images/7nm.jpg",
			)

		public fun getRandomInstance(): User {
			val birthdate = LocalDateTime
				.ofInstant(
					Instant.ofEpochSecond(BIRTHDATE_DATE_RANGE.random()),
					ZoneOffset.UTC
				)
				.toLocalDate()

			val commentsCount = Random.nextInt(2, 9)
			val comments = ArrayList<Comment>(commentsCount)
			repeat(commentsCount) {
				comments.add(Comment.getRandomInstance())
			}

			return User(
				uid = Random.nextLong(),
				name = NAMES.random(),
				birthdate = birthdate,
				joinTime = Instant.ofEpochSecond(JOIN_TIME_DATE_RANGE.random()),
				imageUrl = IMAGE_URLS.random(),
				type = UserType.values().random(),
				comments = comments
			)
		}
	}
}

public object UserCacheAdapter : CacheAdapter<User>() {

	private val commentListCacheAdapter: CacheAdapter<List<Comment>> = GenericListCacheAdapter(CommentCacheAdapter)

	override fun read(reader: CacheReader): User {
		val id = reader.readLong()
		val name = reader.readString()
		val imageUrl = reader.readString()
		val birthdate = reader.readNullableSerializable<LocalDate>()
		val joinTime = Instant.ofEpochSecond(reader.readLong())
		val type = UserType.fromN(reader.readByte().toInt())
		val comments = reader.readObject(commentListCacheAdapter)

		return User(
			id,
			name,
			imageUrl,
			birthdate,
			joinTime,
			type,
			comments,
		)
	}

	override fun write(value: User, writer: CacheWriter) {
		writer.writeLong(value.uid)
		writer.writeString(value.name)
		writer.writeString(value.imageUrl)
		writer.writeNullableSerializable(value.birthdate)
		writer.writeLong(value.joinTime.epochSecond)
		writer.writeByte(value.type.n.toByte())
		writer.writeObject(value.comments, commentListCacheAdapter)
	}
}
