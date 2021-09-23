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
import io.github.mfederczuk.filecache.adapter.CacheWriter
import java.io.ObjectOutputStream
import java.io.OutputStream
import java.io.Serializable

internal class CacheWriterImplWithSharedReusableBuffer(
	override val stream: OutputStream,
	private val sharedReusableBuffer: ByteArray
) : CacheWriter {

	init {
		assert(sharedReusableBuffer.size >= Long.SIZE_BYTES)
	}


	override fun writeBuffer(buffer: ByteArray, offset: Int, length: Int) {
		stream.write(buffer, offset, length)
	}


	override fun writeBoolean(boolean: Boolean) {
		stream.write(if(boolean) 1 else 0)
	}


	override fun writeByte(byte: Byte) {
		stream.write(byte.toInt())
	}

	override fun writeShort(short: Short) {
		sharedReusableBuffer[0] = (short.toInt() ushr 8).toByte()
		sharedReusableBuffer[1] = short.toByte()
		stream.write(sharedReusableBuffer, 0, 2)
	}

	override fun writeInt(int: Int) {
		sharedReusableBuffer[0] = (int ushr 24).toByte()
		sharedReusableBuffer[1] = (int ushr 16).toByte()
		sharedReusableBuffer[2] = (int ushr 8).toByte()
		sharedReusableBuffer[3] = int.toByte()
		stream.write(sharedReusableBuffer, 0, 4)
	}

	override fun writeLong(long: Long) {
		sharedReusableBuffer[0] = (long ushr 56).toByte()
		sharedReusableBuffer[1] = (long ushr 48).toByte()
		sharedReusableBuffer[2] = (long ushr 40).toByte()
		sharedReusableBuffer[3] = (long ushr 32).toByte()
		sharedReusableBuffer[4] = (long ushr 24).toByte()
		sharedReusableBuffer[5] = (long ushr 16).toByte()
		sharedReusableBuffer[6] = (long ushr 8).toByte()
		sharedReusableBuffer[7] = long.toByte()
		stream.write(sharedReusableBuffer)
	}


	override fun writeFloat(float: Float) {
		writeInt(float.toRawBits())
	}

	override fun writeDouble(double: Double) {
		writeLong(double.toRawBits())
	}


	override fun writeChar(char: Char) {
		writeShort(char.code.toShort())
	}


	override fun writeString(string: String) {
		if(string.isEmpty()) {
			writeInt(0)
			return
		}

		val byteArray = string.toByteArray()
		writeInt(byteArray.size)
		writeBuffer(byteArray)
	}


	override fun writeSerializable(serializable: Serializable) {
		// don't close because that would close the underlying stream as well
		val objectOutputStream = ObjectOutputStream(stream)
		objectOutputStream.writeObject(serializable)
	}

	override fun <T : Any> writeObject(`object`: T, adapter: CacheAdapter<T>) {
		adapter.write(`object`, this)
	}
}
