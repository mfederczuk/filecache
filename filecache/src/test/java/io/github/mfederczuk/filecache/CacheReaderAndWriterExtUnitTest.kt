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
import io.github.mfederczuk.filecache.adapter.CacheWriter
import io.github.mfederczuk.filecache.adapter.readNullableBoolean
import io.github.mfederczuk.filecache.adapter.readNullableByte
import io.github.mfederczuk.filecache.adapter.readNullableChar
import io.github.mfederczuk.filecache.adapter.readNullableDouble
import io.github.mfederczuk.filecache.adapter.readNullableFloat
import io.github.mfederczuk.filecache.adapter.readNullableInt
import io.github.mfederczuk.filecache.adapter.readNullableLong
import io.github.mfederczuk.filecache.adapter.readNullableObject
import io.github.mfederczuk.filecache.adapter.readNullableSerializable
import io.github.mfederczuk.filecache.adapter.readNullableShort
import io.github.mfederczuk.filecache.adapter.readNullableString
import io.github.mfederczuk.filecache.adapter.writeNullableBoolean
import io.github.mfederczuk.filecache.adapter.writeNullableByte
import io.github.mfederczuk.filecache.adapter.writeNullableChar
import io.github.mfederczuk.filecache.adapter.writeNullableDouble
import io.github.mfederczuk.filecache.adapter.writeNullableFloat
import io.github.mfederczuk.filecache.adapter.writeNullableInt
import io.github.mfederczuk.filecache.adapter.writeNullableLong
import io.github.mfederczuk.filecache.adapter.writeNullableObject
import io.github.mfederczuk.filecache.adapter.writeNullableSerializable
import io.github.mfederczuk.filecache.adapter.writeNullableShort
import io.github.mfederczuk.filecache.adapter.writeNullableString
import org.junit.Test
import java.io.Serializable
import java.time.Instant
import java.time.LocalDate

public class CacheReaderAndWriterExtUnitTest : BaseCacheReaderAndWriterUnitTest() {

	// region helpers

	private fun TestHelper.nullableBoolean(boolean: Boolean?) {
		action(boolean) {
			write(CacheWriter::writeNullableBoolean)
			read(CacheReader::readNullableBoolean)
		}
	}


	private fun TestHelper.nullableByte(byte: Byte?) {
		action(byte) {
			write(CacheWriter::writeNullableByte)
			read(CacheReader::readNullableByte)
		}
	}

	private fun TestHelper.nullableShort(short: Short?) {
		action(short) {
			write(CacheWriter::writeNullableShort)
			read(CacheReader::readNullableShort)
		}
	}

	private fun TestHelper.nullableInt(int: Int?) {
		action(int) {
			write(CacheWriter::writeNullableInt)
			read(CacheReader::readNullableInt)
		}
	}

	private fun TestHelper.nullableLong(long: Long?) {
		action(long) {
			write(CacheWriter::writeNullableLong)
			read(CacheReader::readNullableLong)
		}
	}


	private fun TestHelper.nullableFloat(float: Float?) {
		action(float) {
			write(CacheWriter::writeNullableFloat)
			read(CacheReader::readNullableFloat)
		}
	}

	private fun TestHelper.nullableDouble(double: Double?) {
		action(double) {
			write(CacheWriter::writeNullableDouble)
			read(CacheReader::readNullableDouble)
		}
	}


	private fun TestHelper.nullableChar(char: Char?) {
		action(char) {
			write(CacheWriter::writeNullableChar)
			read(CacheReader::readNullableChar)
		}
	}


	private fun TestHelper.nullableString(string: String?) {
		action(string) {
			write(CacheWriter::writeNullableString)
			read(CacheReader::readNullableString)
		}
	}


	private fun TestHelper.nullableSerializable(serializable: Serializable?) {
		action(serializable) {
			write(CacheWriter::writeNullableSerializable)
			read(CacheReader::readNullableSerializable)
		}
	}

	private fun <T : Any> TestHelper.nullableObject(`object`: T?, adapter: CacheAdapter<T>) {
		action(`object`) {
			write { writeNullableObject(it, adapter) }
			read { readNullableObject(adapter) }
		}
	}

	// endregion


	// region tests

	@Test
	public fun onlyBoolean() {
		test {
			nullableBoolean(true)
			nullableBoolean(null)
			nullableBoolean(false)
		}
	}


	@Test
	public fun onlyByte() {
		test {
			nullableByte(null)
			nullableByte(85)
			nullableByte(null)
			nullableByte(117)
			nullableByte(-74)
		}
	}

	@Test
	public fun onlyShort() {
		test {
			nullableShort(987)
			nullableShort(null)
			nullableShort(-7412)
			nullableShort(null)
			nullableShort(null)
			nullableShort(14564)
		}
	}

	@Test
	public fun onlyInt() {
		test {
			nullableInt(null)
			nullableInt(77135)
			nullableInt(-87145165)
			nullableInt(null)
			nullableInt(null)
			nullableInt(6168415)
			nullableInt(null)
		}
	}

	@Test
	public fun onlyLong() {
		test {
			nullableLong(-7635161513)
			nullableLong(null)
			nullableLong(null)
			nullableLong(86530541853)
			nullableLong(894625135131)
			nullableLong(5626526165)
			nullableLong(null)
			nullableLong(-515165065656)
		}
	}


	@Test
	public fun onlyFloat() {
		test {
			nullableFloat(541.54f)
			nullableFloat(9865.654f)
			nullableFloat(null)
			nullableFloat(null)
			nullableFloat(4651.45f)
			nullableFloat(null)
			nullableFloat(null)
			nullableFloat(744.6546f)
			nullableFloat(7987.44f)
			nullableFloat(null)
		}
	}

	@Test
	public fun onlyDouble() {
		test {
			nullableDouble(null)
			nullableDouble(null)
			nullableDouble(6525.84661)
			nullableDouble(null)
			nullableDouble(965165.8465)
			nullableDouble(null)
			nullableDouble(48617861.784)
			nullableDouble(9826518.858)
			nullableDouble(5415664.874651)
			nullableDouble(null)
			nullableDouble(4165156.64651653)
			nullableDouble(null)
		}
	}


	@Test
	public fun onlyChar() {
		test {
			nullableChar(null)
			nullableChar('a')
			nullableChar(null)
			nullableChar('b')
			nullableChar('c')
			nullableChar(null)
			nullableChar('d')
		}
	}


	@Test
	public fun onlyString() {
		test {
			nullableString("abc")
			nullableString("xyz")
			nullableString(null)
			nullableString("123")
			nullableString(null)
			nullableString(null)
			nullableString(null)
			nullableString("foo")
			nullableString("bar")
			nullableString(null)
		}
	}


	@Test
	public fun onlySerializable() {
		test {
			nullableSerializable(LocalDate.now())
			nullableSerializable(null)
			nullableSerializable(null)
			nullableSerializable(LocalDate.MIN)

			nullableSerializable(null)
			nullableSerializable("wait that's a string")

			nullableSerializable(null)
			nullableSerializable(Instant.now())
			nullableSerializable(null)
			nullableSerializable(Instant.MIN)

			nullableSerializable(User.getRandomInstance())
			nullableSerializable(null)
			nullableSerializable(null)
			nullableSerializable(Comment.getRandomInstance())
		}
	}

	@Test
	public fun onlyObject() {
		test {
			nullableObject(null, UserCacheAdapter)
			nullableObject(User.getRandomInstance(), UserCacheAdapter)
			nullableObject(null, CommentCacheAdapter)
			nullableObject(Comment.getRandomInstance(), CommentCacheAdapter)
		}
	}


	@Test
	public fun mixed() {
		test {
			nullableSerializable(Instant.now())
			nullableFloat(null)
			nullableLong(6452651231)
			nullableChar(null)
			nullableObject(null, UserCacheAdapter)
			nullableInt(-9845)
			nullableDouble(52156.6456)
			nullableByte(null)
			nullableShort(null)
			nullableBoolean(true)
			nullableString("writing unit tests is great /s")
		}
	}

	// endregion
}
