package io.fluent

interface Hub<in V> {
  fun connect(view: V)

  fun disconnect()
}
