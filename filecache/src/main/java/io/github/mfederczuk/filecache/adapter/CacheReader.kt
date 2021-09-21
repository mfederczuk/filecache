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

import java.io.EOFException
import java.io.IOException
import java.io.InputStream
import java.io.Serializable

/**
 * Interface for reading data from a cache.
 *
 * All `read*` methods may possible throw an [EOFException] when they encounter `EOF` while still reading data, or they
 * may throw an [IOException] when any other sort of I/O error occurs.
 *
 * @since TODO TBD
 */
public interface CacheReader {

	/**
	 * The underlying stream that is connected to the cache.
	 *
	 * All `read*` methods read from this stream.
	 *
	 * @since TODO TBD
	 */
	public val stream: InputStream


	/**
	 * Skips the next [n] available bytes.
	 *
	 * @throws IOException When any sort of I/O error occurs.
	 *
	 * @since TODO TBD
	 */
	@Throws(IOException::class)
	public fun skip(n: Int)


	/**
	 * Reads at most [length] amount of the next bytes available and writes it into [buffer], starting at [offset].
	 *
	 * @param buffer The `ByteArray` to write to.
	 *
	 * @param offset The index of [buffer] where to start writing to.\
	 *  Defaults to `0`.
	 *
	 * @param length The max amount of bytes to read.\
	 * Less bytes may be read when not enough data is available. (`EOF`)\
	 * Defaults to `(buffer.size - offset)`.
	 *
	 * @return The actual amount of bytes that were read. Will always be >= 1.
	 *
	 * @throws IOException When any sort of I/O error occurs.
	 *
	 * @since TODO TBD
	 */
	@Throws(IOException::class)
	public fun readIntoBuffer(
		buffer: ByteArray,
		offset: Int = 0,
		length: Int = (buffer.size - offset)
	): Int


	/**
	 * Reads a boolean from the next available byte.
	 *
	 * @return The next boolean that was read.
	 *
	 * @throws IOException When any sort of I/O error occurs.
	 *
	 * @since TODO TBD
	 */
	@Throws(IOException::class)
	public fun readBoolean(): Boolean


	/**
	 * Reads the next available byte.
	 *
	 * @return The next available byte that was read.
	 *
	 * @throws IOException When any sort of I/O error occurs.
	 *
	 * @since TODO TBD
	 */
	@Throws(IOException::class)
	public fun readByte(): Byte

	/**
	 * Reads a short from the next available bytes.
	 *
	 * @return The next short that was read.
	 *
	 * @throws IOException When any sort of I/O error occurs.
	 *
	 * @since TODO TBD
	 */
	@Throws(IOException::class)
	public fun readShort(): Short

	/**
	 * Reads an int from the next available bytes.
	 *
	 * @return The next int that was read.
	 *
	 * @throws IOException When any sort of I/O error occurs.
	 *
	 * @since TODO TBD
	 */
	@Throws(IOException::class)
	public fun readInt(): Int

	/**
	 * Reads a long from the next available bytes.
	 *
	 * @return The next long that was read.
	 *
	 * @throws IOException When any sort of I/O error occurs.
	 *
	 * @since TODO TBD
	 */
	@Throws(IOException::class)
	public fun readLong(): Long


	/**
	 * Reads a float from the next available bytes.
	 *
	 * @return The float that was read.
	 *
	 * @throws IOException When any sort of I/O error occurs.
	 *
	 * @since TODO TBD
	 */
	@Throws(IOException::class)
	public fun readFloat(): Float

	/**
	 * Reads a double from the next available doubles.
	 *
	 * @return The double that was read.
	 *
	 * @throws IOException When any sort of I/O error occurs.
	 *
	 * @since TODO TBD
	 */
	@Throws(IOException::class)
	public fun readDouble(): Double


	/**
	 * Reads a char from the next available bytes.
	 *
	 * @return The char that was read.
	 *
	 * @throws IOException When any sort of I/O error occurs.
	 *
	 * @since TODO TBD
	 */
	@Throws(IOException::class)
	public fun readChar(): Char


	/**
	 * Reads a `String` from the next available bytes.
	 *
	 * @return The `String` that was read.
	 *
	 * @throws IOException When any sort of I/O error occurs.
	 *
	 * @since TODO TBD
	 */
	@Throws(IOException::class)
	public fun readString(): String


	/**
	 * Reads a `Serializable` object from the next available bytes.
	 *
	 * @param T The type to cast the deserialized object to.
	 *
	 * @return The deserialized `Serializable` object that was read and cast to [T].
	 *
	 * @throws IOException When any sort of I/O error occurs.
	 *
	 * @since TODO TBD
	 */
	@Throws(IOException::class)
	public fun <T : Serializable> readSerializable(): T

	/**
	 * Reads an object from the next available bytes.
	 *
	 * It is recommended to use this method instead of [CacheAdapter.read] directly.
	 *
	 * @param T The type of object to convert from serialized data.
	 *
	 * @param adapter The `CacheAdapter` to use to convert the serialized data into an substantial object.
	 *
	 * @return The object that was read and converted.
	 *
	 * @throws IOException When any sort of I/O error occurs.
	 *
	 * @since TODO TBD
	 */
	@Throws(IOException::class)
	public fun <T : Any> readObject(adapter: CacheAdapter<T>): T
}
