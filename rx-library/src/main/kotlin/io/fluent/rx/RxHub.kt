package io.fluent.rx

import io.fluent.Hub
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class RxHub<in V> : Hub<V> {
  private val compositeDisposable = CompositeDisposable()

  override fun disconnect() {
    compositeDisposable.clear()
  }

  fun Disposable.compose() = compositeDisposable.add(this)

  fun <T> Observable<T>.subscribeAndCompose() = subscribe().compose()

  fun Completable.subscribeAndCompose() = subscribe().compose()

  fun <T> Single<T>.subscribeAndCompose() = subscribe().compose()

  fun <T> Maybe<T>.subscribeAndCompose() = subscribe().compose()

  fun <T> Flowable<T>.subscribeAndCompose() = subscribe().compose()

  fun <T> Observable<T>.bind(job: RxJob<T>) =
      this.flatMapCompletable { job.bind(it) }
          .subscribeAndCompose()

  fun <T> Single<T>.bind(job: RxJob<T>) =
      this.flatMapCompletable { job.bind(it) }
          .subscribeAndCompose()

  fun <T> Maybe<T>.bind(job: RxJob<T>) =
      this.flatMapCompletable { job.bind(it) }
          .subscribeAndCompose()

  fun <T> Flowable<T>.bind(job: RxJob<T>) =
      this.flatMapCompletable { job.bind(it) }
          .subscribeAndCompose()

  fun <T> Observable<T>.bind(vararg jobs: RxJob<T>) =
      this.flatMapCompletable { value ->
        jobs.map { it.bind(value) }
            .fold(Completable.complete()) { acc, next -> acc.andThen(next) }
      }.subscribeAndCompose()

  fun <T> Single<T>.bind(vararg jobs: RxJob<T>) =
      this.flatMapCompletable { value ->
        jobs.map { it.bind(value) }
            .fold(Completable.complete()) { acc, next -> acc.andThen(next) }
      }.subscribeAndCompose()

  fun <T> Maybe<T>.bind(vararg jobs: RxJob<T>) =
      this.flatMapCompletable { value ->
        jobs.map { it.bind(value) }
            .fold(Completable.complete()) { acc, next -> acc.andThen(next) }
      }.subscribeAndCompose()

  fun <T> Flowable<T>.bind(vararg jobs: RxJob<T>) =
      this.flatMapCompletable { value ->
        jobs.map { it.bind(value) }
            .fold(Completable.complete()) { acc, next -> acc.andThen(next) }
      }.subscribeAndCompose()
}