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

public class StringPrefixSuffixUnitTest {

	@Test
	public fun prefix() {
		assertEquals("abcdefghi", "abcdefghi".withPrefix("abcdef"))
		assertEquals("abcdefghi", "bcdefghi".withPrefix("abcdef"))
		assertEquals("abcdefghi", "cdefghi".withPrefix("abcdef"))
		assertEquals("abcdefghi", "defghi".withPrefix("abcdef"))
		assertEquals("abcdefghi", "efghi".withPrefix("abcdef"))
		assertEquals("abcdefghi", "fghi".withPrefix("abcdef"))
		assertEquals("abcdefghi", "ghi".withPrefix("abcdef"))

		assertEquals("foo_bar_baz", "baz".withPrefix("foo_bar_"))
		assertEquals("foo_bar_baz", "_baz".withPrefix("foo_bar_"))
		assertEquals("foo_bar_baz", "bar_baz".withPrefix("foo_bar_"))
		assertEquals("foo_bar_baz", "_bar_baz".withPrefix("foo_bar_"))
		assertEquals("foo_bar_baz", "foo_bar_baz".withPrefix("foo_bar_"))

		assertEquals("cache_user", "user".withPrefix("cache_"))

		assertEquals("foo_bar", "".withPrefix("foo_bar"))
		assertEquals("foo_bar", "foo_bar".withPrefix(""))
	}

	@Test
	public fun suffix() {
		assertEquals("abcdefghi", "abcdefghi".withSuffix("defghi"))
		assertEquals("abcdefghi", "abcdefgh".withSuffix("defghi"))
		assertEquals("abcdefghi", "abcdefg".withSuffix("defghi"))
		assertEquals("abcdefghi", "abcdef".withSuffix("defghi"))
		assertEquals("abcdefghi", "abcde".withSuffix("defghi"))
		assertEquals("abcdefghi", "abcd".withSuffix("defghi"))
		assertEquals("abcdefghi", "abc".withSuffix("defghi"))

		assertEquals("foo_bar_baz", "foo".withSuffix("_bar_baz"))
		assertEquals("foo_bar_baz", "foo_".withSuffix("_bar_baz"))
		assertEquals("foo_bar_baz", "foo_bar".withSuffix("_bar_baz"))
		assertEquals("foo_bar_baz", "foo_bar_".withSuffix("_bar_baz"))
		assertEquals("foo_bar_baz", "foo_bar_baz".withSuffix("_bar_baz"))

		assertEquals("user.cache.bin", "user".withSuffix(".cache.bin"))
		assertEquals("user.cache.bin", "user.cache".withSuffix(".cache.bin"))

		assertEquals("foo_bar", "".withSuffix("foo_bar"))
		assertEquals("foo_bar", "foo_bar".withSuffix(""))
	}

	@Test
	public fun both() {
		assertEquals("foo_bar_baz", "bar".withPrefixAndSuffix("foo_", "_baz"))
		assertEquals("foobar+foobar+foobar", "foobar".withPrefixAndSuffix("foobar+", "+foobar"))
	}
}
