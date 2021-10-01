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

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides
import dagger.Reusable
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject
import javax.inject.Singleton

public class TestContainer @Inject constructor(
	public val userCache: FileCache<User>
)

@Singleton
@Component(
	modules = [TestComponent.CompanionModule::class]
)
public interface TestComponent {

	@Component.Factory
	public interface Factory {
		public fun create(@BindsInstance context: Context): TestComponent
	}

	@Module
	public object CompanionModule {

		@Provides
		@Reusable
		public fun provideFileCacheFactory(context: Context): FileCacheFactory {
			return FileCacheFactoryBuilder()
				.baseCacheDir(context, "dagger_test")
				.build()
		}

		@Provides
		@Singleton
		public fun provideUserCache(fileCacheFactory: FileCacheFactory): FileCache<User> {
			return fileCacheFactory.create(
				"user_dagger.cache.bin",
				UserCacheAdapter
			)
		}
	}

	public fun testContainer(): TestContainer
}

@RunWith(AndroidJUnit4::class)
public class DaggerInstrumentedTest : BaseInstrumentedTest() {

	private val testComponent: TestComponent by lazy {
		DaggerTestComponent.factory()
			.create(instrumentationTargetContext)
	}

	@Test
	public fun test() {
		val testContainer = testComponent.testContainer()

		val user = User.getRandomInstance()

		testContainer.userCache.setValue(user)

		assertEquals(
			user,
			testContainer.userCache.getValue()
		)
	}
}
