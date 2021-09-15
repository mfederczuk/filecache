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

import java.time.Duration
import java.time.Instant

/**
 * Represents the state of a cache in a particular point in time.
 *
 * @param T The type of the value stored in the cache.
 *
 * @since TODO TBD
 *
 * @constructor Constructs a new `CacheSnapshot` instance.
 *
 * @param value The stored value of the cache.
 *
 * @param modificationTime The point in time when [value] was written to the cache.\
 * Defaults to [Instant.now].
 */
public data class CacheSnapshot<T : Any>(
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
	 * The time between [modificationTime] and [now][Instant.now].
	 *
	 * @since TODO TBD
	 *
	 * @see Duration.between
	 */
	inline val age: Duration
		/**
		 * The time between [modificationTime] and [now][Instant.now].
		 *
		 * @since TODO TBD
		 *
		 * @see Duration.between
		 */
		inline get() {
			val now = Instant.now()
			return Duration.between(modificationTime, now)
		}
}
