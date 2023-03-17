package io.github.mfederczuk.filecache.app

import android.app.Application
import io.github.mfederczuk.filecache.app.di.AppComponent
import io.github.mfederczuk.filecache.app.di.DaggerAppComponent

class DemoApp : Application() {

	private lateinit var appComponent: AppComponent

	override fun onCreate() {
		super.onCreate()

		appComponent = DaggerAppComponent.factory().create(this)
	}
}
