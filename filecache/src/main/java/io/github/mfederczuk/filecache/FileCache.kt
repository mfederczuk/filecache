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

@file:Suppress("NOTHING_TO_INLINE")

package io.github.mfederczuk.filecache

import android.content.Context
import androidx.annotation.CheckResult
import io.github.mfederczuk.filecache.adapter.CacheAdapter
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.RandomAccessFile
import java.security.SecureRandom
import java.time.Duration
import java.time.Instant
import java.util.Vector
import java.util.function.Supplier
import kotlin.math.ceil

/**
 * Object to access and manage a cached value stored as serialized data inside a regular file on the filesystem.
 *
 * This class exposes methods to retrieve, update or delete the cache file.
 *
 * ### Thread safety ###
 *
 * This class is thread-safe.
 *
 * Only ever one write operation at the same time is permitted, ensuring that multiple threads do not write to the file
 * at the same time, thereby possible creating malformed data.\
 * The write operation also blocks any read operations from running, ensuring that no half-finished data is read.
 *
 * Multiple read operations can be running at the same time, since they do not interfere with each other.\
 * As long as at least one read permission is running, write operations are blocked.
 *
 * Thread safety can only be guaranteed *as long as the same instance of this class is used*.\
 * Only one instance per distinct file should exist.
 *
 * @param T The type of object to store in the cache.
 *
 * @since TODO TBD
 *
 * @constructor Constructs a new `FileCache` instance.
 *
 * @param file The location where to save the cache file.\
 * The application requires read and write permissions to this location.
 *
 * @param adapter The `CacheAdapter` to use to convert serialized data to a substantial object and to convert a
 * substantial object to serialized data.
 *
 * @param maxAge The maximum age of the cache before it is seen as invalidated.\
 * After this duration, the cache is seen as empty, and any attempt to access it (even if the cache is still present)
 * will result in an empty result or an error. (depending on the method that was called)\
 * If this parameter is set to [Duration.ZERO] (which is also the default value) or below, then this feature is turned
 * off.
 */
public class FileCache<T : Any> @JvmOverloads constructor(
	/**
	 * The location where the cache file is saved.
	 *
	 * @since TODO TBD
	 */
	public val file: File,

	/**
	 * The `CacheAdapter` used to convert serialized data to a substantial object and to convert a substantial object to
	 * serialized data.
	 *
	 * @since TODO TBD
	 */
	public val adapter: CacheAdapter<T>,

	/**
	 * The maximum age of the cache before it is seen as invalidated.\
	 * After this duration, the cache is seen as empty, and any attempt to access it (even if the cache is still present)
	 * will result in an empty result or an error. (depending on the method that was called)\
	 * If this field is set to [Duration.ZERO] or below, then this feature is turned off.
	 *
	 * @since TODO TBD
	 */
	public val maxAge: Duration = Duration.ZERO
) {

	/**
	 * Contains factory functions for [FileCache].
	 *
	 * @since TODO TBD
	 */
	public companion object {

		private const val NO_CACHE_PRESENT_MSG = "No cache present"

		private const val SHRED_ITERATIONS: Int = 3
		private const val SHRED_BUFFER_SIZE: Int = 4 * 1024

		/**
		 * Creates a new `FileCache` instance from an Android `Context` object.
		 *
		 * The [context] object will be used to retrieve the application specific cache directory path
		 * ([Context.getCacheDir]) and use it as a base directory for the save location of the cache file.
		 *
		 * @param T The type of object to store in the cache.
		 *
		 * @param context The `Context` to use the application specific cache directory path from.
		 *
		 * @param name A path that, combined with the application specific cache directory from [context], makes up the
		 * save location of the cache file.
		 *
		 * @param adapter The `CacheAdapter` to use to convert serialized data to a substantial object and to convert a
		 * substantial object to serialized data.
		 *
		 * @param maxAge The maximum age of the cache before it is seen as invalidated.\
		 * After this duration, the cache is seen as empty, and any attempt to access it (event if the cache is still
		 * present) will result in an empty result or an error. (depending on the method that was called)\
		 * If this parameter is set to [Duration.ZERO] or below, then this feature is turned off.
		 *
		 * @return A new `FileCache` instance.
		 *
		 * @since TODO TBD
		 *
		 * @see createInFilesDir
		 */
		@JvmStatic
		@JvmOverloads
		public fun <T : Any> createInCacheDir(
			context: Context,
			name: File,
			adapter: CacheAdapter<T>,
			maxAge: Duration = Duration.ZERO
		): FileCache<T> {
			return FileCache(
				context.cacheDir.resolve(name),
				adapter,
				maxAge
			)
		}

		/**
		 * Creates a new `FileCache` instance from an Android `Context` object.
		 *
		 * The [context] object will be used to retrieve the application specific cache directory path
		 * ([Context.getCacheDir]) and use it as a base directory for the save location of the cache file.
		 *
		 * @param T The type of object to store in the cache.
		 *
		 * @param context The `Context` to use the application specific cache directory path from.
		 *
		 * @param name A path that, combined with the application specific cache directory from [context], makes up the
		 * save location of the cache file.
		 *
		 * @param adapter The `CacheAdapter` to use to convert serialized data to a substantial object and to convert a
		 * substantial object to serialized data.
		 *
		 * @param maxAge The maximum age of the cache before it is seen as invalidated.\
		 * After this duration, the cache is seen as empty, and any attempt to access it (event if the cache is still
		 * present) will result in an empty result or an error. (depending on the method that was called)\
		 * If this parameter is set to [Duration.ZERO] or below, then this feature is turned off.
		 *
		 * @return A new `FileCache` instance.
		 *
		 * @since TODO TBD
		 *
		 * @see createInFilesDir
		 */
		@JvmStatic
		@JvmOverloads
		public inline fun <T : Any> createInCacheDir(
			context: Context,
			name: String,
			adapter: CacheAdapter<T>,
			maxAge: Duration = Duration.ZERO
		): FileCache<T> {
			return createInCacheDir(context, File(name), adapter, maxAge)
		}

		/**
		 * Creates a new `FileCache` instance from an Android `Context` object.
		 *
		 * The [context] object will be used to retrieve the directory path for application files
		 * ([Context.getFilesDir]) and use it as a base directory for the save location of the cache file.
		 *
		 * @param T The type of object to store in the cache.
		 *
		 * @param context The `Context` to use the directory for application files from.
		 *
		 * @param name A path that, combined with the directory for application files from [context], makes up the save
		 * location of the cache file.
		 *
		 * @param adapter The `CacheAdapter` to use to convert serialized data to a substantial object and to convert a
		 * substantial object to serialized data.
		 *
		 * @param maxAge The maximum age of the cache before it is seen as invalidated.\
		 * After this duration, the cache is seen as empty, and any attempt to access it (event if the cache is still
		 * present) will result in an empty result or an error. (depending on the method that was called)\
		 * If this parameter is set to [Duration.ZERO] or below, then this feature is turned off.
		 *
		 * @return A new `FileCache` instance.
		 *
		 * @since TODO TBD
		 *
		 * @see createInCacheDir
		 */
		@JvmStatic
		@JvmOverloads
		public fun <T : Any> createInFilesDir(
			context: Context,
			name: File,
			adapter: CacheAdapter<T>,
			maxAge: Duration = Duration.ZERO
		): FileCache<T> {
			return FileCache(
				context.filesDir.resolve(name),
				adapter,
				maxAge
			)
		}

		/**
		 * Creates a new `FileCache` instance from an Android `Context` object.
		 *
		 * The [context] object will be used to retrieve the directory path for application files
		 * ([Context.getFilesDir]) and use it as a base directory for the save location of the cache file.
		 *
		 * @param T The type of object to store in the cache.
		 *
		 * @param context The `Context` to use the directory for application files from.
		 *
		 * @param name A path that, combined with the directory for application files from [context], makes up the save
		 * location of the cache file.
		 *
		 * @param adapter The `CacheAdapter` to use to convert serialized data to a substantial object and to convert a
		 * substantial object to serialized data.
		 *
		 * @param maxAge The maximum age of the cache before it is seen as invalidated.\
		 * After this duration, the cache is seen as empty, and any attempt to access it (event if the cache is still
		 * present) will result in an empty result or an error. (depending on the method that was called)\
		 * If this parameter is set to [Duration.ZERO] or below, then this feature is turned off.
		 *
		 * @return A new `FileCache` instance.
		 *
		 * @since TODO TBD
		 *
		 * @see createInCacheDir
		 */
		@JvmStatic
		@JvmOverloads
		public inline fun <T : Any> createInFilesDir(
			context: Context,
			name: String,
			adapter: CacheAdapter<T>,
			maxAge: Duration = Duration.ZERO
		): FileCache<T> {
			return createInFilesDir(context, File(name), adapter, maxAge)
		}
	}

	private val readWriteSemaphore = ReadWriteSemaphore()
	private val sharedReusableBuffer = ByteArray(Long.SIZE_BYTES)
	private val blockedForUpdateThreads = Vector<Thread>()

	private fun checkBlockedForUpdateThreads() {
		check(Thread.currentThread() !in blockedForUpdateThreads) { "This thread is blocked! A deadlock was prevented" }
	}

	private fun <R> blockCurrentThreadForUpdate(block: () -> R): R {
		val currentThread = Thread.currentThread()

		blockedForUpdateThreads.add(currentThread)
		try {
			return block()
		} finally {
			blockedForUpdateThreads.remove(currentThread)
		}
	}

	// region read operations

	// region read utils

	@CheckResult
	private inline fun unsynchronizedGetModificationTime(): Instant {
		return Instant.ofEpochMilli(file.lastModified())
	}

	@CheckResult
	private fun unsynchronizedIsPresent(now: Instant = Instant.now()): Boolean {
		if(file.length() <= 0) {
			return false
		}

		if(maxAge > Duration.ZERO) {
			val age = Duration.between(
				unsynchronizedGetModificationTime(),
				now
			)

			return (age <= maxAge)
		}

		return true
	}

	/** Calls [action] when the cache is present. */
	private inline fun <R : Any> unsynchronizedRunIfPresent(crossinline action: () -> R?): R? {
		return if(unsynchronizedIsPresent()) {
			action()
		} else {
			null
		}
	}

	private inline fun <R : Any> unsynchronizedReadFile(crossinline action: (InputStream) -> R?): R? {
		return unsynchronizedRunIfPresent {
			file.inputStream()
				.use(action)
		}
	}

	/** Invokes [action] when the cache is present. */
	private inline fun <R : Any> synchronizedRunIfPresent(crossinline action: () -> R?): R? {
		return readWriteSemaphore.read {
			unsynchronizedRunIfPresent(action)
		}
	}

	/** Opens an [InputStream] and calls [action] when the cache is present. */
	private inline fun <R : Any> synchronizedReadFile(crossinline action: (InputStream) -> R?): R? {
		return readWriteSemaphore.read {
			unsynchronizedReadFile(action)
		}
	}

	@CheckResult
	private inline fun unsynchronizedGetValueOrNullFromInputStream(inputStream: InputStream): T {
		val cacheReader = CacheReaderImpl(inputStream)

		return cacheReader.readObject(adapter)
	}

	@CheckResult
	private inline fun unsynchronizedGetSnapshotOrNullFromInputStream(inputStream: InputStream): CacheSnapshot<T> {
		val cacheReader = CacheReaderImpl(inputStream)

		val value = cacheReader.readObject(adapter)
		val modificationTime = unsynchronizedGetModificationTime()

		return CacheSnapshot(
			value,
			modificationTime
		)
	}

	@CheckResult
	private inline fun unsynchronizedGetSnapshotOrNull(): CacheSnapshot<T>? {
		return unsynchronizedReadFile(::unsynchronizedGetSnapshotOrNullFromInputStream)
	}

	@CheckResult
	private inline fun unsynchronizedGetValueOrNull(): T? {
		return unsynchronizedReadFile(::unsynchronizedGetValueOrNullFromInputStream)
	}

	// endregion

	// region present checks

	/**
	 * Tests whether or not the cache is currently present.
	 *
	 * This function does NOT check for malformed data, i.e.: even though the cache might be present, trying to retrieve
	 * the cache might still result in an error/`null`.
	 *
	 * @return `true` if the cache is currently present, `false` otherwise.
	 *
	 * @since TODO TBD
	 *
	 * @see isNotPresent
	 */
	@CheckResult
	public fun isPresent(): Boolean {
		checkBlockedForUpdateThreads()

		return readWriteSemaphore.read(::unsynchronizedIsPresent)
	}

	/**
	 * Tests whether or not the cache is currently present.
	 *
	 * Convenience method for `!isPresent()`.
	 *
	 * @return `true` if the cache is currently not present, `false` otherwise.
	 *
	 * @since TODO TBD
	 *
	 * @see isPresent
	 */
	@CheckResult
	public inline fun isNotPresent(): Boolean {
		return !isPresent()
	}

	// endregion

	// region get or null

	/**
	 * Tries to retrieve the current snapshot of the cache.
	 *
	 * @return The current snapshot of the cache, or `null` if no cache is currently present.
	 *
	 * @throws IOException When an I/O error occurs while reading.
	 *
	 * @since TODO TBD
	 *
	 * @see getSnapshot
	 * @see getValueOrNull
	 * @see getModificationTimeOrNull
	 */
	@CheckResult
	@Throws(IOException::class)
	public fun getSnapshotOrNull(): CacheSnapshot<T>? {
		checkBlockedForUpdateThreads()

		return synchronizedReadFile(::unsynchronizedGetSnapshotOrNullFromInputStream)
	}

	/**
	 * Tries to read the current modification time of the cache.
	 *
	 * @return The current modification time of the cache, or `null` if no cache is currently present.
	 *
	 * @since TODO TBD
	 *
	 * @see getModificationTime
	 * @see getSnapshotOrNull
	 * @see getValueOrNull
	 */
	@CheckResult
	public fun getModificationTimeOrNull(): Instant? {
		checkBlockedForUpdateThreads()

		return synchronizedRunIfPresent(::unsynchronizedGetModificationTime)
	}

	/**
	 * Tries to retrieve the current value of the cache.
	 *
	 * @return The current value of the cache, or `null` if no cache is currently present.
	 *
	 * @throws IOException When an I/O error occurs while reading.
	 *
	 * @since TODO TBD
	 *
	 * @see getValue
	 * @see getValueOrElse
	 * @see getValueOrDefault
	 * @see getSnapshotOrNull
	 * @see getModificationTimeOrNull
	 */
	@CheckResult
	@Throws(IOException::class)
	public fun getValueOrNull(): T? {
		checkBlockedForUpdateThreads()

		return synchronizedReadFile(::unsynchronizedGetValueOrNullFromInputStream)
	}

	// endregion

	// region get

	/**
	 * Retrieves the current snapshot of the cache.
	 *
	 * @return The current snapshot of the cache.
	 *
	 * @throws IllegalStateException If no cache is currently present.
	 *
	 * @throws IOException When an I/O error occurs while reading.
	 *
	 * @since TODO TBD
	 *
	 * @see getSnapshotOrNull
	 * @see getValue
	 * @see getModificationTime
	 */
	@CheckResult
	@Throws(IllegalStateException::class, IOException::class)
	public fun getSnapshot(): CacheSnapshot<T> {
		return checkNotNull(getSnapshotOrNull()) { NO_CACHE_PRESENT_MSG }
	}

	/**
	 * Reads the current modification time of the cache.
	 *
	 * @return The current modification time of the cache.
	 *
	 * @throws IllegalStateException If no cache is currently present.
	 *
	 * @since TODO TBD
	 *
	 * @see getModificationTimeOrNull
	 * @see getSnapshot
	 * @see getValue
	 */
	@CheckResult
	@Throws(IllegalStateException::class)
	public fun getModificationTime(): Instant {
		return checkNotNull(getModificationTimeOrNull()) { NO_CACHE_PRESENT_MSG }
	}

	/**
	 * Retrieves the current value of the value.
	 *
	 * @return The current value of the cache.
	 *
	 * @throws IllegalStateException If no cache is currently present.
	 *
	 * @throws IOException When an I/O error occurs while reading.
	 *
	 * @since TODO TBD
	 *
	 * @see getValueOrNull
	 * @see getValueOrElse
	 * @see getValueOrDefault
	 * @see getSnapshot
	 * @see getModificationTime
	 */
	@CheckResult
	@Throws(IllegalStateException::class, IOException::class)
	public fun getValue(): T {
		return checkNotNull(getValueOrNull()) { NO_CACHE_PRESENT_MSG }
	}

	// endregion

	// region get or else

	/**
	 * Tries to retrieve the current value of the cache.
	 *
	 * @param defaultValueSupplier The supplier of a default value that will get returned when no cache present is.
	 *
	 * @return The current value of the cache, or the result of [defaultValueSupplier.get][Supplier.get] if no cache is
	 * currently present.
	 *
	 * @throws IOException When an I/O error occurs while reading.
	 *
	 * @since TODO TBD
	 *
	 * @see getValueOrNull
	 * @see getValue
	 * @see getValueOrDefault
	 */
	@CheckResult
	@Throws(IOException::class)
	public inline fun getValueOrElse(defaultValueSupplier: Supplier<T>): T {
		return getValueOrNull() ?: defaultValueSupplier.get()
	}

	/**
	 * Tries to retrieve the current value of the cache.
	 *
	 * @param defaultValue The value to return when no cache present is.
	 *
	 * @throws IOException When an I/O error occurs while reading.
	 *
	 * @return The current value of the cache, or [defaultValue]] if no cache is currently present.
	 *
	 * @since TODO TBD
	 *
	 * @see getValueOrElse
	 * @see getValueOrNull
	 * @see getValue
	 */
	@CheckResult
	@Throws(IOException::class)
	public inline fun getValueOrDefault(defaultValue: T): T {
		return getValueOrElse { defaultValue }
	}

	// endregion

	// region if present

	/**
	 * Invokes an action when the cache is currently present.
	 *
	 * Instead of using this method and calling [setValue] inside [action], use [update].
	 *
	 * @param action The action to invoke when the cache present is.\
	 * Receives the current cache snapshot as an argument.
	 *
	 * @throws IOException When an I/O error occurs while reading.
	 *
	 * @since TODO TBD
	 *
	 * @see ifNotPresent
	 * @see ifValuePresent
	 * @see getSnapshotOrNull
	 */
	@Throws(IOException::class)
	public inline fun ifPresent(action: (CacheSnapshot<T>) -> Unit) {
		val snapshot = getSnapshotOrNull()

		if(snapshot != null) {
			action(snapshot)
		}
	}

	/**
	 * Invokes an action the cache is currently *not* present.
	 *
	 * @param action The action to invoke when the cache not present is.
	 *
	 * @since TODO TBD
	 *
	 * @see ifPresent
	 * @see isNotPresent
	 */
	public inline fun ifNotPresent(action: () -> Unit) {
		if(isNotPresent()) {
			action()
		}
	}


	/**
	 * Invokes an action when the cache is currently present.
	 *
	 * Instead of using this method and calling [setValue] inside [action], use [updateValue].
	 *
	 * @param action The action to invoke when the cache present is.\
	 *               Receives the current cache value as an argument.
	 *
	 * @throws IOException When an I/O error occurs while reading.
	 *
	 * @since TODO TBD
	 *
	 * @see ifPresent
	 * @see getValueOrNull
	 */
	@Throws(IOException::class)
	public inline fun ifValuePresent(action: (T) -> Unit) {
		val value = getValueOrNull()

		if(value != null) {
			action(value)
		}
	}

	// endregion

	// endregion

	// region write operations

	// region write utils

	private inline fun unsynchronizedTouch(`when`: Instant) {
		file.createNewFile() // no-op when file already exists
		file.setLastModified(`when`.toEpochMilli())
	}

	private inline fun unsynchronizedSetValue(value: T, optionalModificationTime: Instant?) {
		val now = Instant.now()

		val finalModificationTime: Instant =
			when {
				optionalModificationTime != null -> optionalModificationTime
				unsynchronizedIsPresent(now)     -> unsynchronizedGetModificationTime()
				else                             -> now
			}

		file.parentFile?.mkdirs()
		file.outputStream()
			.buffered()
			.use { outputStream ->
				val cacheWriter = CacheWriterImplWithSharedReusableBuffer(outputStream, sharedReusableBuffer)

				adapter.write(value, cacheWriter)
			}

		unsynchronizedTouch(finalModificationTime)
	}

	private inline fun unsynchronizedDelete() {
		file.delete()
	}

	// endregion

	/**
	 * Overwrites the old cache value with a new one.
	 *
	 * Calling this function inside blocks of [ifPresent] or [ifValuePresent] is not recommended.\
	 * Use [update] or [updateValue] instead.
	 *
	 * @param value The new value to write to the cache.
	 *
	 * @param when The time to set the modification time to.\
	 * If `null` is passed, then the modification time is not changed.\
	 * Defaults to [Instant.now].
	 *
	 * Note that the argument will be truncated to seconds, since ext4 only supports seconds.
	 *
	 * @throws IOException When an I/O error occurs while writing.
	 *
	 * @since TODO TBD
	 *
	 * @see getAndSetValue
	 */
	@JvmOverloads
	@Throws(IOException::class)
	public fun setValue(value: T, `when`: Instant? = Instant.now()) {
		checkBlockedForUpdateThreads()

		readWriteSemaphore.write {
			unsynchronizedSetValue(value, `when`)
		}
	}

	/**
	 * Tries to retrieve the current value of the cache, and then overwrites it with a new one.
	 *
	 * @param value The new value to write to the cache.
	 *
	 * @param when The time to set the modification time to.\
	 * If `null` is passed, then the modification time is not changed.\
	 * Defaults to [Instant.now].
	 *
	 * Note that the argument will be truncated to seconds, since ext4 only supports seconds.
	 *
	 * @return The current value of the cache, or `null` if no cache is currently present.
	 *
	 * @throws IOException When an I/O error occurs while reading or writing.
	 *
	 * @since TODO TBD
	 *
	 * @see setValue
	 * @see updateValue
	 * @see getValueOrNull
	 */
	@CheckResult
	@JvmOverloads
	@Throws(IOException::class)
	public fun getAndSetValue(value: T, `when`: Instant? = Instant.now()): T? {
		checkBlockedForUpdateThreads()

		return readWriteSemaphore.write {
			val oldValue = unsynchronizedGetValueOrNull()
			unsynchronizedSetValue(value, `when`)
			oldValue
		}
	}

	/**
	 * Deletes the current cache.
	 *
	 * @since TODO TBD
	 *
	 * @see shred
	 */
	public fun delete() {
		checkBlockedForUpdateThreads()

		readWriteSemaphore.write(::unsynchronizedDelete)
	}

	/**
	 * Tries to retrieve the current value of the cache, and then deletes it.
	 *
	 * @return The current value of the cache, or `null` if no cache is currently present.
	 *
	 * @throws IOException When an I/O error occurs while reading.
	 *
	 * @since TODO TBD
	 *
	 * @see delete
	 * @see getValueOrNull
	 */
	@CheckResult
	@Throws(IOException::class)
	public fun getAndDelete(): T? {
		checkBlockedForUpdateThreads()

		return readWriteSemaphore.write {
			val oldValue = unsynchronizedGetValueOrNull()
			unsynchronizedDelete()
			oldValue
		}
	}

	/**
	 * Updates the current cache using a provided function.
	 *
	 * Calling any `FileCache` methods of the same instance that this `update` was called upon *inside* the [update]
	 * parameter will lead to an [IllegalStateException] being thrown. This is to avoid deadlocks.
	 *
	 * @param update Function to update the cache.
	 *
	 * Receives the current cache snapshot as an argument.\
	 * If the argument is `null`, it means that no cache is currently present.
	 *
	 * The [`value`][CacheUpdate.value] of the returned `CacheUpdate` will be written to the cache, and the
	 * [`modificationTime`][CacheUpdate.modificationTime] will be set to the cache's modification time. \
	 * If instead of a `CacheUpdate` instance, `null` is returned, then the cache will be deleted.
	 *
	 * If both the argument and the returned value are `null`, nothing happens.
	 *
	 * @throws IOException When an I/O error occurs while reading or writing.
	 *
	 * @since TODO TBD
	 *
	 * @see updateValue
	 * @see getSnapshotOrNull
	 * @see setValue
	 * @see delete
	 */
	@Throws(IOException::class)
	public fun update(update: (CacheSnapshot<T>?) -> CacheUpdate<T>?) {
		checkBlockedForUpdateThreads()

		readWriteSemaphore.write {
			val snapshot = unsynchronizedGetSnapshotOrNull()
			val updatedSnapshot = blockCurrentThreadForUpdate { update(snapshot) }

			if(updatedSnapshot != null) {
				unsynchronizedSetValue(updatedSnapshot.value, updatedSnapshot.modificationTime)
				return@write
			}

			unsynchronizedDelete()
		}
	}

	/**
	 * Updates the current cache value using a provided function.
	 *
	 * Calling any `FileCache` methods of the same instance that this `updateValue` was called upon *inside* the
	 * [update] parameter will lead to an [IllegalStateException] being thrown. This is to avoid deadlocks.
	 *
	 * @param when The time to set the modification time to.\
	 * If `null` is passed, then the modification time is not changed.\
	 * Defaults to [Instant.now].
	 *
	 * @param update Function to update the cache.
	 *
	 * Receives the current cache value as an argument.\
	 * If the argument is `null`, it means that no cache is currently present.
	 *
	 * The returned value will be written to the cache.\
	 * If instead of an object of type [T], `null` is returned, then the cache will be deleted.
	 *
	 * If both the argument and the returned value are `null`, nothing happens.
	 *
	 * @throws IOException When an I/O error occurs while reading or writing.
	 *
	 * @since TODO TBD
	 *
	 * @see FileCache.update
	 * @see updateExistingValue
	 * @see getAndSetValue
	 * @see getValueOrNull
	 * @see setValue
	 * @see delete
	 */
	@JvmOverloads
	@Throws(IOException::class)
	public fun updateValue(`when`: Instant? = Instant.now(), update: (T?) -> T?) {
		checkBlockedForUpdateThreads()

		readWriteSemaphore.write {
			val value = unsynchronizedGetValueOrNull()
			val updatedValue = blockCurrentThreadForUpdate { update(value) }

			if(updatedValue != null) {
				unsynchronizedSetValue(updatedValue, `when`)
				return@write
			}

			unsynchronizedDelete()
		}
	}

	/**
	 * Updates the current existing cache value using a provided function.
	 *
	 * Calling any `FileCache` methods of the same instance that this `updateExistingValue` was called upon *inside* the
	 * [update] parameter will lead to an [IllegalStateException] being thrown. This is to avoid deadlocks.
	 *
	 * @param when The time to set the modification time to.\
	 * Defaults to [Instant.now].
	 *
	 * @param update Function to update the cache.\
	 * This function won't be called if no cache is currently present.
	 *
	 * Receives the current cache value as an argument.
	 *
	 * The returned value will be written to the cache.\
	 * If instead of an object of type [T], `null` is returned, then the cache will be deleted.
	 *
	 * @throws IOException When an I/O error occurs while reading or writing.
	 *
	 * @since TODO TBD
	 *
	 * @see updateValue
	 * @see setValue
	 * @see delete
	 */
	@JvmOverloads
	@Throws(IOException::class)
	public inline fun updateExistingValue(`when`: Instant? = Instant.now(), crossinline update: (T) -> T?) {
		updateValue(`when`) { value ->
			if(value != null) {
				update(value)
			} else {
				null
			}
		}
	}

	/**
	 * Updates the modification time of the current cache.
	 *
	 * This does *not* update the value, *just* the modification time.
	 *
	 * @param when The instant to set the modification time to.\
	 * Defaults to [Instant.now].
	 *
	 * @throws IOException When an I/O error occurs setting the modification time.
	 *
	 * @since TODO TBD
	 */
	@JvmOverloads
	@Throws(IOException::class)
	public fun touch(`when`: Instant = Instant.now()) {
		checkBlockedForUpdateThreads()

		readWriteSemaphore.write {
			unsynchronizedTouch(`when`)
		}
	}

	/**
	 * Overwrites the contents of the cache file with random bytes, multiple times, and then deletes the file.
	 *
	 * See also [shred(1)](https://linux.die.net/man/1/shred).
	 *
	 * @since TODO TBD
	 *
	 * @see delete
	 */
	public fun shred() {
		checkBlockedForUpdateThreads()

		readWriteSemaphore.write {
			val requiredWriteCount: Long = ceil(file.length().toDouble() / SHRED_BUFFER_SIZE).toLong()

			if(requiredWriteCount == 0L) {
				// no need to shred a non-existent or empty file
				return@write
			}

			RandomAccessFile(file, "rwd").use { file ->
				val random = SecureRandom()
				val buffer = ByteArray(SHRED_BUFFER_SIZE)

				repeat(SHRED_ITERATIONS) {
					repeat(requiredWriteCount) {
						random.nextBytes(buffer)
						file.write(buffer)
					}

					file.seek(0)
				}

				buffer.fill(0)
				repeat(requiredWriteCount) {
					file.write(buffer)
				}
			}

			// TODO also wipe file name?

			unsynchronizedDelete()
		}
	}

	// endregion
}
