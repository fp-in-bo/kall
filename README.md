Kall
=======================================

A Retrofit 2 (Experimental) `CallAdapter.Factory` bringing map and flatmap to calls.

Usage
-----
Add **JitPack** repository to your `build.gradle` file

``` gradle
allprojects {
	repositories {
	     ...
	     maven { url 'https://jitpack.io' }
	}
}
```

Add the Dependency 

``` gradle
dependencies {
    implementation "com.github.fp-in-bo:kall:0.0.1"
}
```

Add `KallAdapterFactory` as a `Call` adapter when building your `Retrofit` instance:
```kotlin
val retrofit = Retrofit.Builder()
    .baseUrl("https://example.com/")
    .addCallAdapterFactory(KallAdapterFactory())
    .build()
```

Your service methods can now use `Kall ` as their return type.
```kotlin

interface GitHubAPI {

    @GET("users/{username}")
    fun getUser(@Path("username") userName: String): Kall<User>
    
    @GET
    fun getFollowers(@Url url: String): Kall<List<User>>
}
```
Your can now map 

```kotlin
    api.getUser("dcampogiani").map { it.login.toUpperCase() }
```

And flatmap
```kotlin
    api.getUser("dcampogiani").flatMap {
            api.getFollowers(it.followersUrl)
        }
```


Thanks
-----

* Jake Wharton and Square for their [open](https://github.com/JakeWharton/retrofit2-kotlin-coroutines-adapter) [source](https://github.com/square/retrofit/tree/master/retrofit-adapters) adapters
* [azanin](https://github.com/azanin) and [al333z](https://github.com/al333z) for teaching me functional programming
