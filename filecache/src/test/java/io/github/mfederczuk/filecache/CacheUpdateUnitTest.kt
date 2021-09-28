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
import java.time.Duration
import java.time.Instant

public class CacheUpdateUnitTest {

	@Test
	public fun fromAge() {
		val epochSeconds = 990050400L
		val now = Instant.ofEpochSecond(epochSeconds)

		val duration1Second = Duration.ofSeconds(1)
		val duration1Minute = Duration.ofMinutes(1)

		val cacheUpdateAge1Second = CacheUpdate.fromAge(value = 0, age = duration1Second, now)
		val cacheUpdateAge1Minute = CacheUpdate.fromAge(value = 0, age = duration1Minute, now)

		assertEquals(Instant.ofEpochSecond(epochSeconds - 1), cacheUpdateAge1Second.modificationTime)
		assertEquals(Instant.ofEpochSecond(epochSeconds - 60), cacheUpdateAge1Minute.modificationTime)
	}
}
