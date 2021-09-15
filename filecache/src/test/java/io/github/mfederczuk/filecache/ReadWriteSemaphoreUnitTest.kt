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
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

public class ReadWriteSemaphoreUnitTest {

	@Test
	public fun singleThread() {
		val readWriteSemaphore = ReadWriteSemaphore()

		readWriteSemaphore.acquireRead()
		readWriteSemaphore.acquireRead()
		assertTrue(readWriteSemaphore.tryAcquireRead())
		assertTrue(readWriteSemaphore.tryAcquireRead())

		assertFalse(readWriteSemaphore.tryAcquireWrite())

		readWriteSemaphore.releaseRead()
		readWriteSemaphore.releaseRead()
		readWriteSemaphore.releaseRead()
		readWriteSemaphore.releaseRead()


		readWriteSemaphore.acquireWrite()

		assertFalse(readWriteSemaphore.tryAcquireRead())
		assertFalse(readWriteSemaphore.tryAcquireWrite())

		readWriteSemaphore.releaseWrite()
	}

	@Test
	public fun multiThreadsReadOnly() {
		val readWriteSemaphore = ReadWriteSemaphore()
		val i = 64

		val threads = launchThreads(
			{
				readWriteSemaphore.read {
					assertEquals(64, i)
				}
			},
			{
				readWriteSemaphore.read {
					assertEquals(64, i)
				}
			},
			{
				readWriteSemaphore.read {
					assertEquals(64, i)
				}
			}
		)

		threads.joinAll()

		assertEquals(64, i)
	}

	@Test
	public fun multiThreadsReadWrite() {
		val readWriteSemaphore = ReadWriteSemaphore()
		var i = 64

		val writeThread1 = launchThread {
			readWriteSemaphore.write {
				assertEquals(64, i)
				i = 128
			}
		}

		launchThread {
			readWriteSemaphore.read {
				assertTrue(i == 64 || i == 128 || i == 256 || i == 512)
			}
		}

		val writeThread2 = launchThread {
			writeThread1.join()
			readWriteSemaphore.write {
				assertEquals(128, i)
				i = 256
			}
		}

		launchThreads(
			{
				readWriteSemaphore.read {
					assertTrue(i == 64 || i == 128 || i == 256 || i == 512)
				}
			},
			{
				writeThread1.join()
				readWriteSemaphore.read {
					assertTrue(i == 128 || i == 256 || i == 512)
				}
			},
			{
				writeThread1.join()
				writeThread2.join()
				readWriteSemaphore.read {
					assertTrue(i == 256 || i == 512)
				}
			}
		)

		val writeThread3 = launchThread {
			writeThread1.join()
			writeThread2.join()
			readWriteSemaphore.write {
				assertEquals(256, i)
				i = 512
			}
		}

		launchThread {
			readWriteSemaphore.read {
				assertTrue(i == 64 || i == 128 || i == 256 || i == 512)
			}
		}

		val writeThreads = listOf(writeThread1, writeThread2, writeThread3)

		launchThread {
			writeThreads.joinAll()
			readWriteSemaphore.read {
				assertTrue(i == 512)
			}
		}

		writeThreads.joinAll()

		assertEquals(512, i)
	}


	private inline fun launchThread(crossinline action: () -> Unit): Thread {
		val thread = actionToThread(action)
		thread.start()
		return thread
	}

	@Suppress("NOTHING_TO_INLINE")
	private inline fun launchThreads(vararg actions: () -> Unit): List<Thread> {
		return actions
			.map(::actionToThread)
			.onEach(Thread::start)
	}

	private inline fun actionToThread(crossinline action: () -> Unit): Thread {
		return Thread {
			Thread.sleep(10)
			action()
		}
	}
}

private fun Iterable<Thread>.joinAll() {
	this.forEach(Thread::join)
}
