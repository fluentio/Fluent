package br.com.rsicarelli.rxfirebaselogin.feature.login.jobs

import br.com.rsicarelli.rxfirebaselogin.feature.login.google.GoogleLogin
import io.fluent.rx.RxJob
import io.reactivex.Completable
import javax.inject.Inject

class RequestGoogleLoginJob @Inject constructor(
    private val googleLogin: GoogleLogin
) : RxJob<Unit>() {

  override fun bind(input: Unit): Completable {
    return Completable.fromAction {
      googleLogin.startActivityForResult()
    }
  }

}
