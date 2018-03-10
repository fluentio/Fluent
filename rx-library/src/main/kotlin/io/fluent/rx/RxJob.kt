package io.fluent.rx

import io.fluent.Job
import io.reactivex.Completable

abstract class RxJob<in T> : Job<T> {
  override fun run(input: T) {
    bind(input).blockingGet()
  }

  abstract fun bind(input: T): Completable
}
