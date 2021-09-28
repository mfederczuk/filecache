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

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import java.io.File
import java.time.Duration
import java.time.Instant
import java.time.temporal.ChronoUnit

public abstract class BaseInstrumentedTest {

	protected val instrumentationTargetContext: Context
		get() {
			return InstrumentationRegistry.getInstrumentation().targetContext
		}


	protected fun getUserFileCacheFile(testName: String): File {
		return instrumentationTargetContext.cacheDir
			.resolve(this::class.java.name)
			.resolve("user_$testName.cache.bin")
	}

	protected fun getNewUserFileCache(testName: String): FileCache<User> {
		val file = getUserFileCacheFile(testName)

		// file might still exist from older test runs
		file.delete()

		return FileCache(
			file,
			UserCacheAdapter
		)
	}


	protected fun FileCache<*>.assert(expectedFile: File) {
		assertEquals(expectedFile, this.file)
	}

	protected fun FileCache<*>.assert(expectedMaxAge: Duration) {
		assertEquals(expectedMaxAge, this.maxAge)
	}

	protected fun FileCache<*>.assert(expectedFile: File, expectedMaxAge: Duration) {
		this@assert.assert(expectedFile)
		this@assert.assert(expectedMaxAge)
	}

	protected fun FileCache<User>.assertPresent(user: User) {
		assertTrue(this@assertPresent.isPresent())
		assertFalse(this@assertPresent.isNotPresent())

		val nullableSnapshot = this@assertPresent.getSnapshotOrNull()
		assertEquals(user, nullableSnapshot!!.value)
		nullableSnapshot.modificationTime
		assertEquals(user, this@assertPresent.getValueOrNull())
		assertNotNull(this@assertPresent.getModificationTimeOrNull())

		val snapshot = this@assertPresent.getSnapshot()
		assertEquals(user, snapshot.value)
		snapshot.modificationTime
		assertEquals(user, this@assertPresent.getValue())
		@Suppress("CheckResult") this@assertPresent.getModificationTime()

		val invalidUser = User.getRandomInstance()
		assertEquals(user, this@assertPresent.getValueOrElse { throw AssertionError() })
		assertEquals(user, this@assertPresent.getValueOrDefault(invalidUser))

		assertCallbackCalled { confirm ->
			this@assertPresent.ifPresent { snapshot ->
				confirm()
				assertEquals(user, snapshot.value)
				snapshot.modificationTime
			}
		}
		assertCallbackNotCalled(this@assertPresent::ifNotPresent)
		assertCallbackCalled { confirm ->
			this@assertPresent.ifValuePresent { value ->
				confirm()
				assertEquals(user, value)
			}
		}
	}

	protected fun FileCache<User>.assertPresent(user: User, modificationTime: Instant) {
		assertTrue(this@assertPresent.isPresent())
		assertFalse(this@assertPresent.isNotPresent())

		val modificationTimeTruncatedToSeconds = modificationTime.truncatedTo(ChronoUnit.SECONDS)

		val nullableSnapshot = this@assertPresent.getSnapshotOrNull()
		assertEquals(user, nullableSnapshot!!.value)
		assertEquals(modificationTimeTruncatedToSeconds, nullableSnapshot.modificationTime)
		assertEquals(user, this@assertPresent.getValueOrNull())
		assertEquals(modificationTimeTruncatedToSeconds, this@assertPresent.getModificationTimeOrNull())

		val snapshot = this@assertPresent.getSnapshot()
		assertEquals(user, snapshot.value)
		assertEquals(modificationTimeTruncatedToSeconds, snapshot.modificationTime)
		assertEquals(user, this@assertPresent.getValue())
		assertEquals(modificationTimeTruncatedToSeconds, this@assertPresent.getModificationTime())

		val invalidUser = User.getRandomInstance()
		assertEquals(user, this@assertPresent.getValueOrElse { throw AssertionError() })
		assertEquals(user, this@assertPresent.getValueOrDefault(invalidUser))

		assertCallbackCalled { confirm ->
			this@assertPresent.ifPresent { snapshot ->
				confirm()
				assertEquals(user, snapshot.value)
				assertEquals(modificationTimeTruncatedToSeconds, snapshot.modificationTime)
			}
		}
		assertCallbackNotCalled(this@assertPresent::ifNotPresent)
		assertCallbackCalled { confirm ->
			this@assertPresent.ifValuePresent { value ->
				confirm()
				assertEquals(user, value)
			}
		}
	}

	protected fun FileCache<User>.assertNotPresent() {
		assertFalse(this@assertNotPresent.isPresent())
		assertTrue(this@assertNotPresent.isNotPresent())

		assertNull(this@assertNotPresent.getSnapshotOrNull())
		assertNull(this@assertNotPresent.getValueOrNull())
		assertNull(this@assertNotPresent.getModificationTimeOrNull())

		assertThrows(IllegalStateException::class.java, this@assertNotPresent::getSnapshot)
		assertThrows(IllegalStateException::class.java, this@assertNotPresent::getValue)
		assertThrows(IllegalStateException::class.java, this@assertNotPresent::getModificationTime)

		val defaultUser = User.getRandomInstance()
		assertEquals(defaultUser, this@assertNotPresent.getValueOrElse { defaultUser })
		assertEquals(defaultUser, this@assertNotPresent.getValueOrDefault(defaultUser))

		assertCallbackNotCalled { fail ->
			this@assertNotPresent.ifPresent { fail() }
		}
		assertCallbackCalled(this@assertNotPresent::ifNotPresent)
		assertCallbackNotCalled { fail ->
			this@assertNotPresent.ifValuePresent { fail() }
		}
	}


	protected fun assertCallbackCalled(call: (confirm: () -> Unit) -> Unit) {
		var confirmed = false

		call {
			confirmed = true
		}

		assertTrue(confirmed)
	}

	protected fun assertCallbackNotCalled(call: (fail: () -> Nothing) -> Unit) {
		call {
			throw AssertionError()
		}
	}
}
