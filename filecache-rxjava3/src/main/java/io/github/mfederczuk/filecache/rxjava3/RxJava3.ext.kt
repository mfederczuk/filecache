package io.github.mfederczuk.filecache.rxjava3

import io.github.mfederczuk.filecache.FileCache
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.MaybeSource
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import java.time.Instant

public fun <T : Any> FileCache<T>.maybeRetrieveValue(): Maybe<T> {
	return Maybe.create { emitter ->
		val value = this.getValueOrNull()

		if(value != null) {
			emitter.onSuccess(value)
		} else {
			emitter.onComplete()
		}
	}
}

public fun <T : Any> FileCache<T>.retrieveValue(): Single<T> {
	return Single.fromCallable(this::getValue)
}

public fun <T : Any> FileCache<T>.startWithValue(): Observable<T> {
	return Observable.create { emitter ->
		this.ifValuePresent(emitter::onNext)
	}
}


public fun <T : Any> FileCache<T>.setEventually(value: T, `when`: Instant = Instant.now()): Completable {
	return Completable.fromAction {
		this.setValue(value, `when`)
	}
}

public fun <T : Any> FileCache<T>.updateValueEventually(`when`: Instant? = Instant.now(), update: (T?) -> T?): Completable {
	return Completable.fromAction {
		this.updateValue(`when`, update)
	}
}


public fun <T : Any> Flowable<T>.updateCache(fileCache: FileCache<T>): Flowable<T> {
	return this.doOnNext(fileCache::setValue)
}

public fun <T : Any> Observable<T>.updateCache(fileCache: FileCache<T>): Observable<T> {
	return this.doOnNext(fileCache::setValue)
}

public fun <T : Any> Single<T>.updateCache(fileCache: FileCache<T>): Single<T> {
	return this.doOnSuccess(fileCache::setValue)
}

public fun <T : Any> Maybe<T>.updateCache(fileCache: FileCache<T>): Maybe<T> {
	return this.doOnSuccess(fileCache::setValue)
}

public fun <T : Any> Maybe<T>.updateOrDeleteCache(fileCache: FileCache<T>/*, shredOnDelete: Boolean = false*/): Maybe<T> {
	return this
		.doOnSuccess(fileCache::setValue)
		.doOnComplete(fileCache::delete)
//		.doOnComplete(if(!shredOnDelete) fileCache::delete else fileCache::shred)
}


public fun <T : Any> Observable<T>.startWithCacheValue(fileCache: FileCache<T>): Observable<T> {
	return this.startWith(fileCache.maybeRetrieveValue())
}


//fun <T:Any> Single<T>.setupCache(fileCache: FileCache<T>): Observable<T> {
//	return  this
//		.doOnSuccess(fileCache::setValue)
//}

public fun <T : Any> Observable<T>.setupCache(fileCache: FileCache<T>): Observable<T> {
	return this
		.doOnNext(fileCache::setValue)
		.startWith(MaybeSource { emitter ->
			val value = fileCache.getValueOrNull()

			if(value != null) {
				emitter.onSuccess(value)
			} else {
				emitter.onComplete()
			}
		})
}
