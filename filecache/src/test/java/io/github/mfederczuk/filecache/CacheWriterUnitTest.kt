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

import io.github.mfederczuk.filecache.adapter.CacheWriter
import org.junit.Test
import java.io.ByteArrayOutputStream
import java.time.Instant
import java.time.LocalDate

public class CacheWriterUnitTest {

	private interface TestHelper {
		fun action(action: CacheWriter.() -> Unit)
	}


	private val sharedReusableBuffer = ByteArray(Long.SIZE_BYTES)


	@Test
	public fun onlyBoolean() {
		test {
			action { writeBoolean(true) }
			action { writeBoolean(false) }
		}
	}


	@Test
	public fun onlyByte() {
		test {
			(Byte.MIN_VALUE..Byte.MAX_VALUE)
				.map(Int::toByte) // (Byte)..(Byte) produces an IntRange
				.forEach { byte ->
					action { writeByte(byte) }
				}
		}
	}

	@Test
	public fun onlyShort() {
		test {
			(Short.MIN_VALUE..Short.MAX_VALUE)
				.map(Int::toShort) // (Short)..(Short) produces an IntRange
				.forEach { short ->
					action { writeShort(short) }
				}
		}
	}

	@Test
	public fun onlyInt() {
		test {
			action { writeInt(0) }
			action { writeInt(21) }
			action { writeInt(69420) }
			action { writeInt(-1283326001) }
			action { writeInt(Int.MIN_VALUE) }
			action { writeInt(Int.MAX_VALUE) }
		}
	}

	@Test
	public fun onlyLong() {
		test {
			action { writeLong(0) }
			action { writeLong(36) }
			action { writeLong(1577446) }
			action { writeLong(-139694338094404) }
			action { writeLong(1808196346716496481) }
			action { writeLong(Long.MIN_VALUE) }
			action { writeLong(Long.MAX_VALUE) }
		}
	}


	@Test
	public fun onlyFloat() {
		test {
			action { writeFloat(0.0f) }
			action { writeFloat(2.5f) }
			action { writeFloat(15.75f) }
			action { writeFloat(-500.654f) }
			action { writeFloat(65444.46548f) }
			action { writeFloat(Float.NaN) }
			action { writeFloat(Float.NEGATIVE_INFINITY) }
			action { writeFloat(Float.POSITIVE_INFINITY) }
			action { writeFloat(Float.MIN_VALUE) }
			action { writeFloat(Float.MAX_VALUE) }
		}
	}

	@Test
	public fun onlyDouble() {
		test {
			action { writeDouble(0.0) }
			action { writeDouble(2.5) }
			action { writeDouble(32.25) }
			action { writeDouble(654.147) }
			action { writeDouble(-81057.45793) }
			action { writeDouble(7835114.1250752) }
			action { writeDouble(Double.NaN) }
			action { writeDouble(Double.NEGATIVE_INFINITY) }
			action { writeDouble(Double.POSITIVE_INFINITY) }
			action { writeDouble(Double.MIN_VALUE) }
			action { writeDouble(Double.MAX_VALUE) }
		}
	}


	@Test
	public fun onlyChar() {
		test {
			action { writeChar('\u0000') }
			action { writeChar('a') }
			action { writeChar('_') }
			action { writeChar('\'') }
			action { writeChar('\u00F6') }
			action { writeChar('\u2191') }
		}
	}


	@Test
	public fun onlyString() {
		test {
			action { writeString("") }
			action { writeString("foo") }
			action { writeString("bar") }
			action { writeString("foo bar") }
			action { writeString("\u0D9E") }
			action { writeString("\u22A2\u2192 amogus \u2190\u22A3") }
			action { writeString("c-string\u0000") }
			action { writeString("yee\u0000haw") }
			action { writeString("\"quoted\"") }
		}
	}


	@Test
	public fun onlySerializable() {
		test {
			action { writeSerializable(LocalDate.MIN) }
			action { writeSerializable(LocalDate.now()) }
			action { writeSerializable(LocalDate.MAX) }

			action { writeSerializable("wait that's a string") }

			action { writeSerializable(Instant.MIN) }
			action { writeSerializable(Instant.now()) }
			action { writeSerializable(Instant.MAX) }

			action { writeSerializable(User.getRandomInstance()) }
			action { writeSerializable(Comment.getRandomInstance()) }
		}
	}

	@Test
	public fun onlyObject() {
		test {
			action { writeObject(User.getRandomInstance(), UserCacheAdapter) }
			action { writeObject(Comment.getRandomInstance(), CommentCacheAdapter) }
		}
	}


	@Test
	public fun mixed() {
		test {
			action { writeInt(55) }
			action { writeObject(User.getRandomInstance(), UserCacheAdapter) }
			action { writeLong(6116855165685) }
			action { writeDouble(6745.6457514) }
			action { writeChar('\"') }
			action { writeString("abc") }
			action { writeShort(0x22) }
			action { writeBoolean(true) }
			action { writeSerializable(Comment.getRandomInstance()) }
			action { writeFloat(5466.9846f) }
			action { writeByte(127) }
		}
	}


	private fun test(testHelperBlock: TestHelper.() -> Unit) {
		val testHelper =
			object : TestHelper {
				val actions: MutableList<CacheWriter.() -> Unit> = ArrayList()

				override fun action(action: CacheWriter.() -> Unit) {
					actions.add(action)
				}
			}

		testHelper.apply(testHelperBlock)

		run {
			val cacheWriter = createCacheWriter()
			testHelper.actions
				.forEach { action ->
					cacheWriter.apply(action)
				}
		}

		testHelper.actions
			.forEach { action ->
				val oneTimeUseCacheWriter = createCacheWriter()
				oneTimeUseCacheWriter.apply(action)
			}
	}

	private fun createCacheWriter(): CacheWriter {
		val outputStream = ByteArrayOutputStream()
		return CacheWriterImplWithSharedReusableBuffer(outputStream, sharedReusableBuffer)
	}
}
