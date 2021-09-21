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

@file:JvmName("CacheWriters")

package io.github.mfederczuk.filecache.adapter

import java.io.IOException
import java.io.Serializable
import java.util.function.Supplier

private inline fun <T : Any> CacheWriter.writeNullable(value: T?, action: CacheWriter.(T) -> Unit) {
	if(value == null) {
		this.writeBoolean(false)
		return
	}

	this.writeBoolean(true)
	this.action(value)
}


/**
 * Writes a nullable boolean to the cache.
 *
 * Booleans written by this function may be read again using either [CacheReader.readBoolean] or
 * [CacheReader.readNullableBoolean].
 *
 * @receiver The `CacheWriter` instance to use to write to.
 *
 * @param boolean The nullable boolean to write.
 *
 * @throws IOException When any sort of I/O error occurs.
 *
 * @since TODO TBD
 *
 * @see CacheWriter.writeBoolean
 */
@Throws(IOException::class)
public fun CacheWriter.writeNullableBoolean(boolean: Boolean?) {
	val byte =
		when(boolean) {
			true  -> 0b11
			false -> 0b10
			null  -> 0
		}.toByte()

	this.writeByte(byte)
}


/**
 * Writes a nullable byte to the cache.
 *
 * *Bytes written by this function can **not** be read again using [CacheReader.readByte], instead,
 * [CacheReader.readNullableByte] must be used.*
 *
 * @receiver The `CacheWriter` instance to use to write to.
 *
 * @param byte The nullable byte to write.
 *
 * @throws IOException When any sort of I/O error occurs.
 *
 * @since TODO TBD
 *
 * @see CacheWriter.writeByte
 */
@Throws(IOException::class)
public fun CacheWriter.writeNullableByte(byte: Byte?) {
	this.writeNullable(byte, CacheWriter::writeByte)
}

/**
 * Writes a nullable short to the cache.
 *
 * *Shorts written by this function can **not** be read again using [CacheReader.readShort], instead,
 * [CacheReader.readNullableShort] must be used.*
 *
 * @receiver The `CacheWriter` instance to use to write to.
 *
 * @param short The nullable short to write.
 *
 * @throws IOException When any sort of I/O error occurs.
 *
 * @since TODO TBD
 *
 * @see CacheWriter.writeShort
 */
@Throws(IOException::class)
public fun CacheWriter.writeNullableShort(short: Short?) {
	this.writeNullable(short, CacheWriter::writeShort)
}

/**
 * Writes a nullable int to the cache.
 *
 * *Ints written by this function can **not** be read again using [CacheReader.readInt], instead,
 * [CacheReader.readNullableInt] must be used.*
 *
 * @receiver The `CacheWriter` instance to use to write to.
 *
 * @param int The nullable int to write.
 *
 * @throws IOException When any sort of I/O error occurs.
 *
 * @since TODO TBD
 *
 * @see CacheWriter.writeInt
 */
@Throws(IOException::class)
public fun CacheWriter.writeNullableInt(int: Int?) {
	this.writeNullable(int, CacheWriter::writeInt)
}

/**
 * Writes a nullable long to the cache.
 *
 * *Longs written by this function can **not** be read again using [CacheReader.readLong], instead,
 * [CacheReader.readNullableLong] must be used.*
 *
 * @receiver The `CacheWriter` instance to use to write to.
 *
 * @param long The nullable long to write.
 *
 * @throws IOException When any sort of I/O error occurs.
 *
 * @since TODO TBD
 *
 * @see CacheWriter.writeLong
 */
@Throws(IOException::class)
public fun CacheWriter.writeNullableLong(long: Long?) {
	this.writeNullable(long, CacheWriter::writeLong)
}


/**
 * Writes a nullable float to the cache.
 *
 * *Floats written by this function can **not** be read again using [CacheReader.readFloat], instead,
 * [CacheReader.readNullableFloat] must be used.*
 *
 * @receiver The `CacheWriter` instance to use to write to.
 *
 * @param float The nullable float to write.
 *
 * @throws IOException When any sort of I/O error occurs.
 *
 * @since TODO TBD
 *
 * @see CacheWriter.writeFloat
 */
@Throws(IOException::class)
public fun CacheWriter.writeNullableFloat(float: Float?) {
	this.writeNullable(float, CacheWriter::writeFloat)
}

/**
 * Writes a nullable double to the cache.
 *
 * *Doubles written by this function can **not** be read again using [CacheReader.readDouble], instead,
 * [CacheReader.readNullableDouble] must be used.*
 *
 * @receiver The `CacheWriter` instance to use to write to.
 *
 * @param double The nullable double to write.
 *
 * @throws IOException When any sort of I/O error occurs.
 *
 * @since TODO TBD
 *
 * @see CacheWriter.writeDouble
 */
@Throws(IOException::class)
public fun CacheWriter.writeNullableDouble(double: Double?) {
	this.writeNullable(double, CacheWriter::writeDouble)
}


/**
 * Writes a nullable char to the cache.
 *
 * *Chars written by this function can **not** be read again using [CacheReader.readChar], instead,
 * [CacheReader.readNullableChar] must be used.*
 *
 * @receiver The `CacheWriter` instance to use to write to.
 *
 * @param char The nullable char to write.
 *
 * @throws IOException When any sort of I/O error occurs.
 *
 * @since TODO TBD
 *
 * @see CacheWriter.writeChar
 */
@Throws(IOException::class)
public fun CacheWriter.writeNullableChar(char: Char?) {
	this.writeNullable(char, CacheWriter::writeChar)
}


/**
 * Writes a nullable `String` to the cache.
 *
 * `String`s written by this function may be read again using either [CacheReader.readString] or
 * [CacheReader.readNullableString].
 *
 * @receiver The `CacheWriter` instance to use to write to.
 *
 * @param string The nullable `String` to write.
 *
 * @throws IOException When any sort of I/O error occurs.
 *
 * @since TODO TBD
 *
 * @see CacheWriter.writeString
 */
@Throws(IOException::class)
public fun CacheWriter.writeNullableString(string: String?) {
	if(string == null) {
		writeInt(-1)
		return
	}

	this.writeString(string)
}


/**
 * Writes a nullable `Serializable` object to the cache.
 *
 * *`Serializable` objects written by this function can **not** be read again using [CacheReader.readSerializable],
 * instead, [CacheReader.readNullableSerializable] must be used.*
 *
 * @receiver The `CacheWriter` instance to use to write to.
 *
 * @param serializable The nullable `Serializable` object to serialize and write to the cache.
 *
 * @throws IOException When any sort of I/O error occurs.
 *
 * @since TODO TBD
 *
 * @see CacheWriter.writeSerializable
 */
@Throws(IOException::class)
public fun CacheWriter.writeNullableSerializable(serializable: Serializable?) {
	this.writeNullable(serializable, CacheWriter::writeSerializable)
}

/**
 * Writes a nullable object to the cache.
 *
 * If possible, it is recommended to use a custom nullable `CacheAdapter` for your type instead of using this function.
 *
 * *Objects written by this function can **not** be read again using [CacheReader.readObject], instead,
 * [CacheReader.readNullableObject] must be used.*
 *
 * @receiver The `CacheWriter` instance to use to write to.
 *
 * @param T The of object to convert to serialized data.
 *
 * @param object The nullable object to convert and write.
 *
 * @param adapterSupplier Supplier object to provide the `CacheAdapter` to use to convert the substantial object into
 * serialized data.\
 * [adapterSupplier.get][Supplier.get] is only invoked if [object] is not `null`.
 *
 * @throws IOException When any sort of I/O error occurs.
 *
 * @since TODO TBD
 *
 * @see CacheWriter.writeObject
 */
@Throws(IOException::class)
public fun <T : Any> CacheWriter.writeNullableObject(`object`: T?, adapterSupplier: Supplier<CacheAdapter<T>>) {
	this.writeNullable(`object`) { value ->
		val adapter = adapterSupplier.get()
		this@writeNullable.writeObject(value, adapter)
	}
}

/**
 * Writes a nullable object to the cache.
 *
 * *Objects written by this function can **not** be read again using [CacheReader.readObject], instead,
 * [CacheReader.readNullableObject] must be used.*
 *
 * @receiver The `CacheWriter` instance to use to write to.
 *
 * @param T The of object to convert to serialized data.
 *
 * @param object The nullable object to convert and write.
 *
 * @param adapter The `CacheAdapter` to use to convert the substantial object into serialized data.
 *
 * @throws IOException When any sort of I/O error occurs.
 *
 * @since TODO TBD
 *
 * @see CacheWriter.writeObject
 */
@Throws(IOException::class)
public fun <T : Any> CacheWriter.writeNullableObject(`object`: T?, adapter: CacheAdapter<T>) {
	this.writeNullableObject(`object`) { adapter }
}
