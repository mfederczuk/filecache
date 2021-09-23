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

import io.github.mfederczuk.filecache.adapter.CacheReader
import io.github.mfederczuk.filecache.adapter.CacheWriter
import org.junit.Assert.assertEquals
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

public abstract class BaseCacheReaderAndWriterUnitTest {

	protected interface TestHelper {
		public interface ActionHelper<T> {
			public fun write(writeAction: CacheWriter.(T) -> Unit)
			public fun read(readAction: CacheReader.() -> T)
		}

		public fun <T> action(actionValue: T, actionBlock: ActionHelper<T>.() -> Unit)
	}

	private data class Action(
		val write: CacheWriter.() -> Unit,
		val readAndAssert: CacheReader.() -> Unit
	)

	private class ActionHelperImpl<T>(val value: T) : TestHelper.ActionHelper<T> {

		private lateinit var write: CacheWriter.() -> Unit
		private lateinit var readAndAssert: CacheReader.() -> Unit

		override fun write(writeAction: CacheWriter.(T) -> Unit) {
			write = { writeAction(value) }
		}

		override fun read(readAction: CacheReader.() -> T) {
			readAndAssert = { assertEquals(value, readAction()) }
		}

		fun createAction(): Action {
			return Action(
				write,
				readAndAssert
			)
		}
	}

	private class TestHelperImpl : TestHelper {
		val actions: MutableList<Action> = ArrayList()

		override fun <T> action(actionValue: T, actionBlock: TestHelper.ActionHelper<T>.() -> Unit) {
			val actionHelper = ActionHelperImpl(actionValue)

			actionHelper.apply(actionBlock)

			actions.add(actionHelper.createAction())
		}
	}

	private val sharedReusableBuffer = ByteArray(Long.SIZE_BYTES)

	protected fun test(testHelperBlock: TestHelper.() -> Unit) {
		val testHelper = TestHelperImpl()

		testHelper.apply(testHelperBlock)

		run {
			val outputStream = ByteArrayOutputStream()
			val cacheWriter = CacheWriterImplWithSharedReusableBuffer(outputStream, sharedReusableBuffer)
			testHelper.actions
				.forEach { action ->
					cacheWriter.apply(action.write)
				}

			val cacheReader = CacheReaderImpl(ByteArrayInputStream(outputStream.toByteArray()))
			testHelper.actions
				.forEach { action ->
					cacheReader.apply(action.readAndAssert)
				}
		}

		testHelper.actions
			.forEach { action ->
				val oneTimeUseOutputStream = ByteArrayOutputStream()
				val oneTimeUseCacheWriter =
					CacheWriterImplWithSharedReusableBuffer(oneTimeUseOutputStream, sharedReusableBuffer)
				oneTimeUseCacheWriter.apply(action.write)

				val oneTimeUseCacheReader = CacheReaderImpl(ByteArrayInputStream(oneTimeUseOutputStream.toByteArray()))
				oneTimeUseCacheReader.apply(action.readAndAssert)
			}
	}
}
