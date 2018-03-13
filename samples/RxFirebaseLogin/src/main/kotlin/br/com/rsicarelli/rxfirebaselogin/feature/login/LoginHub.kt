package br.com.rsicarelli.rxfirebaselogin.feature.login

import android.content.Intent
import br.com.rsicarelli.rxfirebaselogin.feature.login.google.GoogleLogin
import io.fluent.rx.RxHub
import io.fluent.rx.RxJob
import javax.inject.Inject

class LoginHub @Inject constructor(
    private val doGoogleLoginJob: RxJob<@JvmSuppressWildcards Intent>,
    private val requestGoogleLoginJob: RxJob<@JvmSuppressWildcards Unit>
) : RxHub<LoginView>() {

  override fun connect(view: LoginView) {
    view.doLoginClicks()
        .bind(requestGoogleLoginJob)

    view.activityResults()
        .filter { it.first == GoogleLogin.GOOGLE_REQUEST }
        .map { it.second }
        .bind(doGoogleLoginJob)
  }

}
