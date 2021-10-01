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
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertThrows
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.time.Duration

@RunWith(AndroidJUnit4::class)
public class FileCacheFactoryBuilderInstrumentedTest : BaseInstrumentedTest() {

	private sealed class Field(
		val action: FileCacheFactoryBuilder.() -> FileCacheFactoryBuilder
	)

	private data class FileBaseDir(val baseDir: File) : Field({ baseDir(baseDir) })
	private data class StringBaseDir(val baseDir: String) : Field({ baseDir(baseDir) })

	private data class BaseCacheDir(val context: Context) : Field({ baseCacheDir(context) })

	private data class FileBaseCacheDir(
		val context: Context,
		val baseDir: File
	) : Field({ baseCacheDir(context, baseDir) })

	private data class StringBaseCacheDir(
		val context: Context,
		val baseDir: String
	) : Field({ baseCacheDir(context, baseDir) })

	private data class BaseFilesDir(val context: Context) : Field({ baseFilesDir(context) })

	private data class FileBaseFilesDir(
		val context: Context,
		val baseDir: File
	) : Field({ baseFilesDir(context, baseDir) })

	private data class StringBaseFilesDir(
		val context: Context,
		val baseDir: String
	) : Field({ baseFilesDir(context, baseDir) })

	private data class FilenamePrefix(val filenamePrefix: String) : Field({ filenamePrefix(filenamePrefix) })
	private data class FilenameSuffix(val filenameSuffix: String) : Field({ filenameSuffix(filenameSuffix) })
	private data class DefaultMaxAge(val defaultMaxAge: Duration) : Field({ defaultMaxAge(defaultMaxAge) })


	@Test
	public fun noBaseDir() {
		val filenamePrefixes = arrayOf(null, "foo", "cache_")
		val filenameSuffixes = arrayOf(null, "bar", ".cache.bin")
		val defaultMaxAges = arrayOf(null, Duration.ofSeconds(20), Duration.ofHours(1))

		filenamePrefixes.forEach { filenamePrefix ->
			filenameSuffixes.forEach { filenameSuffix ->
				defaultMaxAges.forEach { defaultMaxAge ->
					FileCacheFactoryBuilder()
						.run {
							if(filenamePrefix != null) {
								this@run.filenamePrefix(filenamePrefix)
							} else {
								this@run
							}
						}
						.run {
							if(filenameSuffix != null) {
								this@run.filenameSuffix(filenameSuffix)
							} else {
								this@run
							}
						}
						.run {
							if(defaultMaxAge != null) {
								this@run.defaultMaxAge(defaultMaxAge)
							} else {
								this@run
							}
						}
						.run {
							assertThrows(IllegalStateException::class.java, this@run::build)
						}


					val builder = FileCacheFactoryBuilder()

					if(filenamePrefix != null) builder.filenamePrefix(filenamePrefix)
					if(filenameSuffix != null) builder.filenameSuffix(filenameSuffix)
					if(defaultMaxAge != null) builder.defaultMaxAge(defaultMaxAge)

					assertThrows(IllegalStateException::class.java, builder::build)
				}
			}
		}
	}

	@Test
	public fun onlyBaseDir() {
		val assertBlockA = createAssertBlock {
			create("yeehaw", UserCacheAdapter)
				.assert(File("/yeehaw"))

			create("foo/bar", UserCacheAdapter)
				.assert(File("/foo/bar"))

			create("/a/b/c", UserCacheAdapter)
				.assert(File("/a/b/c"))
		}

		test(FileBaseDir(File("/")), assertBlock = assertBlockA)
		test(StringBaseDir("/"), assertBlock = assertBlockA)


		val assertBlockB = createAssertBlock {
			create("baz", UserCacheAdapter)
				.assert(File("/abc/xyz/baz"))

			create("x/y/z", UserCacheAdapter)
				.assert(File("/abc/xyz/x/y/z"))

			create("/123/456", UserCacheAdapter)
				.assert(File("/123/456"))
		}

		test(FileBaseDir(File("/abc/xyz")), assertBlock = assertBlockB)
		test(StringBaseDir("/abc/xyz"), assertBlock = assertBlockB)


		val assertBlockC = createAssertBlock {
			create("cache", UserCacheAdapter)
				.assert(instrumentationTargetContext.cacheDir.resolve("cache"))

			create("app/user", UserCacheAdapter)
				.assert(instrumentationTargetContext.cacheDir.resolve("app/user"))

			create("/not/the/cache/dir", UserCacheAdapter)
				.assert(File("/not/the/cache/dir"))
		}

		test(FileBaseDir(instrumentationTargetContext.cacheDir), assertBlock = assertBlockC)
		test(StringBaseDir(instrumentationTargetContext.cacheDir.path), assertBlock = assertBlockC)


		test(
			StringBaseDir("/foo"),
			StringBaseDir("/bar")
		) {
			create("baz", UserCacheAdapter)
				.assert(File("/bar/baz"))
		}

		test(
			StringBaseDir("/foo/bar"),
			StringBaseDir("/baz/123"),
			FileBaseDir(File("/abc/xyz"))
		) {
			create("yeehaw", UserCacheAdapter)
				.assert(File("/abc/xyz/yeehaw"))
		}
	}

	@Test
	public fun onlyBaseCacheDir() {
		test(BaseCacheDir(instrumentationTargetContext)) {
			create("foo", UserCacheAdapter)
				.assert(instrumentationTargetContext.cacheDir.resolve("foo"))

			create("bar/baz", UserCacheAdapter)
				.assert(instrumentationTargetContext.cacheDir.resolve("bar/baz"))

			create("/a/bc/d", UserCacheAdapter)
				.assert(File("/a/bc/d"))
		}

		val assertBlockB = createAssertBlock {
			create("abc", UserCacheAdapter)
				.assert(instrumentationTargetContext.cacheDir.resolve("foo/bar/abc"))

			create("x/y/z", UserCacheAdapter)
				.assert(instrumentationTargetContext.cacheDir.resolve("foo/bar/x/y/z"))

			create("/yee/haw", UserCacheAdapter)
				.assert(File("/yee/haw"))
		}

		test(FileBaseCacheDir(instrumentationTargetContext, File("foo/bar")), assertBlock = assertBlockB)
		test(StringBaseCacheDir(instrumentationTargetContext, "foo/bar"), assertBlock = assertBlockB)

		val assertBlockC = createAssertBlock {
			create("xyz", UserCacheAdapter)
				.assert(File("/a/b/c/xyz"))

			create("d/e/f", UserCacheAdapter)
				.assert(File("/a/b/c/d/e/f"))

			create("/foo/bar", UserCacheAdapter)
				.assert(File("/foo/bar"))
		}

		test(FileBaseCacheDir(instrumentationTargetContext, File("/a/b/c")), assertBlock = assertBlockC)
		test(StringBaseCacheDir(instrumentationTargetContext, "/a/b/c"), assertBlock = assertBlockC)
	}

	@Test
	public fun onlyBaseFilesDir() {
		test(BaseFilesDir(instrumentationTargetContext)) {
			create("foo", UserCacheAdapter)
				.assert(instrumentationTargetContext.filesDir.resolve("foo"))

			create("bar/baz", UserCacheAdapter)
				.assert(instrumentationTargetContext.filesDir.resolve("bar/baz"))

			create("/a/bc/d", UserCacheAdapter)
				.assert(File("/a/bc/d"))
		}

		val assertBlockB = createAssertBlock {
			create("abc", UserCacheAdapter)
				.assert(instrumentationTargetContext.filesDir.resolve("foo/bar/abc"))

			create("x/y/z", UserCacheAdapter)
				.assert(instrumentationTargetContext.filesDir.resolve("foo/bar/x/y/z"))

			create("/yee/haw", UserCacheAdapter)
				.assert(File("/yee/haw"))
		}

		test(FileBaseFilesDir(instrumentationTargetContext, File("foo/bar")), assertBlock = assertBlockB)
		test(StringBaseFilesDir(instrumentationTargetContext, "foo/bar"), assertBlock = assertBlockB)

		val assertBlockC = createAssertBlock {
			create("xyz", UserCacheAdapter)
				.assert(File("/a/b/c/xyz"))

			create("d/e/f", UserCacheAdapter)
				.assert(File("/a/b/c/d/e/f"))

			create("/foo/bar", UserCacheAdapter)
				.assert(File("/foo/bar"))
		}

		test(FileBaseFilesDir(instrumentationTargetContext, File("/a/b/c")), assertBlock = assertBlockC)
		test(StringBaseFilesDir(instrumentationTargetContext, "/a/b/c"), assertBlock = assertBlockC)
	}

	@Test
	public fun filenamePrefix() {
		val assertBlockA = createAssertBlock {
			create("baz", UserCacheAdapter)
				.assert(File("/bar/foobaz"))

			create("foobar", UserCacheAdapter)
				.assert(File("/bar/foobar"))

			create("foo", UserCacheAdapter)
				.assert(File("/bar/foo"))
		}

		test(
			StringBaseDir("/bar"),
			FilenamePrefix("foo"),
			assertBlock = assertBlockA
		)

		test(
			FilenamePrefix("foo"),
			StringBaseDir("/bar"),
			assertBlock = assertBlockA
		)


		val assertBlockB = createAssertBlock {
			create("user", UserCacheAdapter)
				.assert(File("/foo/bar/cache_user"))

			create("cache_user", UserCacheAdapter)
				.assert(File("/foo/bar/cache_user"))
		}

		test(
			StringBaseDir("/foo/bar"),
			FilenamePrefix("cache_"),
			assertBlock = assertBlockB
		)

		test(
			FilenamePrefix("cache_"),
			StringBaseDir("/foo/bar"),
			assertBlock = assertBlockB
		)


		test(
			StringBaseDir("/foo"),
			FilenamePrefix("x"),
			FilenamePrefix("cache_")
		) {
			create("file", UserCacheAdapter)
				.assert(File("/foo/cache_file"))
		}
	}

	@Test
	public fun filenameSuffix() {
		val assertBlockA = createAssertBlock {
			create("baz", UserCacheAdapter)
				.assert(File("/foo/bazbar"))

			create("foobar", UserCacheAdapter)
				.assert(File("/foo/foobar"))

			create("bar", UserCacheAdapter)
				.assert(File("/foo/bar"))
		}

		test(
			StringBaseDir("/foo"),
			FilenameSuffix("bar"),
			assertBlock = assertBlockA
		)

		test(
			FilenameSuffix("bar"),
			StringBaseDir("/foo"),
			assertBlock = assertBlockA
		)


		val assertBlockB = createAssertBlock {
			create("user", UserCacheAdapter)
				.assert(File("/foo/bar/user.cache.bin"))

			create("user.cache.bin", UserCacheAdapter)
				.assert(File("/foo/bar/user.cache.bin"))
		}

		test(
			StringBaseDir("/foo/bar"),
			FilenameSuffix(".cache.bin"),
			assertBlock = assertBlockB
		)

		test(
			FilenameSuffix(".cache.bin"),
			StringBaseDir("/foo/bar"),
			assertBlock = assertBlockB
		)


		test(
			StringBaseDir("/foo"),
			FilenameSuffix(".cache"),
			FilenameSuffix(".bin")
		) {
			create("file", UserCacheAdapter)
				.assert(File("/foo/file.bin"))
		}
	}

	@Test
	public fun filenamePrefixAndFilenameSuffix() {
		val assertBlockA = createAssertBlock {
			create("bar", UserCacheAdapter)
				.assert(File("/abc/foobarbaz"))

			create("foobarbaz", UserCacheAdapter)
				.assert(File("/abc/foobarbaz"))

			create("foobaz", UserCacheAdapter)
				.assert(File("/abc/foobaz"))
		}

		test(
			StringBaseDir("/abc"),
			FilenamePrefix("foo"),
			FilenameSuffix("baz"),
			assertBlock = assertBlockA
		)

		test(
			FilenameSuffix("baz"),
			FilenamePrefix("foo"),
			StringBaseDir("/abc"),
			assertBlock = assertBlockA
		)


		val assertBlockB = createAssertBlock {
			create("user", UserCacheAdapter)
				.assert(File("/foo/bar/cache_user.bin"))

			create("cache_user.bin", UserCacheAdapter)
				.assert(File("/foo/bar/cache_user.bin"))
		}

		test(
			StringBaseDir("/foo/bar"),
			FilenamePrefix("cache_"),
			FilenameSuffix(".bin"),
			assertBlock = assertBlockB
		)

		test(
			FilenameSuffix(".bin"),
			FilenamePrefix("cache_"),
			StringBaseDir("/foo/bar"),
			assertBlock = assertBlockB
		)

		test(
			StringBaseDir("/foo"),
			FilenameSuffix(".cache"),
			FilenamePrefix("x"),
			FilenameSuffix(".bin"),
			FilenamePrefix("cache_")
		) {
			create("file", UserCacheAdapter)
				.assert(File("/foo/cache_file.bin"))
		}
	}

	@Test
	public fun defaultMaxAge() {
		val duration20Seconds = Duration.ofSeconds(20)
		val duration1Hour = Duration.ofHours(1)


		val assertBlockA = createAssertBlock {
			create("bar", UserCacheAdapter)
				.assert(duration20Seconds)

			create("bar", UserCacheAdapter, duration20Seconds)
				.assert(duration20Seconds)

			create("bar", UserCacheAdapter, duration1Hour)
				.assert(duration1Hour)
		}

		test(
			StringBaseDir("/foo"),
			DefaultMaxAge(duration20Seconds),
			assertBlock = assertBlockA
		)

		test(
			DefaultMaxAge(duration20Seconds),
			StringBaseDir("/foo"),
			assertBlock = assertBlockA
		)


		test(
			StringBaseDir("/foo"),
			DefaultMaxAge(duration1Hour)
		) {
			create("bar", UserCacheAdapter)
				.assert(duration1Hour)

			create("bar", UserCacheAdapter, duration20Seconds)
				.assert(duration20Seconds)

			create("bar", UserCacheAdapter, duration1Hour)
				.assert(duration1Hour)
		}


		test(
			StringBaseDir("/foo"),
			DefaultMaxAge(duration20Seconds),
			DefaultMaxAge(duration1Hour)
		) {
			create("bar", UserCacheAdapter)
				.assert(duration1Hour)
		}
	}

	@Test
	public fun mixed() {
		val duration20Seconds = Duration.ofSeconds(20)
		val duration1Hour = Duration.ofHours(1)


		val assertBlockA = createAssertBlock {
			create("foo", UserCacheAdapter)
				.assert(instrumentationTargetContext.cacheDir.resolve("bar/baz/app_foo_cache.bin"), duration20Seconds)

			create("abc-xyz", UserCacheAdapter, duration1Hour)
				.assert(instrumentationTargetContext.cacheDir.resolve("bar/baz/app_abc-xyz_cache.bin"), duration1Hour)
		}

		test(
			FilenameSuffix("_cache.bin"),
			DefaultMaxAge(duration20Seconds),
			FilenamePrefix("app_"),
			FileBaseCacheDir(instrumentationTargetContext, File("bar/baz")),
			assertBlock = assertBlockA
		)

		test(
			FilenamePrefix("x"),
			DefaultMaxAge(duration1Hour),
			StringBaseFilesDir(instrumentationTargetContext, "yee/haw/pardner"),
			DefaultMaxAge(duration20Seconds),
			FileBaseCacheDir(instrumentationTargetContext, File("bar/baz")),
			FilenameSuffix("_cache.bin"),
			FilenamePrefix("app_"),
			assertBlock = assertBlockA
		)


		val assertBlockB = createAssertBlock {
			create("foobar", UserCacheAdapter, duration20Seconds)
				.assert(File("/x/y/abc/foobar+foobar+foobar"), duration20Seconds)
		}

		test(
			StringBaseDir("/x/y/abc"),
			FilenamePrefix("foobar+"),
			FilenameSuffix("+foobar"),
			DefaultMaxAge(duration1Hour),
			assertBlock = assertBlockB
		)

		test(
			StringBaseDir("/x/y/abc"),
			FilenamePrefix("foobar+"),
			FilenameSuffix("+foobar"),
			assertBlock = assertBlockB
		)
	}

	@Test
	public fun newBuilder() {
		val factory = FileCacheFactoryBuilder()
			.baseDir("/foo/bar")
			.filenamePrefix("abc-")
			.build()
			.newBuilder()
			.filenameSuffix("-xyz")
			.baseCacheDir(instrumentationTargetContext, "baz")
			.build()

		factory.create("user.cache.bin", UserCacheAdapter)
			.assert(instrumentationTargetContext.cacheDir.resolve("baz/abc-user.cache.bin-xyz"))
	}


	private fun createAssertBlock(assertBlock: FileCacheFactory.() -> Unit): FileCacheFactory.() -> Unit {
		return assertBlock
	}

	private fun test(vararg fields: Field, assertBlock: FileCacheFactory.() -> Unit) {
		FileCacheFactoryBuilder()
			.run {
				fields.fold(this@run) { builder, field ->
					builder.run(field.action)
				}
			}
			.build()
			.apply(assertBlock)


		val builder = FileCacheFactoryBuilder()

		fields.forEach { field ->
			builder.run(field.action)
		}

		builder.build().apply(assertBlock)
	}
}
