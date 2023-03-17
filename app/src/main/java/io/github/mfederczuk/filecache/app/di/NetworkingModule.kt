package io.github.mfederczuk.filecache.app.di

import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import io.github.mfederczuk.filecache.app.networking.WiktionaryApiService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

@Module
object NetworkingModule {

	@Provides
	fun provideMoshi(): Moshi {
		return Moshi.Builder()
			.build()
	}

	@Provides
	fun provideOkHttpClient(): OkHttpClient {
		return OkHttpClient.Builder()
			.build()
	}

	@Provides
	fun provideRetrofit(okHttpClient: OkHttpClient, moshi: Moshi): Retrofit {
		return Retrofit.Builder()
			.baseUrl("https://en.wiktionary.org/api/rest_v1/")
			.client(okHttpClient)
			.addConverterFactory(MoshiConverterFactory.create(moshi))
			.addCallAdapterFactory(RxJava3CallAdapterFactory.create())
			.build()
	}

	@Provides
	fun provideWiktionaryApiService(retrofit: Retrofit): WiktionaryApiService {
		return retrofit.create(WiktionaryApiService::class.java)
	}
}
