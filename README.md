
# Fluent 

Fluent is a lightweight framework that helps you to create an entirely Android Application following SOLID pattern with a unidirectional flow using reactive concepts or not.

The main concepts in this architecture are: `State`, `View`, `Store`, `Job` and `Hub` 

---
>![fluent diagram](https://image.ibb.co/cAXPhH/Screen_Shot_2018_04_11_at_12_09_41.png)
---

## Concept Explanation

### View

The `View` class is the entry point of your Fluent flow. Basically, you need to expose all the events (or streams) that the `Hub` class needs to connect with some `Job`.

You need to create your own specialized `View `class for each view of your application.

A regular implementation of your `View`:

```kotlin
interface LoginView : View<LoginState> {
  fun doLoginClicks(): UserCredentials
}
```

Note that the `View` class is the reference to your screen itself and you should attach your screen `State` as well.

Your `Activity`, `Fragment` or `Custom View` needs to implement your `View` class:

```kotlin
class LoginActivity : AppCompatActivity(), LoginView {
    override fun bind(newState: LoginState) { ... }
}
```

> For convenience, the Fluent framework already provides the `bind()` function. It is just a helper function to reduce the boilerplate.

>See how to implement [with rx](#view---reactive-way) and [without rx](#view---non-reactive-way)

### State

The `State` represents some state of your screen. In order to reduce inconsistency from your `View`, the `State` is an object who represents the entire state which you view should react.

Note that your view's state should be a `data class`, which means that your `View` should not have more than one single `State`.

It is a good practice that each of your screens has your own `State` class.
Fluent already provides the `type()` function so you can easily retrieve the type of your view's state.

By default, every `State` has a `StateType` which represents the type of your view state.

#### StateType

The `StateType` is class that you can create the different states of your view.
The Fluent framework already provides four default StateType, which is: `Initial`, `Loading`, `Success` and `Error`.

You can easily create your own StateType inheriting the StateType class:

```kotlin
class LoginStateType : StateType() {
  object Refreshing : StateType()
  class Error(val message: String) : StateType()
}
```

### Store

Responsible for handle all the new States. It is the only way to update the View state.
It receives all the possible states of a `View` so you don't need to worry, because we take care about statefulness and performance.

>See how to implement [with rx](#rxstore) and [without rx](#store---non-reactive-way)

### Job

A job is a little piece of work which might look like an atomic operation. It can be network calls, database operations, third-party libraries or even a view-lifecycle handling.

It's the only place where you can generate new states and push them to the `Store`.

>See how to implement [with rx](#rxjob) and [without rx](#job---non-reactive-way)

### Hub

The `Hub` is maybe the most important and disruptive layer of this framework. 
For each new `View` events, your `Hub` should connect with some `Job`. 

It is responsible to `connect()` the `View` and the `Job`'s layers.

You can combine and/or filter actions before performing the `Job` itself. It acts kind like a bridge binding user actions with the use cases. 

>See how to implement [with rx](#rxhub) and [without rx](#hub---non-reactive-way)


## Implementing in a non Reactive Way

### View - Non Reactive Way
`//TODO`
Too be filled

### Store - Non Reactive Way
`//TODO`
Too be filled

### Job - Non Reactive Way
`//TODO`
Too be filled

### Hub - Non Reactive Way
`//TODO`
Too be filled

## Implementing in a Reactive way

Besides Fluent can be used with callbacks, Fluent can be more charming with Reactive Extensions, following the Reactive Manifesto

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

It requires an initial `State`. You can use the `stateChanges()` to start receiving new states:

```kotlin
RxStore(SomeState()).stateChanges()
    .observeOn(mainThread)
    .subscribe { newState -> bind(newState) }
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


The RxJob have the `bind()` function, which returns a `Completable` class. Since Fluent is a unidirectional flow, your `Job` should complete or not.

You just need to inherit your use case class from `RxJob` (specifying the type of the input parameter) and override the `bind(input: T):Completable` method with the properly work.

>You must handle all the possible edge cases into your job. You should not allow your job to throw an exception back to the `Hub`.

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

The `RxHub` contains functions to easily connects your `View` events source to your `RxJob`'s.

You connect the `View` and the `RxJob`'s layers through the operator `bind(job: RxJob<T>)`(that's why all of your `View`'s methods needs to return an `Observable`).

After that just need to inherit the class `RxHub<T>`(specifying the `View` it will connect with) and override the `connect(view: T)` method with the binds to `RxJobs` you need.

We have already implemented the `disconnect()` method to release resources by disposing all the bound jobs.

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

And finally that's how you connect and disconnect your `View` to the `Hub`

```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_login)

    ...
    hub.connect(this)
}

override fun onDestroy() {
    super.onDestroy()
    hub.disconnect()
}
```

---

We are looking forward to improving this framework. If you have some feedback, comments, questions or if you want to contribute, join us at our [Slack group](https://join.slack.com/t/fluent-io/shared_invite/enQtMzQ3MDY3Njk0MjI3LTZkMjU0Y2Q3ZTA2NWQ0OTE4ZjFiZmQxNzJlMTYxNjIwODBjYmJhOGRlYzRhNGY5NmNjZTVhMTIzMTJhNzEwYTY)


## Next Steps:
* Library icon
* Non-reactive samples
* More reactive samples
* Improve documentation with callbacks (non-reactive)
* Fluent for iOS (Swift)

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
