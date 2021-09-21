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

@file:JvmName("CacheReaders")

package io.github.mfederczuk.filecache.adapter

import java.io.IOException
import java.io.Serializable
import java.util.function.Supplier

/**
 * Uses [CacheReader.readIntoBuffer] to create a new [ByteArray].
 *
 * @receiver The `CacheReader` instance to use to read from.
 *
 * @param size The size of the new `ByteArray`.
 *
 * @return A new `ByteArray` instance with the specified [size].
 *
 * @throws IOException When any sort of I/O error occurs.
 *
 * @since TODO TBD
 */
@Throws(IOException::class)
public fun CacheReader.readBuffer(size: Int): ByteArray {
	val buffer = ByteArray(size)

	val c = this.readIntoBuffer(buffer)
	if(c != size) {
		throw IOException("Did not read the requested amount of bytes")
	}

	return buffer
}

// region nullable

private inline fun <T : Any> CacheReader.readNullable(crossinline action: CacheReader.() -> T): T? {
	val present = this.readBoolean()

	if(!present) {
		return null
	}

	return this.action()
}


/**
 * Reads a nullable boolean from the next available byte.
 *
 * Booleans written by either [CacheWriter.writeBoolean] or [CacheWriter.writeNullableBoolean] can be read using this
 * function.
 *
 * @receiver The `CacheReader` instance to use to read from.
 *
 * @return The next boolean that was read or `null`.
 *
 * @throws IOException When any sort of I/O error occurs.
 *
 * @since TODO TBD
 *
 * @see CacheReader.readBoolean
 */
@Throws(IOException::class)
public fun CacheReader.readNullableBoolean(): Boolean? {
	return when(this.readByte().toInt() and 0b11) {
		0b11 -> true
		0b10 -> false
		else -> null
	}
}


/**
 * Reads a nullable byte from the next available bytes.
 *
 * *Bytes written by [CacheWriter.writeByte] can **not** be read using this function, only ones written with
 * [CacheWriter.writeNullableByte] are compatible*
 *
 * @receiver The `CacheReader` instance to use to read from.
 *
 * @return The next byte that was read or `null`.
 *
 * @throws IOException When any sort of I/O error occurs.
 *
 * @since TODO TBD
 *
 * @see CacheReader.readByte
 */
@Throws(IOException::class)
public fun CacheReader.readNullableByte(): Byte? {
	return this.readNullable(CacheReader::readByte)
}

/**
 * Reads a nullable short from the next available bytes.
 *
 * *Shorts written by [CacheWriter.writeShort] can **not** be read using this function, only ones written with
 * [CacheWriter.writeNullableShort] are compatible*
 *
 * @receiver The `CacheReader` instance to use to read from.
 *
 * @return The next short that was read or `null`.
 *
 * @throws IOException When any sort of I/O error occurs.
 *
 * @since TODO TBD
 *
 * @see CacheReader.readShort
 */
@Throws(IOException::class)
public fun CacheReader.readNullableShort(): Short? {
	return this.readNullable(CacheReader::readShort)
}

/**
 * Reads a nullable int from the next available bytes.
 *
 * *INts written by [CacheWriter.writeInt] can **not** be read using this function, only ones written with
 * [CacheWriter.writeNullableInt] are compatible*
 *
 * @receiver The `CacheReader` instance to use to read from.
 *
 * @return The next int that was read or `null`.
 *
 * @throws IOException When any sort of I/O error occurs.
 *
 * @since TODO TBD
 *
 * @see CacheReader.readInt
 */
@Throws(IOException::class)
public fun CacheReader.readNullableInt(): Int? {
	return this.readNullable(CacheReader::readInt)
}

/**
 * Reads a nullable long from the next available bytes.
 *
 * *Longs written by [CacheWriter.writeLong] can **not** be read using this function, only ones written with
 * [CacheWriter.writeNullableLong] are compatible*
 *
 * @receiver The `CacheReader` instance to use to read from.
 *
 * @return The next long that was read or `null`.
 *
 * @throws IOException When any sort of I/O error occurs.
 *
 * @since TODO TBD
 *
 * @see CacheReader.readLong
 */
@Throws(IOException::class)
public fun CacheReader.readNullableLong(): Long? {
	return this.readNullable(CacheReader::readLong)
}


/**
 * Reads a nullable float from the next available bytes.
 *
 * *Floats written by [CacheWriter.writeFloat] can **not** be read using this function, only ones written with
 * [CacheWriter.writeNullableFloat] are compatible*
 *
 * @receiver The `CacheReader` instance to use to read from.
 *
 * @return The next float that was read or `null`.
 *
 * @throws IOException When any sort of I/O error occurs.
 *
 * @since TODO TBD
 *
 * @see CacheReader.readFloat
 */
@Throws(IOException::class)
public fun CacheReader.readNullableFloat(): Float? {
	return this.readNullable(CacheReader::readFloat)
}

/**
 * Reads a nullable double from the next available bytes.
 *
 * *Doubles written by [CacheWriter.writeDouble] can **not** be read using this function, only ones written with
 * [CacheWriter.writeNullableDouble] are compatible*
 *
 * @receiver The `CacheReader` instance to use to read from.
 *
 * @return The next double that was read or `null`.
 *
 * @throws IOException When any sort of I/O error occurs.
 *
 * @since TODO TBD
 *
 * @see CacheReader.readDouble
 */
@Throws(IOException::class)
public fun CacheReader.readNullableDouble(): Double? {
	return this.readNullable(CacheReader::readDouble)
}


/**
 * Reads a nullable char from the next available bytes.
 *
 * *Chars written by [CacheWriter.writeChar] can **not** be read using this function, only ones written with
 * [CacheWriter.writeNullableChar] are compatible*
 *
 * @receiver The `CacheReader` instance to use to read from.
 *
 * @return The next char that was read or `null`.
 *
 * @throws IOException When any sort of I/O error occurs.
 *
 * @since TODO TBD
 *
 * @see CacheReader.readChar
 */
@Throws(IOException::class)
public fun CacheReader.readNullableChar(): Char? {
	return this.readNullable(CacheReader::readChar)
}


/**
 * Reads a nullable `String` from the next available bytes.
 *
 * `String`s written by either [CacheWriter.writeString] or [CacheWriter.writeNullableString] can be read using this
 * function.
 *
 * @receiver The `CacheReader` instance to use to read from.
 *
 * @return The next `String` that was read or `null`.
 *
 * @throws IOException When any sort of I/O error occurs.
 *
 * @since TODO TBD
 *
 * @see CacheReader.readString
 */
@Throws(IOException::class)
public fun CacheReader.readNullableString(): String? {
	val size = readInt()

	if(size < 0) return null
	if(size == 0) return ""

	val buffer = this.readBuffer(size)
	return String(buffer)
}


/**
 * Reads a nullable `Serializable` object from the next available bytes.
 *
 * *`Serializable` objects written by [CacheWriter.writeSerializable] can **not** be read using this function, only ones
 * written with [CacheWriter.writeNullableSerializable] are compatible*
 *
 * @receiver The `CacheReader` instance to use to read from.
 *
 * @param T The type to cast to.
 *
 * @return The deserialized `Serializable` object that was read and cast to [T] or `null`.
 *
 * @throws IOException When any sort of I/O error occurs.
 *
 * @since TODO TBD
 *
 * @see CacheReader.readSerializable
 */
@Throws(IOException::class)
public fun <T : Serializable> CacheReader.readNullableSerializable(): T? {
	return this.readNullable(CacheReader::readSerializable)
}

/**
 * Reads a nullable object from the next available bytes.
 *
 * If possible, it is recommended to use a custom nullable `CacheAdapter` for your type instead of using this function.
 *
 * *Objects written by [CacheWriter.writeObject] can **not** be read using this function, only ones written with
 * [CacheWriter.writeNullableObject] are compatible*
 *
 * @receiver The `CacheReader` instance to use to read from.
 *
 * @param T The type of object to convert from serialized data.
 *
 * @param adapterSupplier Supplier object to provide the `CacheAdapter` to use to convert the serialized data into an
 * substantial object.\
 * [adapterSupplier.get][Supplier.get] is only invoked if the stored object is not `null`.
 *
 * @return The object that was read and converted or `null`.
 *
 * @throws IOException When any sort of I/O error occurs.
 *
 * @since TODO TBD
 *
 * @see CacheReader.readObject
 */
@Throws(IOException::class)
public fun <T : Any> CacheReader.readNullableObject(adapterSupplier: Supplier<CacheAdapter<T>>): T? {
	return this.readNullable {
		val adapter = adapterSupplier.get()
		this@readNullable.readObject(adapter)
	}
}

/**
 * Reads a nullable object from the next available bytes.
 *
 * *Objects written by [CacheWriter.writeObject] can **not** be read using this function, only ones written with
 * [CacheWriter.writeNullableObject] are compatible*
 *
 * @receiver The `CacheReader` instance to use to read from.
 *
 * @param T The type of object to convert from serialized data.
 *
 * @param adapter The `CacheAdapter` to use to convert the serialized data into an substantial object.
 *
 * @return The object that was read and converted or `null`.
 *
 * @throws IOException When any sort of I/O error occurs.
 *
 * @since TODO TBD
 *
 * @see CacheReader.readObject
 */
@Throws(IOException::class)
public fun <T : Any> CacheReader.readNullableObject(adapter: CacheAdapter<T>): T? {
	return this.readNullableObject { adapter }
}

// endregion
