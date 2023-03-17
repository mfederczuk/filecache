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

@file:JvmName("MoshiAdapters")
@file:Suppress("NOTHING_TO_INLINE")

package io.github.mfederczuk.filecache.moshi

import androidx.annotation.CheckResult
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapter
import io.github.mfederczuk.filecache.adapter.CacheAdapter
import io.github.mfederczuk.filecache.adapter.CacheReader
import io.github.mfederczuk.filecache.adapter.CacheWriter
import okio.buffer
import okio.sink
import okio.source
import java.lang.reflect.Type
import kotlin.reflect.KType

private class MoshiCacheAdapter<T : Any>(private val jsonAdapter: JsonAdapter<T>) : CacheAdapter<T>() {

	override fun read(reader: CacheReader): T {
		val bufferedSource = reader.stream.source().buffer()

		return jsonAdapter.fromJson(bufferedSource)!!
	}

	override fun write(value: T, writer: CacheWriter) {
		val bufferedSink = writer.stream.sink().buffer()

		jsonAdapter.toJson(
			bufferedSink,
			value
		)
	}
}

/**
 * Creates a `CacheAdapter` that uses a Moshi `JsonAdapter` internally.
 *
 * @receiver The `JsonAdapter` to use to convert between object and serialized JSON data.
 *
 * @param T The type of object to convert from/to serialized data.
 *
 * @return A new implementation of a `CacheAdapter`.
 *
 * @since TODO TBD
 */
@CheckResult
@JvmName("createCacheAdapter")
public fun <T : Any> JsonAdapter<T>.asCacheAdapter(): CacheAdapter<T> {
	return MoshiCacheAdapter(this)
}


/**
 * TODO
 *
 * @receiver TODO
 *
 * @param T TODO
 *
 * @param type TODO
 *
 * @return TODO
 *
 * @since TODO TBD
 *
 * @see Moshi.adapter
 * @see asCacheAdapter
 */
@CheckResult
@JvmName("getCacheAdapter")
public inline fun <T : Any> Moshi.cacheAdapter(type: Type): CacheAdapter<T> {
	return this.adapter<T>(type).asCacheAdapter()
}

/**
 * TODO
 *
 * @receiver TODO
 *
 * @param T TODO
 *
 * @param type TODO
 *
 * @return TODO
 *
 * @since TODO TBD
 *
 * @see Moshi.adapter
 * @see asCacheAdapter
 */
@CheckResult
@JvmName("getCacheAdapter")
public inline fun <T : Any> Moshi.cacheAdapter(type: Class<T>): CacheAdapter<T> {
	return this.adapter(type).asCacheAdapter()
}

/**
 * TODO
 *
 * @receiver TODO
 *
 * @param T TODO
 *
 * @param type TODO
 *
 * @param annotationType TODO
 *
 * @return TODO
 *
 * @since TODO TBD
 *
 * @see Moshi.adapter
 * @see asCacheAdapter
 */
@CheckResult
@JvmName("getCacheAdapter")
public inline fun <T : Any> Moshi.cacheAdapter(
	type: Type,
	annotationType: Class<out Annotation>
): CacheAdapter<T> {
	return this.adapter<T>(type, annotationType).asCacheAdapter()
}

/**
 * TODO
 *
 * @receiver TODO
 *
 * @param T TODO
 *
 * @param type TODO
 *
 * @param annotationTypes TODO
 *
 * @return TODO
 *
 * @since TODO TBD
 *
 * @see Moshi.adapter
 * @see asCacheAdapter
 */
@CheckResult
@JvmName("getCacheAdapter")
public inline fun <T : Any> Moshi.cacheAdapter(
	type: Type,
	vararg annotationTypes: Class<out Annotation>
): CacheAdapter<T> {
	return this.adapter<T>(type, *annotationTypes).asCacheAdapter()
}

/**
 * TODO
 *
 * @receiver TODO
 *
 * @param T TODO
 *
 * @param type TODO
 *
 * @param annotations TODO
 *
 * @return TODO
 *
 * @since TODO TBD
 *
 * @see Moshi.adapter
 * @see asCacheAdapter
 */
@CheckResult
@JvmName("getCacheAdapter")
public inline fun <T : Any> Moshi.cacheAdapter(
	type: Type,
	annotations: Set<Annotation>
): CacheAdapter<T> {
	return this.adapter<T>(type, annotations).asCacheAdapter()
}

/**
 * TODO
 *
 * @receiver TODO
 *
 * @param T TODO
 *
 * @param type TODO
 *
 * @param annotations TODO
 *
 * @param fieldName TODO
 *
 * @return TODO
 *
 * @since TODO TBD
 *
 * @since TODO TBD
 *
 * @see Moshi.adapter
 * @see asCacheAdapter
 */
@CheckResult
@JvmName("getCacheAdapter")
public inline fun <T : Any> Moshi.cacheAdapter(
	type: Type,
	annotations: Set<Annotation>,
	fieldName: String?
): CacheAdapter<T> {
	return this.adapter<T>(type, annotations, fieldName).asCacheAdapter()
}


/**
 * TODO
 *
 * @receiver TODO
 *
 * @param T TODO
 *
 * @return TODO
 *
 * @since TODO TBD
 *
 * @see adapter
 * @see asCacheAdapter
 */
@CheckResult
@ExperimentalStdlibApi
public inline fun <reified T : Any> Moshi.cacheAdapter(): CacheAdapter<T> {
	return this.adapter<T>().asCacheAdapter()
}

/**
 * TODO
 *
 * @receiver TODO
 *
 * @param T TODO
 *
 * @param ktype TODO
 *
 * @return TODO
 *
 * @since TODO TBD
 *
 * @see adapter
 * @see asCacheAdapter
 */
@CheckResult
@ExperimentalStdlibApi
@JvmName("getCacheAdapter")
public inline fun <T : Any> Moshi.cacheAdapter(ktype: KType): CacheAdapter<T> {
	return this.adapter<T>(ktype).asCacheAdapter()
}
