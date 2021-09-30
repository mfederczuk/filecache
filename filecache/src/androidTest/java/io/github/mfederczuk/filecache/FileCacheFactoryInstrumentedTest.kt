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
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.time.Duration

@RunWith(AndroidJUnit4::class)
public class FileCacheFactoryInstrumentedTest : BaseInstrumentedTest() {

	@Test
	public fun onlyBaseDir() {
		with(FileCacheFactory(baseDir = File("/"))) {
			create("yeehaw", UserCacheAdapter)
				.assert(File("/yeehaw"))

			create("foo/bar", UserCacheAdapter)
				.assert(File("/foo/bar"))

			create("/a/b/c", UserCacheAdapter)
				.assert(File("/a/b/c"))
		}

		with(FileCacheFactory(baseDir = File("/abc/xyz"))) {
			create("baz", UserCacheAdapter)
				.assert(File("/abc/xyz/baz"))

			create("x/y/z", UserCacheAdapter)
				.assert(File("/abc/xyz/x/y/z"))

			create("/123/456", UserCacheAdapter)
				.assert(File("/123/456"))
		}

		with(FileCacheFactory(baseDir = instrumentationTargetContext.cacheDir)) {
			create("cache", UserCacheAdapter)
				.assert(instrumentationTargetContext.cacheDir.resolve("cache"))

			create("app/user", UserCacheAdapter)
				.assert(instrumentationTargetContext.cacheDir.resolve("app/user"))

			create("/not/the/cache/dir", UserCacheAdapter)
				.assert(File("/not/the/cache/dir"))
		}
	}

	@Test
	public fun filenamePrefix() {
		with(FileCacheFactory(baseDir = File("/"), filenamePrefix = "foo")) {
			create("bar", UserCacheAdapter)
				.assert(File("/foobar"))

			create("abc/foo", UserCacheAdapter)
				.assert(File("/abc/foo"))

			create("foobar", UserCacheAdapter)
				.assert(File("/foobar"))
		}

		with(FileCacheFactory(baseDir = instrumentationTargetContext.cacheDir, filenamePrefix = "cache_")) {
			create("_foobar", UserCacheAdapter)
				.assert(instrumentationTargetContext.cacheDir.resolve("cache_foobar"))

			create("cache_user", UserCacheAdapter)
				.assert(instrumentationTargetContext.cacheDir.resolve("cache_user"))

			create("yeehaw", UserCacheAdapter)
				.assert(instrumentationTargetContext.cacheDir.resolve("cache_yeehaw"))
		}
	}

	@Test
	public fun filenameSuffix() {
		with(FileCacheFactory(baseDir = File("/"), filenameSuffix = "bar")) {
			create("foo", UserCacheAdapter)
				.assert(File("/foobar"))

			create("foo/bar", UserCacheAdapter)
				.assert(File("/foo/bar"))

			create("foobar", UserCacheAdapter)
				.assert(File("/foobar"))
		}

		with(FileCacheFactory(baseDir = instrumentationTargetContext.cacheDir, filenameSuffix = ".cache.bin")) {
			create("foobar.cache", UserCacheAdapter)
				.assert(instrumentationTargetContext.cacheDir.resolve("foobar.cache.bin"))

			create("user.cache.bin", UserCacheAdapter)
				.assert(instrumentationTargetContext.cacheDir.resolve("user.cache.bin"))

			create("yeehaw", UserCacheAdapter)
				.assert(instrumentationTargetContext.cacheDir.resolve("yeehaw.cache.bin"))
		}
	}

	@Test
	public fun filenamePrefixAndFilenameSuffix() {
		with(
			FileCacheFactory(
				baseDir = File("/"),
				filenamePrefix = "foo",
				filenameSuffix = "baz"
			)
		) {
			create("bar", UserCacheAdapter)
				.assert(File("/foobarbaz"))

			create("xyz/foobaz", UserCacheAdapter)
				.assert(File("/xyz/foobaz"))

			create("foobarbaz", UserCacheAdapter)
				.assert(File("/foobarbaz"))
		}

		with(
			FileCacheFactory(
				baseDir = instrumentationTargetContext.cacheDir,
				filenamePrefix = "cache_",
				filenameSuffix = ".bin"
			)
		) {
			create("_foobar.", UserCacheAdapter)
				.assert(instrumentationTargetContext.cacheDir.resolve("cache_foobar.bin"))

			create("cache_user.bin", UserCacheAdapter)
				.assert(instrumentationTargetContext.cacheDir.resolve("cache_user.bin"))

			create("yeehaw", UserCacheAdapter)
				.assert(instrumentationTargetContext.cacheDir.resolve("cache_yeehaw.bin"))
		}
	}

	@Test
	public fun defaultMaxAge() {
		val duration20Seconds = Duration.ofSeconds(20)
		val duration1Hour = Duration.ofHours(1)

		with(FileCacheFactory(baseDir = File("/"))) {
			create("foo", UserCacheAdapter)
				.assert(Duration.ZERO)

			create("foo", UserCacheAdapter, duration20Seconds)
				.assert(duration20Seconds)

			create("foo", UserCacheAdapter, duration1Hour)
				.assert(duration1Hour)
		}

		with(FileCacheFactory(baseDir = File("/"), defaultMaxAge = duration20Seconds)) {
			create("foo", UserCacheAdapter)
				.assert(duration20Seconds)

			create("foo", UserCacheAdapter, duration20Seconds)
				.assert(duration20Seconds)

			create("foo", UserCacheAdapter, duration1Hour)
				.assert(duration1Hour)
		}

		with(FileCacheFactory(baseDir = File("/"), defaultMaxAge = duration1Hour)) {
			create("foo", UserCacheAdapter)
				.assert(duration1Hour)

			create("foo", UserCacheAdapter, duration20Seconds)
				.assert(duration20Seconds)

			create("foo", UserCacheAdapter, duration1Hour)
				.assert(duration1Hour)
		}
	}
}
