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

import androidx.annotation.CheckResult
import java.util.concurrent.Semaphore

/**
 * Semaphore-like class that allows any number of read acquisitions but only one write acquisition that lock each other.
 */
internal class ReadWriteSemaphore {

	/**
	 * The actual semaphore.
	 * If it is acquired, either a read or write operation is active.
	 */
	private val operationSemaphore: Semaphore = Semaphore(1, true)

	/** How many read acquisitions are active. */
	private var readCount: UInt = 0u

	/*
	 * when {
	 *     operationSemaphore released                   -> no operations active
	 *     operationSemaphore acquired && readCount == 0 -> write operation active
	 *     operationSemaphore acquired && readCount > 0  -> one or more read operations active
	 * }
	 */

	// region read

	@Synchronized
	fun acquireRead() {
		if(readCount == 0u) {
			operationSemaphore.acquire()
		}

		++readCount
	}

	@Synchronized
	fun releaseRead() {
		if(readCount == 0u) return

		--readCount

		if(readCount == 0u) {
			operationSemaphore.release()
		}
	}

	@Synchronized
	@CheckResult
	fun tryAcquireRead(): Boolean {
		if(readCount > 0u || operationSemaphore.tryAcquire()) {
			++readCount
			return true
		}

		return false
	}

	inline fun <T> read(crossinline action: () -> T): T {
		acquireRead()
		try {
			return action()
		} finally {
			releaseRead()
		}
	}

	inline fun <T : Any> tryRead(crossinline action: () -> T): T? {
		if(!tryAcquireRead()) {
			return null
		}

		try {
			return action()
		} finally {
			releaseRead()
		}
	}

	// endregion

	// region write

	fun acquireWrite() {
		operationSemaphore.acquire()
	}

	fun releaseWrite() {
		operationSemaphore.release()
	}

	@CheckResult
	fun tryAcquireWrite(): Boolean {
		return operationSemaphore.tryAcquire()
	}

	inline fun <T> write(crossinline action: () -> T): T {
		acquireWrite()
		try {
			return action()
		} finally {
			releaseWrite()
		}
	}

	inline fun <T : Any> tryWrite(crossinline action: () -> T): T? {
		if(!tryAcquireWrite()) {
			return null
		}

		try {
			return action()
		} finally {
			releaseWrite()
		}
	}

	// endregion
}
