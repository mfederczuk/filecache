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
import java.io.File
import java.time.Duration

/**
 * Factory class for [FileCache] instances, somewhat simplifying creation for them.
 *
 * @since TODO TBD
 *
 * @constructor Constructs a new `FileCacheFactory` instance.
 *
 * @param baseDir Base directory for the save location of the cache file.
 *
 * @param filenamePrefix Prefix for the filename of the cache file.\
 * Only the missing characters of the prefix are appended to the given filename.
 * (e.g.: prefix: `"cache_"` + filename: `"_user"` = `"cache_user"`, NOT `"cache__user"`)\
 * Default is no prefix. (empty string)
 *
 * @param filenameSuffix Suffix for the filename of the cache file.\
 * Only the missing characters of the suffix are appended to the given filename.
 * (e.g.: suffix: `".cache.bin"` + filename: `"user.cache"` = `"user.cache.bin"`, NOT `"user.cache.cache.bin"`)\
 * Default is no suffix. (empty string)
 *
 * @param defaultMaxAge The default value for the `maxAge` parameter of the [create] method.\
 * Default value of [*this*][defaultMaxAge] parameter is [Duration.ZERO].
 */
public class FileCacheFactory @JvmOverloads constructor(
	private val baseDir: File,
	private val filenamePrefix: String = "",
	private val filenameSuffix: String = "",
	private val defaultMaxAge: Duration = Duration.ZERO
) {

	/**
	 * Creates a new `FileCache` instance.
	 *
	 * @param T The type of object to store in the cache.
	 *
	 * @param name A path that, combined with the base directory and possible the filename suffix given to this
	 * `FileCacheFactory` instance at construction, makes up the save location of the cache file.\
	 * If [name] already ends with the suffix, then it is *not* appended again.
	 *
	 * @param adapter The `CacheAdapter` to use to convert serialized data to a substantial object and to convert a
	 * substantial object to serialized data.
	 *
	 * @param maxAge The maximum age of the cache before it is seen as invalidated.\
	 * After this duration, the cache is seen as empty, and any attempt to access it (even if the cache is still present)
	 * will result in an empty result or an error. (depending on the method that was called)\
	 * If this parameter is set to [Duration.ZERO] or below, then this feature is turned off.
	 *
	 * Default value is the instance given to this `FileCacheFactory` instance at construction.
	 *
	 * @return A new `FileCache` instance.
	 *
	 * @since TODO TBD
	 */
	@JvmOverloads
	public fun <T : Any> create(
		name: String,
		adapter: CacheAdapter<T>,
		maxAge: Duration = defaultMaxAge
	): FileCache<T> {
		var file = File(name).normalize()

		if((filenamePrefix.isNotEmpty() || filenameSuffix.isNotEmpty()) && file.name.isNotEmpty()) {
			val parent = File(file.parent.orEmpty())
			val filename = file.name
				.withPrefixAndSuffix(filenamePrefix, filenameSuffix)

			file = parent.resolve(filename)
		}

		file = baseDir.resolve(file)

		return FileCache(
			file,
			adapter,
			maxAge
		)
	}
}
