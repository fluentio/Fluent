
# Fluent 

Fluent is a lightweight framework thats helps you to create an entirely Android Application following SOLID pattern with an unidirectional flow reactive or not.

The main concepts in this architecture are : `State`, `View`, `Store`, `Job` and `Hub` 

---
>![fluent diagram](https://image.ibb.co/cAXPhH/Screen_Shot_2018_04_11_at_12_09_41.png)
---

## Concept Explanation

### State

We recommend to define one `State` for each one of your screens.

It holds just `type() : StateType` and can be inherit to fit your needs.

### StateType

We already have some default kind of states, you can create your own type inheriting this class.
The default states are: `Initial`, `Loading`, `Success` and `Error`

### View

That's the reference to your screen itself, you should create your kind of `View` that has specific `State` attached.

Your `Activity` or `Fragment` needs to implement `View`

```kotlin
class LoginActivity : AppCompatActivity(), LoginView {
    override fun bind(newState: LoginState) { ... }
}
```

### Store

It receives and manages all the possible states of a `View` so you don't need to worry, because we take care about statefulness and performance.


### Job

A job is a little piece of work which might look like an atomic operation. It's the only place where you can generate new states and push them to the `Store`.

### Hub

The `Hub` is maybe the most important and disruptive layer of this framework. It is responsible to `connect()` the `View` and the `Job`'s layers.

You can combine and/or filter actions before perform the `Job` itself. It acts kind like a bridge binding user actions with the use cases. 

We have already implemented the `disconnect()` method to release resources by disposing all the bound jobs.

---


>For now ahed we can have two kind of approaches with this framework: A non reactive and a total reactive way.


## Non reactive way

### View

### Store

### Job

### Hub


## Reactive way

We work with Reactive Extension to follow the Reactive Manifesto

### View - Reactive Way

Stay aware to think in a way to declare all the user's actions and screen's actions related as `Observable`.

```kotlin
interface LoginView : View<LoginState> {
  fun doLoginClicks(): Observable<Unit>
  fun activityResults(): Observable<Pair<Int, Intent>>
}
```

### RxStore
> `RxStore` is an implementation of `Store` that works with Reactive Extensions

To create one you just need to pass an `State` class as parameter and define the related initial state:

```kotlin
RxStore(LoginState(StateType.Initial))
```

The stream created by it can be handled inside the `View` implementation.

```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_login)

    store.stateChanges()
         .observeOn(AndroidSchedulers.mainThread())
         .subscribe { bind(it) }
}

override fun bind(newState: LoginState) {
   when (newState.type) {
     StateType.Success -> success()
     StateType.Error -> error()
     StateType.Loading -> loading()
   }
}
```


### RxJob
> `RxJob` is an implementation of `Job` that works with Reactive Extensions

Just need to inherit your use case class from `RxJob` (specifying the kind of return of this job) and override the `bind(input: T):Completable` method with the properly work.  As it is returns a `Completable` you should handle the possible returns on the `RxStore` subscription, with `doOnSubscribe`, `doOnSuccess` and `doOnError` as shown in the next example:

```kotlin
class DoGoogleLoginJob @Inject constructor(
    private val store: RxStore<LoginState>,
    private val firebase: GoogleFirebaseAuth) : RxJob<Intent>() {

  override fun bind(input: Intent): Completable {
    return firebase.firebaseAuthWith(intent = input)
        .doOnSubscribe { store.update { setType(StateType.Loading) } }
        .doOnSuccess { store.update { setType(StateType.Success) } }
        .doOnError { store.update { setType(StateType.Error) } }
        .toCompletable()
        .onErrorComplete()
  }
}
```

> In this example we are injecting the local parameters `store` and `firebase` with [dagger](https://github.com/google/dagger) we strong recommend that.

### RxHub
> `RxHub` is an implementation of `Hub` that works with Reactive Extensions

You connect the `View` and the `RxJob`'s layers through the operator `bind(job: RxJob<T>)`(that's why all of your `View`'s methods needs to return an `Observable`).

After that just need to inherit the class `RxHub<T>`(specifying the `View` it will connect with) and override the `connect(view: T)` method with the binds to `RxJobs` you need.

```kotlin
class LoginHub @Inject constructor(
    private val doGoogleLoginJob: RxJob<Intent>,
    private val requestGoogleLoginJob: RxJob<Unit>) : RxHub<LoginView>() {

  override fun connect(view: LoginView) {
    view.doLoginClicks()
        .bind(requestGoogleLoginJob)

    view.activityResults()
        .filter { it.first == GoogleLogin.GOOGLE_REQUEST }
        .map { it.second }
        .bind(doGoogleLoginJob)
  }
}
```

And finally that's how you connect or disconnect your `View` to the `Hub`

```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_login)

    store.stateChanges()
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe { bind(it) }

    hub.connect(this)
}

override fun onDestroy() {
    super.onDestroy()
    hub.disconnect()
}
```

---

If you found potencial in this project please help us to improve the framework and help the open source community to have access to an awesome and well developed project!

---

## License

```
Copyright 2018 fluentio Team

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
