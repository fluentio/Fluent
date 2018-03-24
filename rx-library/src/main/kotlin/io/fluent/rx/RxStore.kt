package io.fluent.rx

import io.fluent.State
import io.fluent.Store
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import kotlin.properties.Delegates

open class RxStore<T : State>(initialState: T) : Store<T>{
  private val publisher = BehaviorSubject.create<T>()

  private var currentState by Delegates.observable(initialState) { _, _, newState ->
    publisher.onNext(newState)
  }

  @Synchronized
  open override fun update(newState: T) {
    currentState = newState
  }

  open override fun state() = currentState

  @Synchronized
  open fun update(block: T.() -> T) {
    currentState = state().block()
  }

  fun stateChanges(): Observable<T> = publisher
}
