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

import android.content.Context
import io.github.mfederczuk.filecache.adapter.CacheAdapter
import java.io.File
import java.time.Duration

/**
 * Factory class for [FileCache] instances, somewhat simplifying creation for them.
 *
 * @since TODO TBD
 *
 * @see FileCacheFactoryBuilder
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

	/**
	 * Creates a new `FileCacheFactoryBuilder` with the same property values that this `FileCacheFactory` has.
	 *
	 * @return A new `FileCacheFactoryBuilder` instance.
	 *
	 * @since TODO TBD
	 */
	public fun newBuilder(): FileCacheFactoryBuilder {
		return FileCacheFactoryBuilder()
			.baseDir(baseDir)
			.filenamePrefix(filenamePrefix)
			.filenameSuffix(filenameSuffix)
			.defaultMaxAge(defaultMaxAge)
	}
}

/**
 * Builder-pattern class for [FileCacheFactory].
 *
 * @since TODO TBD
 *
 * @constructor Constructs a new `FileCacheFactoryBuilder` instance.
 */
public class FileCacheFactoryBuilder {

	private var baseDir: File? = null
	private var filenamePrefix: String = ""
	private var filenameSuffix: String = ""
	private var defaultMaxAge: Duration = Duration.ZERO


	/**
	 * Sets the base directory for the save location of the cache file.
	 *
	 * @param baseDir New value for the `baseDir` parameter of the [FileCacheFactory] constructor.
	 *
	 * @return This `FileCacheFactoryBuilder` instance.
	 *
	 * @since TODO TBD
	 *
	 * @see baseCacheDir
	 * @see baseFilesDir
	 */
	public fun baseDir(baseDir: File): FileCacheFactoryBuilder {
		this.baseDir = baseDir
		return this
	}

	/**
	 * Sets the base directory for the save location of the cache file.
	 *
	 * @param baseDir New value for the `baseDir` parameter of the [FileCacheFactory] constructor.
	 *
	 * @return This `FileCacheFactoryBuilder` instance.
	 *
	 * @since TODO TBD
	 *
	 * @see baseCacheDir
	 * @see baseFilesDir
	 */
	@Suppress("NOTHING_TO_INLINE")
	public inline fun baseDir(baseDir: String): FileCacheFactoryBuilder {
		return baseDir(File(baseDir))
	}


	/**
	 * Sets the base directory for the save location of the cache file from an Android `Context` object.
	 *
	 * The [context] object will be used to retrieve the application specific cache directory path
	 * ([Context.getCacheDir]) and use it as a base directory for the save location of the cache file.
	 *
	 * @param context The `Context` to use the application specific cache directory path from.
	 *
	 * @param relativeBaseDir Extra path to append to the application specific cache directory path.\
	 * Default value is an empty path.
	 *
	 * @return This `FileCacheFactoryBuilder` instance.
	 *
	 * @since TODO TBD
	 *
	 * @see baseDir
	 * @see baseFilesDir
	 */
	@Suppress("NOTHING_TO_INLINE")
	@JvmOverloads
	public inline fun baseCacheDir(context: Context, relativeBaseDir: File = File("")): FileCacheFactoryBuilder {
		return baseDir(context.cacheDir.resolve(relativeBaseDir))
	}

	/**
	 * Sets the base directory for the save location of the cache file from an Android `Context` object.
	 *
	 * The [context] object will be used to retrieve the application specific cache directory path
	 * ([Context.getCacheDir]) and use it as a base directory for the save location of the cache file.
	 *
	 * @param context The `Context` to use the application specific cache directory path from.
	 *
	 * @param relativeBaseDir Extra path to append to the application specific cache directory path.
	 *
	 * @return This `FileCacheFactoryBuilder` instance.
	 *
	 * @since TODO TBD
	 *
	 * @see baseDir
	 * @see baseFilesDir
	 */
	@Suppress("NOTHING_TO_INLINE")
	public inline fun baseCacheDir(context: Context, relativeBaseDir: String): FileCacheFactoryBuilder {
		return baseCacheDir(context, File(relativeBaseDir))
	}


	/**
	 * Sets the base directory for the save location of the cache file from an Android `Context` object.
	 *
	 * The [context] object will be used to retrieve the directory path for application files
	 * ([Context.getFilesDir]) and use it as a base directory for the save location of the cache file.
	 *
	 * @param context The `Context` to use the directory for application files from.
	 *
	 * @param relativeBaseDir Extra path to append to the directory path for application files.\
	 * Default value is an empty path.
	 *
	 * @return This `FileCacheFactoryBuilder` instance.
	 *
	 * @since TODO TBD
	 *
	 * @see baseDir
	 * @see baseCacheDir
	 */
	@Suppress("NOTHING_TO_INLINE")
	@JvmOverloads
	public inline fun baseFilesDir(context: Context, relativeBaseDir: File = File("")): FileCacheFactoryBuilder {
		return baseDir(context.filesDir.resolve(relativeBaseDir))
	}

	/**
	 * Sets the base directory for the save location of the cache file from an Android `Context` object.
	 *
	 * The [context] object will be used to retrieve the directory path for application files
	 * ([Context.getFilesDir]) and use it as a base directory for the save location of the cache file.
	 *
	 * @param context The `Context` to use the directory for application files from.
	 *
	 * @param relativeBaseDir Extra path to append to the directory path for application files.
	 *
	 * @return This `FileCacheFactoryBuilder` instance.
	 *
	 * @since TODO TBD
	 *
	 * @see baseDir
	 * @see baseCacheDir
	 */
	@Suppress("NOTHING_TO_INLINE")
	public inline fun baseFilesDir(context: Context, relativeBaseDir: String): FileCacheFactoryBuilder {
		return baseFilesDir(context, File(relativeBaseDir))
	}


	/**
	 * Sets the prefix for the filename of the cache file.
	 *
	 * The default value, if this method is not called, is no prefix. (empty string)
	 *
	 * @param filenamePrefix New value for the `filenamePrefix` parameter of the [FileCacheFactory] constructor.
	 *
	 * @return This `FileCacheFactoryBuilder` instance.
	 *
	 * @since TODO TBD
	 */
	public fun filenamePrefix(filenamePrefix: String): FileCacheFactoryBuilder {
		this.filenamePrefix = filenamePrefix
		return this
	}

	/**
	 * Sets the suffix for the filename of the cache file.
	 *
	 * The default value, if this method is not called, is no suffix. (empty string)
	 *
	 * @param filenameSuffix New value for the `filenameSuffix` parameter of the [FileCacheFactory] constructor.
	 *
	 * @return This `FileCacheFactoryBuilder` instance.
	 *
	 * @since TODO TBD
	 */
	public fun filenameSuffix(filenameSuffix: String): FileCacheFactoryBuilder {
		this.filenameSuffix = filenameSuffix
		return this
	}


	/**
	 * Sets the default value for the `maxAge` parameter of the [FileCacheFactory.create] method.
	 *
	 * The default value, if this method is not called, is [Duration.ZERO].
	 *
	 * @param defaultMaxAge New value for the `defaultMaxAge` parameter of the [FileCacheFactory] constructor.
	 *
	 * @return This `FileCacheFactoryBuilder` instance.
	 *
	 * @since TODO TBD
	 */
	public fun defaultMaxAge(defaultMaxAge: Duration): FileCacheFactoryBuilder {
		this.defaultMaxAge = defaultMaxAge
		return this
	}


	/**
	 * Builds a new `FileCacheFactory` instance.
	 *
	 * @return A new `FileCacheFactory` instance.
	 *
	 * @throws IllegalStateException When this method was called before any of the `base*Dir` methods was called.
	 *
	 * @since TODO TBD
	 */
	public fun build(): FileCacheFactory {
		return FileCacheFactory(
			checkNotNull(baseDir) { "No base directory given" },
			filenamePrefix,
			filenameSuffix,
			defaultMaxAge
		)
	}
}
