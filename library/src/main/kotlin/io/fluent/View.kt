package io.fluent

interface View<in T : State> {
  fun bind(newState: T)
}
