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
import org.junit.Test
import java.io.Serializable
import java.time.Instant
import java.time.LocalDate

public class CacheReaderAndWriterUnitTest : BaseCacheReaderAndWriterUnitTest() {

	// region helpers

	private fun TestHelper.boolean(boolean: Boolean) {
		action(boolean) {
			write(CacheWriter::writeBoolean)
			read(CacheReader::readBoolean)
		}
	}


	private fun TestHelper.byte(byte: Byte) {
		action(byte) {
			write(CacheWriter::writeByte)
			read(CacheReader::readByte)
		}
	}

	private fun TestHelper.short(short: Short) {
		action(short) {
			write(CacheWriter::writeShort)
			read(CacheReader::readShort)
		}
	}

	private fun TestHelper.int(int: Int) {
		action(int) {
			write(CacheWriter::writeInt)
			read(CacheReader::readInt)
		}
	}

	private fun TestHelper.long(long: Long) {
		action(long) {
			write(CacheWriter::writeLong)
			read(CacheReader::readLong)
		}
	}


	private fun TestHelper.float(float: Float) {
		action(float) {
			write(CacheWriter::writeFloat)
			read(CacheReader::readFloat)
		}
	}

	private fun TestHelper.double(double: Double) {
		action(double) {
			write(CacheWriter::writeDouble)
			read(CacheReader::readDouble)
		}
	}


	private fun TestHelper.char(char: Char) {
		action(char) {
			write(CacheWriter::writeChar)
			read(CacheReader::readChar)
		}
	}


	private fun TestHelper.string(string: String) {
		action(string) {
			write(CacheWriter::writeString)
			read(CacheReader::readString)
		}
	}


	private fun TestHelper.serializable(serializable: Serializable) {
		action(serializable) {
			write(CacheWriter::writeSerializable)
			read(CacheReader::readSerializable)
		}
	}

	private fun <T : Any> TestHelper.`object`(`object`: T, adapter: CacheAdapter<T>) {
		action(`object`) {
			write { writeObject(it, adapter) }
			read { readObject(adapter) }
		}
	}

	// endregion


	// region tests

	@Test
	public fun onlyBoolean() {
		test {
			boolean(true)
			boolean(false)
		}
	}


	@Test
	public fun onlyByte() {
		test {
			byte(0)
			byte(32)
			byte(Byte.MIN_VALUE)
			byte(Byte.MAX_VALUE)
		}

		test {
			(Byte.MIN_VALUE..Byte.MAX_VALUE)
				.map(Int::toByte) // (Byte)..(Byte) produces an IntRange
				.forEach { b ->
					byte(b)
				}
		}
	}

	@Test
	public fun onlyShort() {
		test {
			short(0)
			short(512)
			short(-9999)
			short(Short.MIN_VALUE)
			short(Short.MAX_VALUE)
		}

		test {
			(Short.MIN_VALUE..Short.MAX_VALUE)
				.map(Int::toShort) // (Short)..(Short) produces an IntRange
				.forEach { s ->
					short(s)
				}
		}
	}

	@Test
	public fun onlyInt() {
		test {
			int(0)
			int(21)
			int(69420)
			int(-1283326001)
			int(Int.MIN_VALUE)
			int(Int.MAX_VALUE)
		}
	}

	@Test
	public fun onlyLong() {
		test {
			long(0)
			long(36)
			long(1577446)
			long(-139694338094404)
			long(1808196346716496481)
			long(Long.MIN_VALUE)
			long(Long.MAX_VALUE)
		}
	}


	@Test
	public fun onlyFloat() {
		test {
			float(0.0f)
			float(2.5f)
			float(15.75f)
			float(-500.654f)
			float(65444.46548f)
			float(Float.NaN)
			float(Float.NEGATIVE_INFINITY)
			float(Float.POSITIVE_INFINITY)
			float(Float.MIN_VALUE)
			float(Float.MAX_VALUE)
		}
	}

	@Test
	public fun onlyDouble() {
		test {
			double(0.0)
			double(2.5)
			double(32.25)
			double(654.147)
			double(-81057.45793)
			double(7835114.1250752)
			double(Double.NaN)
			double(Double.NEGATIVE_INFINITY)
			double(Double.POSITIVE_INFINITY)
			double(Double.MIN_VALUE)
			double(Double.MAX_VALUE)
		}
	}


	@Test
	public fun onlyChar() {
		test {
			char('\u0000')
			char('a')
			char('_')
			char('\'')
			char('\u00F6')
			char('\u2191')
		}
	}


	@Test
	public fun onlyString() {
		test {
			string("")
			string("foo")
			string("bar")
			string("foo bar")
			string("\u0D9E")
			string("\u22A2\u2192 amogus \u2190\u22A3")
			string("c-string\u0000")
			string("yee\u0000haw")
			string("\"quoted\"")
		}
	}


	@Test
	public fun onlySerializable() {
		test {
			serializable(LocalDate.MIN)
			serializable(LocalDate.now())
			serializable(LocalDate.MAX)

			serializable("wait that's a string")

			serializable(Instant.MIN)
			serializable(Instant.now())
			serializable(Instant.MAX)

			serializable(User.getRandomInstance())
			serializable(Comment.getRandomInstance())
		}
	}

	@Test
	public fun onlyObject() {
		test {
			`object`(User.getRandomInstance(), UserCacheAdapter)
			`object`(Comment.getRandomInstance(), CommentCacheAdapter)
		}
	}


	@Test
	public fun mixed() {
		test {
			int(55)
			`object`(User.getRandomInstance(), UserCacheAdapter)
			long(6116855165685)
			double(6745.6457514)
			char('\"')
			string("abc")
			short(0x22)
			boolean(true)
			serializable(Comment.getRandomInstance())
			float(5466.9846f)
			byte(127)
		}
	}

	// endregion
}
