package io.fluent

interface Store<T : State> {
  fun update(newState: T)

  fun state(): T
}
