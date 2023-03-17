package io.github.mfederczuk.filecache.app.networking

import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Path

interface WiktionaryApiService {

	@GET("page/definition/{term}")
	fun fetchDefinition(@Path("term") term: String): Single<Nothing> // TODO
}
