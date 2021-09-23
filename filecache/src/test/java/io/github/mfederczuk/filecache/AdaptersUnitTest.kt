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

import io.github.mfederczuk.filecache.adapter.BooleanCacheAdapter
import io.github.mfederczuk.filecache.adapter.ByteCacheAdapter
import io.github.mfederczuk.filecache.adapter.CacheAdapter
import io.github.mfederczuk.filecache.adapter.CharCacheAdapter
import io.github.mfederczuk.filecache.adapter.DoubleCacheAdapter
import io.github.mfederczuk.filecache.adapter.FloatCacheAdapter
import io.github.mfederczuk.filecache.adapter.GenericListCacheAdapter
import io.github.mfederczuk.filecache.adapter.IntCacheAdapter
import io.github.mfederczuk.filecache.adapter.LongCacheAdapter
import io.github.mfederczuk.filecache.adapter.SerializableCacheAdapter
import io.github.mfederczuk.filecache.adapter.ShortCacheAdapter
import io.github.mfederczuk.filecache.adapter.StringCacheAdapter
import org.junit.Test
import java.io.Serializable
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime

public class AdaptersUnitTest : BaseCacheReaderAndWriterUnitTest() {

	// region helpers

	private fun <T : Any> TestHelper.`object`(`object`: T, adapter: CacheAdapter<T>) {
		action(`object`) {
			write { writeObject(it, adapter) }
			read { readObject(adapter) }
		}
	}

	private fun <T : Any> TestHelper.list(list: List<T>, elementAdapter: CacheAdapter<T>) {
		`object`(list, GenericListCacheAdapter(elementAdapter))
	}

	private fun <T : Any> TestHelper.list(vararg elements: T, elementAdapter: CacheAdapter<T>) {
		list(elements.toList(), elementAdapter)
	}


	private fun TestHelper.boolean(boolean: Boolean) = `object`(boolean, BooleanCacheAdapter)
	private fun TestHelper.booleanList(vararg booleans: Boolean) = list(booleans.toList(), BooleanCacheAdapter)


	private fun TestHelper.byte(byte: Byte) = `object`(byte, ByteCacheAdapter)
	private fun TestHelper.byteList(vararg bytes: Byte) = list(bytes.toList(), ByteCacheAdapter)

	private fun TestHelper.short(short: Short) = `object`(short, ShortCacheAdapter)
	private fun TestHelper.shortList(vararg shorts: Short) = list(shorts.toList(), ShortCacheAdapter)

	private fun TestHelper.int(int: Int) = `object`(int, IntCacheAdapter)
	private fun TestHelper.intList(vararg ints: Int) = list(ints.toList(), IntCacheAdapter)

	private fun TestHelper.long(long: Long) = `object`(long, LongCacheAdapter)
	private fun TestHelper.longList(vararg longs: Long) = list(longs.toList(), LongCacheAdapter)


	private fun TestHelper.float(float: Float) = `object`(float, FloatCacheAdapter)
	private fun TestHelper.floatList(vararg floats: Float) = list(floats.toList(), FloatCacheAdapter)

	private fun TestHelper.double(double: Double) = `object`(double, DoubleCacheAdapter)
	private fun TestHelper.doubleList(vararg doubles: Double) = list(doubles.toList(), DoubleCacheAdapter)


	private fun TestHelper.char(char: Char) = `object`(char, CharCacheAdapter)
	private fun TestHelper.charList(vararg chars: Char) = list(chars.toList(), CharCacheAdapter)


	private fun TestHelper.string(string: String) = `object`(string, StringCacheAdapter)
	private fun TestHelper.stringList(vararg strings: String) = list(strings.toList(), StringCacheAdapter)


	private fun <T : Serializable> TestHelper.serializable(serializable: T) {
		`object`(serializable, SerializableCacheAdapter())
	}

	private fun <T : Serializable> TestHelper.serializableList(vararg serializables: T) {
		list(serializables.toList(), SerializableCacheAdapter())
	}

	// endregion


	// region tests

	@Test
	public fun onlySingleBooleans() {
		test {
			boolean(true)
			boolean(false)
		}
	}

	@Test
	public fun onlyBooleanList() {
		test {
			booleanList()
			booleanList(true, true, false)
			booleanList(true)
			booleanList(false, false, true, true)
			booleanList(false)
			booleanList(false, true)
		}
	}


	@Test
	public fun onlySingleBytes() {
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
	public fun onlyByteList() {
		test {
			byteList()
			byteList(12, 14, 96)
			byteList(55, 78)
			byteList(5)
			byteList(1, 1, 1, 1, 1, 1, 1, 1, 1, 1)
			byteList(99, 127, 41, -128, -55, 78, 98, -99)

			byteList(
				*(Byte.MIN_VALUE..Byte.MAX_VALUE)
					.map(Int::toByte) // (Byte)..(Byte) produces an IntRange
					.toByteArray()
			)
		}
	}

	@Test
	public fun onlySingleShorts() {
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
	public fun onlyShortList() {
		test {
			shortList()
			shortList(-4526, 8711, 32165, -6551, 12345)
			shortList(1, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024, 2048, 4096, 8192, 16384)
			shortList(0)
			shortList(Short.MIN_VALUE, Short.MIN_VALUE)
			shortList(789, 231, 456)

			shortList(
				*(Short.MIN_VALUE..Short.MAX_VALUE)
					.map(Int::toShort) // (Short)..(Short) produces an IntRange
					.toShortArray()
			)
		}
	}

	@Test
	public fun onlySingleInts() {
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
	public fun onlyIntList() {
		test {
			intList()
			intList(65989652, 465161)
			intList(Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE)
			intList(-541316, -78751, -87412, -451021, -541561, -5411)
			intList(84123, -455161, 14654896, -46531)
			intList(Int.MIN_VALUE)
		}
	}

	@Test
	public fun onlySingleLongs() {
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
	public fun onlyLongList() {
		test {
			longList()
			longList(861343, -713549, 746512, 2879554, -651321)
			longList(1)
			longList(4865, 4613)
			longList(-1, -2, -3, -4, -5, -6, -8)
			longList(
				61616625,
				89462615252148,
				-56218929516,
				54198191,
				-451618546145,
				441861614,
				8746465156164,
				485267164,
				-89476412541,
				-1849616413,
				-4165265455,
				945616447161341354
			)
		}
	}


	@Test
	public fun onlySingleFloats() {
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
	public fun onlyFloatList() {
		test {
			floatList()
			floatList(0f)
			floatList(1.0f, 2.0f, 3.0f, 4.0f)
			floatList(14.54f, 987.54f, -654.46f, -564.564f)
			floatList(-974.6454f, -312.646534f, -4.354f)
			floatList(0.5f, 0.25f, 0.125f, 0.0625f, 0.03125f, 0.015625f)
		}
	}

	@Test
	public fun onlySingleDoubles() {
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
	public fun onlyDoubleList() {
		test {
			doubleList()
			doubleList(65131.64312)
			doubleList(145645.879654, 564651.978, 768894.98494, 45.0, 65468.64)
			doubleList(789456123.123456789)
			doubleList(864.4546, 486.1546, 71856.4974, 988.16454)
			doubleList(195664.876, 0.89466, 0.974656, 94561.546)
		}
	}


	@Test
	public fun onlySingleChars() {
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
	public fun onlyCharList() {
		test {
			charList()
			charList('a', 'b', 'c')
			charList('f', 'o', 'o', ' ', 'b', 'a', 'r')
			charList('\u2193')
			charList('c', 's', 't', 'r', '\u0000')
			charList('\u2205', '\u2211', '\u2261')
		}
	}


	@Test
	public fun onlySingleStrings() {
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
	public fun onlyStringList() {
		test {
			stringList()
			stringList("yeehaw")
			stringList(" s t r i n g ", "\u0000", " a l s o   s t r i n g ")
			stringList("")
			stringList("abc", "xyz 123", "foo bar baz", "yee", "haw")
			stringList("\u22A2 \u2283 \u2282 \u22A3", "\u23E9 \u23EA", "\u2590\u2588\u258D")
			stringList("\u0000")
		}
	}


	@Test
	public fun onlySingleSerializables() {
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
	public fun onlySerializableList() {
		test {
			serializableList<Serializable>()
			serializableList<Instant>()
			serializableList(
				LocalDate.of(1970, 1, 1),
				LocalDate.MIN,
				LocalDate.MAX,
				LocalDate.now()
			)
			serializableList(User.getRandomInstance(), User.getRandomInstance())
			serializableList(Instant.now(), Instant.MAX, Instant.MIN)
			serializableList(Comment.getRandomInstance())
		}
	}


	@Test
	public fun onlyObjectList() {
		test {
			list(elementAdapter = UserCacheAdapter)
			list(User.getRandomInstance(), User.getRandomInstance(), elementAdapter = UserCacheAdapter)
			list(Comment.getRandomInstance(), elementAdapter = CommentCacheAdapter)
			list(
				User.getRandomInstance(),
				User.getRandomInstance(),
				User.getRandomInstance(),
				User.getRandomInstance(),
				elementAdapter = UserCacheAdapter
			)
		}
	}


	@Test
	public fun mixedSingles() {
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

	@Test
	public fun listsMixed() {
		test {
			doubleList(654.498, 9841.685, 654.79865)
			byteList(-11, 55, 84)
			longList()
			serializableList(Instant.now(), Instant.now())
			charList('b', 'c', 'z')
			shortList(46,645,-654,543)
			floatList(74.3453f, 54.453f)
			intList(2, 5, 10, 78)
			booleanList(true, false, true, false)
			list(User.getRandomInstance(), User.getRandomInstance(), elementAdapter= UserCacheAdapter)
			stringList("tail", "foo bar")
		}
	}

	@Test
	public fun mixed() {
		test {
			double(846531.78964)
			byteList(14,45,-54,127)
			shortList(846,543,-6153,-4653)
			short(0)
			boolean(false)
			charList()
			intList(15,65,65,4984)
			floatList(6153.645f,6564.64f)
			float(9846.78946f)
			long(-6426516516512)
			byte(0)
			list(Comment.getRandomInstance(), Comment.getRandomInstance(), Comment.getRandomInstance(), elementAdapter= CommentCacheAdapter)
			booleanList()
			char('\u0000')
			longList(65153464565,98465651615,65651,-652651615,54511,-51615356135)
			`object`(User.getRandomInstance(), UserCacheAdapter)
			doubleList(4615.643513,65136.6453161,65135198.8461598,846516.4865,8641.6415)
			int(-546545631)
			stringList("\u0000", "\u0000\u0000", "\u0000\u0000\u0000", "")
			string("")
			serializable(Instant.now())
			serializableList(LocalDateTime.now(), LocalDateTime.MIN, LocalDateTime.MAX)
		}
	}

	// endregion
}
