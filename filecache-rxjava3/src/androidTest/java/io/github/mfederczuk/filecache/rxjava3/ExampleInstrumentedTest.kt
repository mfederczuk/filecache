package io.github.mfederczuk.filecache.rxjava3

import androidx.test.ext.junit.runners.AndroidJUnit4
import io.github.mfederczuk.filecache.FileCache
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

	fun apiCall(): Single<Int> = TODO()

	@Test
	fun useAppContext(fileCache: FileCache<Int>) {
		apiCall()
			.updateCache(fileCache)


		fileCache.startWithValue()

		fileCache.retrieveValue()
			.mergeWith(Single.just(0))

		fileCache.setEventually(0)
			.observeOn(Schedulers.io())
			.subscribe()
	}
}
