package io.github.mfederczuk.filecache.app.di

import android.content.Context
import dagger.BindsInstance
import dagger.Component

@Component(
	modules = [
		NetworkingModule::class,
		CacheModule::class,
	]
)
interface AppComponent {

	@Component.Factory
	interface Factory {
		fun create(@BindsInstance applicationContext: Context): AppComponent
	}
}
