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

package io.github.mfederczuk.filecache.moshi

import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import org.junit.Test

@JsonClass(generateAdapter = true)
public data class Foo(
	val dummy: String
)

public class MoshiAdaptersUnitTest {

	private val moshi = Moshi.Builder()
		.build()

	@Test
	public fun test() {
		val fooJsonAdapter = moshi.adapter(Foo::class.java)
		moshi.cacheAdapter<>()
		val fooCacheAdapter = fooJsonAdapter.asCacheAdapter()
		fooJsonAdapter
	}
}
