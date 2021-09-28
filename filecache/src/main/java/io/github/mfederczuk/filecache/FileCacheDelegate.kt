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
import java.time.Instant
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

private class FileCacheDelegate<T : Any>(
	private val fileCache: FileCache<T>,
	shredOnAssignNull: Boolean
) : ReadWriteProperty<Any?, T?> {

	private val assignOnNullAction: FileCache<T>.() -> Unit =
		if(!shredOnAssignNull) FileCache<T>::delete else FileCache<T>::shred

	override fun getValue(thisRef: Any?, property: KProperty<*>): T? {
		return fileCache.getValueOrNull()
	}

	override fun setValue(thisRef: Any?, property: KProperty<*>, value: T?) {
		val now = Instant.now()

		if(value == null) {
			fileCache.run(assignOnNullAction)
			return
		}

		fileCache.setValue(value, `when` = now)
	}
}

/**
 * Creates a delegate that reads from and writes to [fileCache].
 *
 * Reading the value of the delegate property will call [fileCache.getValueOrNull][FileCache.getValueOrNull].
 *
 * Assigning the delegate property a non-`null` value will call [fileCache.setValue][FileCache.setValue].\
 * Assigning a `null` value will call either [fileCache.delete][FileCache.delete] or [fileCache.shred][FileCache.shred],
 * depending on the [shredOnAssignNull] parameter.
 *
 * @param T The type of object to store in the cache.
 *
 * @param fileCache The `FileCache` instance to read from and write to.
 *
 * @param shredOnAssignNull Whether or not to call [fileCache.shred][FileCache.shred] instead of
 * [fileCache.delete][FileCache.delete] when a `null` value is assigned to the delegate property.\
 * By default, the value is `false` and therefore will call `delete` when `null` is assigned.
 *
 * @return A new delegate, simplifying access to a `FileCache` to just reading and assigning to a property.
 *
 * @since TODO TBD
 */
@CheckResult
public fun <T : Any> cached(fileCache: FileCache<T>, shredOnAssignNull: Boolean = false): ReadWriteProperty<Any?, T?> {
	return FileCacheDelegate(fileCache, shredOnAssignNull)
}
