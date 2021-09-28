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

import androidx.annotation.CheckResult
import java.time.Duration
import java.time.Instant

/**
 * Update object used for [FileCache.update].
 *
 * @param T The type of the value stored in the cache.
 *
 * @see CacheSnapshot
 *
 * @since TODO TBD
 *
 * @constructor Constructs a new `CacheUpdate` instance.
 *
 * @param value The value to write to the cache.
 *
 * @param modificationTime The instant to set as the cache modification time.\
 * Defaults to [Instant.now].
 *
 * @see fromAge
 */
public data class CacheUpdate<T : Any>(
	/**
	 * The stored value of the cache.
	 *
	 * @since TODO TBD
	 */
	val value: T,

	/**
	 * The point in time when [value] was written to the cache.
	 *
	 * @since TODO TBD
	 */
	val modificationTime: Instant = Instant.now(),
) {

	/**
	 * Contains factory function for [CacheUpdate].
	 *
	 * @since TODO TBD
	 */
	public companion object {

		/**
		 * Creates a new `CacheUpdate`.\
		 * Instead of specifying the point in time used as the modification time, the age of the cache is given.
		 *
		 * The actual modification time will be set to ([now] - [age]).
		 *
		 * @param T The type of the value stored in the cache.
		 *
		 * @param value The value to write to the cache.
		 *
		 * @param age The requested age of the cache.
		 *
		 * @param now The instant of time serving as "now" that will be used to calculate the actual time of
		 * modification.\
		 * Defaults to [Instant.now].
		 *
		 * @return A new `CacheUpdate` instance.
		 *
		 * @since TODO TBD
		 */
		@CheckResult
		@JvmOverloads
		public fun <T : Any> fromAge(value: T, age: Duration, now: Instant = Instant.now()): CacheUpdate<T> {
			return CacheUpdate(
				value,
				modificationTime = now.minus(age)
			)
		}
	}
}
