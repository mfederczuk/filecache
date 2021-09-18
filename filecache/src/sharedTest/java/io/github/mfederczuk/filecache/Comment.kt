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
import java.io.Serializable
import kotlin.random.Random

public data class Comment(
	val uid: Long,
	val content: String,
) : Serializable {

	public companion object {
		private const val CHARS: String =
			"ABCDEFGHIJKLMNOPQRSTUVWXYZ abcdefghijklmnopqrstuvwxyz abcdefghijklmnopqrstuvwxyz 0123456789"

		public fun getRandomInstance(): Comment {
			val contentLength = Random.nextInt(4, 65)
			val content =
				buildString(capacity = contentLength) {
					repeat(contentLength) {
						append(CHARS.random())
					}
				}

			return Comment(
				uid = Random.nextLong(),
				content = content
			)
		}
	}
}

public object CommentCacheAdapter : CacheAdapter<Comment>() {

	override fun read(reader: CacheReader): Comment {
		val uid = reader.readLong()
		val content = reader.readString()

		return Comment(
			uid,
			content
		)
	}

	override fun write(value: Comment, writer: CacheWriter) {
		writer.writeLong(value.uid)
		writer.writeString(value.content)
	}
}
