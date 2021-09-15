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

import org.junit.Assert.assertEquals
import org.junit.Test
import io.github.mfederczuk.filecache.repeat as longRepeat
import kotlin.repeat as intRepeat

public class LongRepeatUnitTest {

	private companion object {
		const val INT_ITERATIONS: Int = 16
		const val LONG_ITERATIONS: Long = 0x1_00_00_00_00
	}

	@Test
	public fun test() {
		var ic = 0
		intRepeat(INT_ITERATIONS) { i ->
			ic += i
		}

		var lc = 0L
		longRepeat(INT_ITERATIONS.toLong()) { i ->
			lc += i
		}
		assertEquals(ic, lc.toInt())

		lc = 0
		longRepeat(LONG_ITERATIONS) {
			++lc
		}
		assertEquals(LONG_ITERATIONS, lc)
	}
}
