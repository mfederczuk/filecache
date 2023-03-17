<!-- markdownlint-disable first-line-heading -->

*Note: This is mostly a trainings/test project, there are **way** better options than this library which should be used instead for real apps.*

&nbsp;

# FileCache #

[version_shield]: https://img.shields.io/badge/version-N%2FA_(in_development)-important.svg
![version: N/A (in development)][version_shield]

<!--
[version_shield]: https://img.shields.io/badge/version-{{CURRENT_VERSION_NAME}}-informational.svg
[release_page]: https://github.com/mfederczuk/{{GITHUB_REPO_NAME}}/releases/tag/v{{CURRENT_VERSION_NAME}} "Release v{{CURRENT_VERSION_NAME}}"
[![version: {{CURRENT_VERSION_NAME}}][version_shield]][release_page]
[![Changelog](https://img.shields.io/badge/-Changelog-informational.svg)](CHANGELOG.md "Changelog")

TODO: JitPack badges (build status + javadoc link)
-->

## About ##

> Android library to cache arbitrary data on the filesystem

<!-- TODO: description -->

## Usage ##

<!-- TODO: usage -->

### Using Moshi JSON Adapters ###

The `filecache-moshi` module adds support for **Moshi**'s JSON adapters.

<!-- TODO -->

### Dagger Setup ###

The classes [`FileCacheFactory`](filecache/src/main/java/io/github/mfederczuk/filecache/FileCacheFactory.kt) and
[`FileCacheFactoryBuilder`](filecache/src/main/java/io/github/mfederczuk/filecache/FileCacheFactory.kt#L42) are designed
with a Dagger setup in mind.

```kotlin
@Singleton
@Component(
    modules = [CacheModule::class]
)
interface CacheComponent {
}

@Module)
object CacheModule {

    @Provide
    @Reusable
    fun provideFileCacheFactory(context: Context): FileCacheFactory {
        return FileCacheFactoryBuilder()
            // ...
            .build()
    }

    @Provides
    @Singleton
    fun provideFooCache(fileCacheFactory: FileCacheFactory): FileCache<Foo> {
        return fileCacheFactory.create("foo", FooCacheAdapter)
    }
}
```

<!-- TODO -->

### Note about Parcels ###

As noted in the [`Parcel`] API documentation:

> Parcel is **not** a general-purpose serialization mechanism. This class [...] is designed as a high-performance IPC
> transport.

As such, `Parcel`s cannot be written into caches and [`Parcelable`s][Parcelable] cannot be used as or converted to
[`CacheAdapter`].

[`Parcel`]: https://developer.android.com/reference/android/os/Parcel "Parcel &nbsp;|&nbsp; Android Developers"
[Parcelable]: https://developer.android.com/reference/android/os/Parcelable "Parcelable &nbsp;|&nbsp; Android Developers"
[`CacheAdapter`]: filecache/src/main/java/io/github/mfederczuk/filecache/adapter/CacheAdapter.kt

## Installation ##

Installation done using [**JitPack**]

```gradle
repositories {
    maven { url 'https://jitpack.io' }
}
```

```gradle
dependencies {
    implementation 'com.github.mfederczuk:filecache:v1.0.0'
    implementation 'com.github.mfederczuk.filecache:filecache:v1.0.0'
    implementation 'com.github.mfederczuk.filecache:filecache-moshi:v1.0.0'
    implementation 'com.github.mfederczuk.filecache:filecache-rxjava3:v1.0.0'
}
```

[**JitPack**]: https://jitpack.io

## Contributing ##

Read through the [Contribution Guidelines](CONTRIBUTING.md) if you want to contribute to this project.

## License ##

**FileCache** is licensed under the [**Apache License v2.0**](licenses/Apache-v2.0.txt).  
For more information about copying and licensing, see the [COPYING.txt](COPYING.txt) file.
