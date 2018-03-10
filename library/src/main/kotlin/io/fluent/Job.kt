package io.fluent

interface Job<in T> {
  fun run(input: T)
}
