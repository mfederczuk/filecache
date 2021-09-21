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
import java.io.OutputStream
import java.io.Serializable

/**
 * Interface for writing data to a cache.
 *
 * All `write*` methods may possible throw an [IOException] when any sort of I/O error occurs.
 *
 * @since TODO TBD
 */
public interface CacheWriter {

	/**
	 * The underlying stream that is connected to the cache.
	 *
	 * All `write*` methods write to this stream.
	 *
	 * @since TODO TBD
	 */
	public val stream: OutputStream


	/**
	 * Writes [length] amount of bytes from [buffer], starting at [offset], to the cache.
	 *
	 * @param buffer The `ByteArray` to write.
	 *
	 * @param offset The index of [buffer] where to start writing from.\
	 * Defaults to `0`.
	 *
	 * @param length The amount of bytes to write.\
	 * Defaults to `(buffer.size - offset)`.
	 *
	 * @throws IOException When any sort of I/O error occurs.
	 *
	 * @since TODO TBD
	 */
	@Throws(IOException::class)
	public fun writeBuffer(
		buffer: ByteArray,
		offset: Int = 0,
		length: Int = (buffer.size - offset)
	)


	/**
	 * Writes a boolean to the cache.
	 *
	 * @param boolean The boolean to write.
	 *
	 * @throws IOException When any sort of I/O error occurs.
	 *
	 * @since TODO TBD
	 */
	@Throws(IOException::class)
	public fun writeBoolean(boolean: Boolean)


	/**
	 * Writes a byte to the cache.
	 *
	 * @param byte The byte to write.
	 *
	 * @throws IOException When any sort of I/O error occurs.
	 *
	 * @since TODO TBD
	 */
	@Throws(IOException::class)
	public fun writeByte(byte: Byte)

	/**
	 * Writes a short to the cache.
	 *
	 * @param short The short to write.
	 *
	 * @throws IOException When any sort of I/O error occurs.
	 *
	 * @since TODO TBD
	 */
	@Throws(IOException::class)
	public fun writeShort(short: Short)

	/**
	 * Writes am int to the cache.
	 *
	 * @param int The int to write.
	 *
	 * @throws IOException When any sort of I/O error occurs.
	 *
	 * @since TODO TBD
	 */
	@Throws(IOException::class)
	public fun writeInt(int: Int)

	/**
	 * Writes a long to the cache.
	 *
	 * @param long The long to write.
	 *
	 * @throws IOException When any sort of I/O error occurs.
	 *
	 * @since TODO TBD
	 */
	@Throws(IOException::class)
	public fun writeLong(long: Long)


	/**
	 * Writes a float to the cache.
	 *
	 * @param float The float to write.
	 *
	 * @throws IOException When any sort of I/O error occurs.
	 *
	 * @since TODO TBD
	 */
	@Throws(IOException::class)
	public fun writeFloat(float: Float)

	/**
	 * Writes a double to the cache.
	 *
	 * @param double The double to write.
	 *
	 * @throws IOException When any sort of I/O error occurs.
	 *
	 * @since TODO TBD
	 */
	@Throws(IOException::class)
	public fun writeDouble(double: Double)


	/**
	 * Writes a char to the cache.
	 *
	 * @param char The char to write.
	 *
	 * @throws IOException When any sort of I/O error occurs.
	 *
	 * @since TODO TBD
	 */
	@Throws(IOException::class)
	public fun writeChar(char: Char)


	/**
	 * Writes a `String` to the cache.
	 *
	 * @param string The `String` to write.
	 *
	 * @throws IOException When any sort of I/O error occurs.
	 *
	 * @since TODO TBD
	 */
	@Throws(IOException::class)
	public fun writeString(string: String)


	/**
	 * Writes a `Serializable` object to the cache.
	 *
	 * @param serializable The `Serializable` object to serialize and write to the cache.
	 *
	 * @throws IOException When any sort of I/O error occurs.
	 *
	 * @since TODO TBD
	 */
	@Throws(IOException::class)
	public fun writeSerializable(serializable: Serializable)

	/**
	 * Writes an object to the cache.
	 *
	 * It is recommended to use this method instead of [CacheAdapter.write] directly.
	 *
	 * @param T The of object to convert to serialized data.
	 *
	 * @param object The object to convert and write.
	 *
	 * @param adapter The `CacheAdapter` to use to convert the substantial object into serialized data.
	 *
	 * @throws IOException When any sort of I/O error occurs.
	 *
	 * @since TODO TBD
	 */
	@Throws(IOException::class)
	public fun <T : Any> writeObject(`object`: T, adapter: CacheAdapter<T>)
}
