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

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.time.Duration
import java.time.Instant
import java.time.temporal.ChronoUnit

@RunWith(AndroidJUnit4::class)
public class FileCacheInstrumentedTest : BaseInstrumentedTest() {

	@Test
	public fun constructor() {
		val file = getUserFileCacheFile(::constructor.name)
		val adapter = UserCacheAdapter

		val cacheA = FileCache(
			file,
			adapter
		)

		assertEquals(file, cacheA.file)
		assertEquals(adapter, cacheA.adapter)

		val maxAge = Duration.ofDays(1)

		val cacheB = FileCache(
			file,
			UserCacheAdapter,
			maxAge
		)

		assertEquals(file, cacheB.file)
		assertEquals(adapter, cacheB.adapter)
		assertEquals(maxAge, cacheB.maxAge)
	}

	@Test
	public fun factoryFunctions() {
		val cacheA = FileCache.createInCacheDir(
			instrumentationTargetContext,
			"user_factoryCacheDir_a.cache.bin",
			UserCacheAdapter
		)
		assertTrue(cacheA.file.startsWith(instrumentationTargetContext.cacheDir))

		val cacheB = FileCache.createInCacheDir(
			instrumentationTargetContext,
			"user_factorCacheDir_b.cache.bin",
			UserCacheAdapter,
			maxAge = Duration.ofDays(2)
		)
		assertTrue(cacheB.file.startsWith(instrumentationTargetContext.cacheDir))

		val cacheC = FileCache.createInFilesDir(
			instrumentationTargetContext,
			"user_factoryFilesDir_a.cache.bin",
			UserCacheAdapter
		)
		assertTrue(cacheC.file.startsWith(instrumentationTargetContext.filesDir))

		val cacheD = FileCache.createInFilesDir(
			instrumentationTargetContext,
			"user_factoryFilesDir_b.cache.bin",
			UserCacheAdapter,
			maxAge = Duration.ofDays(4)
		)
		assertTrue(cacheD.file.startsWith(instrumentationTargetContext.filesDir))
	}

	@Test
	public fun freshGet() {
		val userCache = getNewUserFileCache(::freshGet.name)
		userCache.assertNotPresent()
	}

	@Test
	public fun setAndGet() {
		val userCache = getNewUserFileCache(::setAndGet.name)

		val userA = User.getRandomInstance()
		userCache.setValue(userA)
		userCache.assertPresent(userA)

		val userB = User.getRandomInstance()
		val instant20SecondsAgo = instant20SecondsAgo()
		userCache.setValue(userB, `when` = instant20SecondsAgo)
		userCache.assertPresent(userB, instant20SecondsAgo)

		val userC = User.getRandomInstance()
		userCache.setValue(userC, `when` = null)
		userCache.assertPresent(userC, instant20SecondsAgo)

		val userD = User.getRandomInstance()
		userCache.setValue(userD)
		userCache.assertPresent(userD)
	}

	@Test
	public fun getAndSet() {
		val userCache = getNewUserFileCache(::getAndSet.name)

		val userA = User.getRandomInstance()
		assertNull(userCache.getAndSetValue(userA))
		userCache.assertPresent(userA)

		val userB = User.getRandomInstance()
		assertEquals(userA, userCache.getAndSetValue(userB))
		userCache.assertPresent(userB)

		val userC = User.getRandomInstance()
		val instant20SecondsAgo = instant20SecondsAgo()
		assertEquals(userB, userCache.getAndSetValue(userC, `when` = instant20SecondsAgo))
		userCache.assertPresent(userC, instant20SecondsAgo)

		val userD = User.getRandomInstance()
		assertEquals(userC, userCache.getAndSetValue(userD, `when` = null))
		userCache.assertPresent(userD, instant20SecondsAgo)

		val userE = User.getRandomInstance()
		assertEquals(userD, userCache.getAndSetValue(userE))
		userCache.assertPresent(userE)

		userCache.delete()

		val userF = User.getRandomInstance()
		assertNull(userCache.getAndSetValue(userF))
		userCache.assertPresent(userF)
	}

	@Test
	public fun delete() {
		val userCache = getNewUserFileCache(::delete.name)

		val userA = User.getRandomInstance()
		userCache.setValue(userA)
		userCache.assertPresent(userA)

		userCache.delete()
		userCache.assertNotPresent()

		assertNull(userCache.getAndDelete())
		userCache.assertNotPresent()

		val userB = User.getRandomInstance()
		userCache.setValue(userB)
		userCache.assertPresent(userB)

		assertEquals(userB, userCache.getAndDelete())
		userCache.assertNotPresent()
	}

	@Test
	public fun shred() {
		val userCache = getNewUserFileCache(::shred.name)

		val user = User.getRandomInstance()
		userCache.setValue(user)
		userCache.assertPresent(user)

		userCache.shred()
		userCache.assertNotPresent()
	}

	@Test
	public fun update() {
		val userCache = getNewUserFileCache(::update.name)

		userCache.update { snapshot ->
			assertNull(snapshot)
			null
		}
		userCache.assertNotPresent()

		val userA = User.getRandomInstance()
		userCache.setValue(userA)

		val userB = User.getRandomInstance()
		userCache.update { snapshot ->
			assertEquals(userA, snapshot!!.value)
			CacheUpdate(userB)
		}
		userCache.assertPresent(userB)

		userCache.delete()

		val userC = User.getRandomInstance()
		val instant20SecondsAgo = instant20SecondsAgo()
		userCache.update { snapshot ->
			assertNull(snapshot)

			CacheUpdate(
				value = userC,
				modificationTime = instant20SecondsAgo
			)
		}
		userCache.assertPresent(userC, instant20SecondsAgo)

		userCache.update { snapshot ->
			assertEquals(userC, snapshot!!.value)
			null
		}
		userCache.assertNotPresent()


		userCache.update {
			assertThrows(IllegalStateException::class.java, userCache::isPresent)
			null
		}

		assertThrows(IllegalStateException::class.java) {
			userCache.update {
				@Suppress("CheckResult") userCache.isPresent()

				throw AssertionError()
			}
		}
	}

	@Test
	public fun updateValue() {
		val userCache = getNewUserFileCache(::updateValue.name)

		userCache.updateValue { value ->
			assertNull(value)
			null
		}
		userCache.assertNotPresent()

		val userA = User.getRandomInstance()
		userCache.setValue(userA)

		val userB = User.getRandomInstance()
		userCache.updateValue { value ->
			assertEquals(userA, value)
			userB
		}
		userCache.assertPresent(userB)

		userCache.delete()

		val userC = User.getRandomInstance()
		val instant20SecondsAgo = instant20SecondsAgo()
		userCache.updateValue(`when` = instant20SecondsAgo) { value ->
			assertNull(value)
			userC
		}
		userCache.assertPresent(userC, instant20SecondsAgo)

		val userD = User.getRandomInstance()
		userCache.updateValue(`when` = null) { value ->
			assertEquals(userC, value)
			userD
		}
		userCache.assertPresent(userD, instant20SecondsAgo)

		userCache.updateValue { value ->
			assertEquals(userD, value)
			null
		}
		userCache.assertNotPresent()
	}

	@Test
	public fun updateExistingValue() {
		val userCache = getNewUserFileCache(::updateExistingValue.name)

		assertCallbackNotCalled { fail ->
			userCache.updateExistingValue { fail() }
		}

		val userA = User.getRandomInstance()
		userCache.setValue(userA)

		val userB = User.getRandomInstance()
		userCache.updateExistingValue { value ->
			assertEquals(userA, value)
			userB
		}
		userCache.assertPresent(userB)

		userCache.updateExistingValue { value ->
			assertEquals(userB, value)
			null
		}
		userCache.assertNotPresent()

		assertCallbackNotCalled { fail ->
			userCache.updateExistingValue { fail() }
		}

		val userC = User.getRandomInstance()
		userCache.setValue(userC)

		val userD = User.getRandomInstance()
		val instant20SecondsAgo = instant20SecondsAgo()
		userCache.updateExistingValue(`when` = instant20SecondsAgo) { value ->
			assertEquals(userC, value)
			userD
		}
		userCache.assertPresent(userD, instant20SecondsAgo)

		val userE = User.getRandomInstance()
		userCache.updateExistingValue(`when` = null) { value ->
			assertEquals(userD, value)
			userE
		}
		userCache.assertPresent(userE, instant20SecondsAgo)

		userCache.delete()

		assertCallbackNotCalled { fail ->
			userCache.updateExistingValue { fail() }
		}
	}

	@Test
	public fun touch() {
		val userCache = getNewUserFileCache(::touch.name)

		val user = User.getRandomInstance()
		userCache.setValue(user)

		val instant20SecondsAgo = instant20SecondsAgo()
		userCache.touch(`when` = instant20SecondsAgo)

		assertEquals(user, userCache.getValue())
		assertEquals(instant20SecondsAgo.truncatedTo(ChronoUnit.SECONDS), userCache.getModificationTime())
	}


	private fun instant20SecondsAgo(now: Instant = Instant.now()): Instant {
		return now.minusSeconds(20)
	}
}
