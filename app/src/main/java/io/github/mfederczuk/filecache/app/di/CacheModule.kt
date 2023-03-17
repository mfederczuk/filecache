package io.github.mfederczuk.filecache.app.di

import android.content.Context
import dagger.Module
import dagger.Provides
import io.github.mfederczuk.filecache.FileCache
import io.github.mfederczuk.filecache.FileCacheFactory
import io.github.mfederczuk.filecache.FileCacheFactoryBuilder

@Module
object CacheModule {

	@Provides
	fun provideCacheFactory(context: Context): FileCacheFactory {
		return FileCacheFactoryBuilder()
			.baseCacheDir(context)
			.filenameSuffix(".cache.bin")
			.build()
	}

	@Provides
	fun provide_Cache(cacheFactory: FileCacheFactory): FileCache<Nothing> {
		return cacheFactory.create<Nothing>(TODO(), TODO())
	}
}
