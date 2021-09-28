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
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
public class FileCacheDelegateInstrumentedTest : BaseInstrumentedTest() {

	private companion object {
		var i: Int = 0
	}

	public inner class TestContainer(shred: Boolean? = null) {

		public val fileCache: FileCache<User> = getNewUserFileCache("user_delegate_${i++}.cache.bin")

		public var userCache: User? by if(shred == null) {
			cached(fileCache)
		} else {
			cached(fileCache, shred)
		}
	}

	@Test
	public fun test() {
		arrayOf(
			TestContainer(),
			TestContainer(shred = false),
			TestContainer(shred = true)
		).forEach { container ->
			assertNull(container.userCache)
			container.fileCache.assertNotPresent()

			val user = User.getRandomInstance()
			container.userCache = user
			assertEquals(user, container.userCache)
			container.fileCache.assertPresent(user)

			container.userCache = null
			assertNull(container.userCache)
			container.fileCache.assertNotPresent()
		}
	}
}
