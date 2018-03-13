package br.com.rsicarelli.rxfirebaselogin.feature.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import br.com.rsicarelli.rxfirebaselogin.R
import com.jakewharton.rxbinding2.view.RxView
import dagger.android.support.DaggerAppCompatActivity
import io.fluent.StateType
import io.fluent.rx.RxHub
import io.fluent.rx.RxStore
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_login.googleSignIn
import timber.log.Timber
import javax.inject.Inject


class LoginActivity : DaggerAppCompatActivity(), LoginView {
  @Inject
  lateinit var hub: RxHub<@JvmSuppressWildcards LoginView>

  @Inject
  lateinit var store: RxStore<LoginState>

  private val activityResultDispatcher = PublishSubject.create<Pair<Int, Intent>>()

  override fun doLoginClicks(): Observable<Unit> = RxView.clicks(googleSignIn).map { }

  override fun activityResults(): Observable<Pair<Int, Intent>> = activityResultDispatcher

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_login)

    store.stateChanges()
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe { bind(it) }

    Timber.d(store.toString())

    hub.connect(this)
  }

  override fun onDestroy() {
    super.onDestroy()
    hub.disconnect()
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    data?.let {
      activityResultDispatcher.onNext(requestCode to data)
    }
  }

  override fun bind(newState: LoginState) {
    when (newState.type) {
      StateType.Success -> success()
    }
  }

  private fun success() {
    Toast.makeText(this, "It works", Toast.LENGTH_LONG).show()
  }
}
