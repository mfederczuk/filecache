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

package io.github.mfederczuk.filecache.adapter

import java.io.IOException

/**
 * Object used to read serialized data from a cache to create a substantial object and to write a substantial object as
 * serialized data to a cache.
 *
 * It is recommended to use [CacheReader.readObject]/[CacheWriter.writeObject] instead of
 * [CacheAdapter.read]/[CacheAdapter.write] directly.
 *
 * ### Implementation Notices ###
 *
 * Implementations MUST ensure that the size of the serialized data is constant, computable or otherwise determinable.
 * In other words; implementations MUST NOT rely on `EOF` to  determine the end of the serialized data.\
 * A good example of this is how strings are serialized; first the length of the string is stored, and then the actual
 * data.
 *
 * @param T The type of object to convert from/to serialized data.
 *
 * @since TODO TBD
 *
 * @sample GenericListCacheAdapter
 *
 * @constructor Empty constructor.
 */
public abstract class CacheAdapter<T : Any> {

	/**
	 * Reads from a cache and creates a substantial object.\
	 * Access to the cache is given via the [reader] interface.
	 *
	 * Implementations MUST NOT close [reader.stream][CacheReader.stream].
	 *
	 * @param reader Interface that allows read access to the cache being read from.
	 *
	 * @return The object that was read from the serialized form from the cache.
	 *
	 * @throws IOException When any sort of I/O error occurs.
	 *
	 * @since TODO TBD
	 */
	@Throws(IOException::class)
	public abstract fun read(reader: CacheReader): T

	/**
	 * Writes a substantial object to a cache.\
	 * Access to the cache is given via the [writer] interface.
	 *
	 * Implementations MUST NOT close [writer.stream][CacheWriter.stream].
	 *
	 * @param value The object to write in a serialized form into the cache.
	 *
	 * @param writer Interface that allows write access to the cache being written to.
	 *
	 * @throws IOException When any sort of I/O error occurs.
	 *
	 * @since TODO TBD
	 */
	@Throws(IOException::class)
	public abstract fun write(value: T, writer: CacheWriter)
}
