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

import java.io.Serializable

/**
 * Generic `CacheAdapter` implementation for a list of objects.
 *
 * @param T The type of elements contained in the list.
 *
 * @param elementAdapter The adapter for the elements contained in the list ([T]).
 *
 * @since TODO TBD
 *
 * @constructor Constructs a new `GenericListCacheAdapter` instance.
 */
public class GenericListCacheAdapter<T : Any>(
	private val elementAdapter: CacheAdapter<T>
) : CacheAdapter<List<T>>() {

	override fun read(reader: CacheReader): List<T> {
		val size = reader.readInt()

		if(size == 0) {
			return emptyList()
		}

		val list = ArrayList<T>(size)

		repeat(size) {
			list.add(reader.readObject(elementAdapter))
		}

		return list
	}

	override fun write(value: List<T>, writer: CacheWriter) {
		writer.writeInt(value.size)

		value.forEach { element ->
			writer.writeObject(element, elementAdapter)
		}
	}
}


// TODO BooleanListCacheAdapter?


/**
 * `CacheAdapter` implementation that uses [CacheReader.readBoolean] and [CacheWriter.writeBoolean].
 *
 * @since TODO TBD
 */
public object BooleanCacheAdapter : CacheAdapter<Boolean>() {
	override fun read(reader: CacheReader): Boolean = reader.readBoolean()
	override fun write(value: Boolean, writer: CacheWriter): Unit = writer.writeBoolean(value)
}


/**
 * `CacheAdapter` implementation that uses [CacheReader.readByte] and [CacheWriter.writeByte].
 *
 * @since TODO TBD
 */
public object ByteCacheAdapter : CacheAdapter<Byte>() {
	override fun read(reader: CacheReader): Byte = reader.readByte()
	override fun write(value: Byte, writer: CacheWriter): Unit = writer.writeByte(value)
}

/**
 * `CacheAdapter` implementation that uses [CacheReader.readShort] and [CacheWriter.writeShort].
 *
 * @since TODO TBD
 */
public object ShortCacheAdapter : CacheAdapter<Short>() {
	override fun read(reader: CacheReader): Short = reader.readShort()
	override fun write(value: Short, writer: CacheWriter): Unit = writer.writeShort(value)
}

/**
 * `CacheAdapter` implementation that uses [CacheReader.readInt] and [CacheWriter.writeInt].
 *
 * @since TODO TBD
 */
public object IntCacheAdapter : CacheAdapter<Int>() {
	override fun read(reader: CacheReader): Int = reader.readInt()
	override fun write(value: Int, writer: CacheWriter): Unit = writer.writeInt(value)
}

/**
 * `CacheAdapter` implementation that uses [CacheReader.readLong] and [CacheWriter.writeLong].
 *
 * @since TODO TBD
 */
public object LongCacheAdapter : CacheAdapter<Long>() {
	override fun read(reader: CacheReader): Long = reader.readLong()
	override fun write(value: Long, writer: CacheWriter): Unit = writer.writeLong(value)
}


/**
 * `CacheAdapter` implementation that uses [CacheReader.readFloat] and [CacheWriter.writeFloat].
 *
 * @since TODO TBD
 */
public object FloatCacheAdapter : CacheAdapter<Float>() {
	override fun read(reader: CacheReader): Float = reader.readFloat()
	override fun write(value: Float, writer: CacheWriter): Unit = writer.writeFloat(value)
}

/**
 * `CacheAdapter` implementation that uses [CacheReader.readDouble] and [CacheWriter.writeDouble].
 *
 * @since TODO TBD
 */
public object DoubleCacheAdapter : CacheAdapter<Double>() {
	override fun read(reader: CacheReader): Double = reader.readDouble()
	override fun write(value: Double, writer: CacheWriter): Unit = writer.writeDouble(value)
}


/**
 * `CacheAdapter` implementation that uses [CacheReader.readChar] and [CacheWriter.writeChar].
 *
 * @since TODO TBD
 */
public object CharCacheAdapter : CacheAdapter<Char>() {
	override fun read(reader: CacheReader): Char = reader.readChar()
	override fun write(value: Char, writer: CacheWriter): Unit = writer.writeChar(value)
}


/**
 * `CacheAdapter` implementation that uses [CacheReader.readString] and [CacheWriter.writeString].
 *
 * @since TODO TBD
 */
public object StringCacheAdapter : CacheAdapter<String>() {
	override fun read(reader: CacheReader): String = reader.readString()
	override fun write(value: String, writer: CacheWriter): Unit = writer.writeString(value)
}


/**
 * `CacheAdapter` implementation that uses [CacheReader.readSerializable] and [CacheWriter.writeSerializable].
 *
 * @since TODO TBD
 *
 * @constructor Constructs a new `SerializableCacheAdapter` instance.
 */
public class SerializableCacheAdapter<T : Serializable> : CacheAdapter<T>() {
	override fun read(reader: CacheReader): T = reader.readSerializable()
	override fun write(value: T, writer: CacheWriter): Unit = writer.writeSerializable(value)
}
