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
import io.github.mfederczuk.filecache.adapter.CacheReader
import io.github.mfederczuk.filecache.adapter.readBuffer
import java.io.EOFException
import java.io.IOException
import java.io.InputStream
import java.io.ObjectInputStream
import java.io.Serializable

internal class CacheReaderImpl(
	override val stream: InputStream
) : CacheReader {

	private val reusableBuffer = ByteArray(Long.SIZE_BYTES)

	@Suppress("NOTHING_TO_INLINE")
	private inline fun Int.eof(): Int {
		if(this == -1) {
			throw EOFException()
		}

		return this
	}

	private fun readIntoReusableBuffer(len: Int) {
		val c = stream.read(reusableBuffer, 0, len)
			.eof()

		if(c < len) {
			throw IOException("Did not read the required amount of bytes")
		}
	}


	override fun skip(n: Int) {
		stream.skip(n.toLong())
	}


	override fun readIntoBuffer(buffer: ByteArray, offset: Int, length: Int): Int {
		return stream.read(buffer, offset, length)
			.eof()
	}


	override fun readBoolean(): Boolean {
		return (stream.read().eof() == 1)
	}


	override fun readByte(): Byte {
		return stream.read()
			.eof()
			.toByte()
	}

	override fun readShort(): Short {
		readIntoReusableBuffer(Short.SIZE_BYTES)

		var uInt = 0u
		uInt = uInt or (reusableBuffer[0].toUByte().toUInt() shl 8)
		uInt = uInt or reusableBuffer[1].toUByte().toUInt()
		return uInt.toShort()
	}

	override fun readInt(): Int {
		readIntoReusableBuffer(Int.SIZE_BYTES)

		var uInt = 0u
		uInt = uInt or (reusableBuffer[0].toUByte().toUInt() shl 24)
		uInt = uInt or (reusableBuffer[1].toUByte().toUInt() shl 16)
		uInt = uInt or (reusableBuffer[2].toUByte().toUInt() shl 8)
		uInt = uInt or reusableBuffer[3].toUByte().toUInt()
		return uInt.toInt()
	}

	override fun readLong(): Long {
		readIntoReusableBuffer(Long.SIZE_BYTES)

		var uLong = 0uL
		uLong = uLong or (reusableBuffer[0].toUByte().toULong() shl 56)
		uLong = uLong or (reusableBuffer[1].toUByte().toULong() shl 48)
		uLong = uLong or (reusableBuffer[2].toUByte().toULong() shl 40)
		uLong = uLong or (reusableBuffer[3].toUByte().toULong() shl 32)
		uLong = uLong or (reusableBuffer[4].toUByte().toULong() shl 24)
		uLong = uLong or (reusableBuffer[5].toUByte().toULong() shl 16)
		uLong = uLong or (reusableBuffer[6].toUByte().toULong() shl 8)
		uLong = uLong or reusableBuffer[7].toUByte().toULong()
		return uLong.toLong()
	}


	override fun readFloat(): Float {
		return Float.fromBits(readInt())
	}

	override fun readDouble(): Double {
		return Double.fromBits(readLong())
	}


	override fun readChar(): Char {
		return readShort()
			.toInt()
			.toChar()
	}


	override fun readString(): String {
		val size = readInt()

		if(size < 1) {
			return ""
		}

		val buffer = readBuffer(size)
		return String(buffer)
	}


	override fun <T : Serializable> readSerializable(): T {
		// don't close because that would close the underlying stream as well
		val objectInputStream = ObjectInputStream(stream)

		@Suppress("UNCHECKED_CAST")
		return objectInputStream.readObject() as T
	}

	override fun <T : Any> readObject(adapter: CacheAdapter<T>): T {
		return adapter.read(this)
	}
}
