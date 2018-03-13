package br.com.rsicarelli.rxfirebaselogin.feature.login

import android.content.Intent
import io.fluent.View
import io.reactivex.Observable

interface LoginView : View<LoginState> {
  fun doLoginClicks(): Observable<Unit>
  fun activityResults(): Observable<Pair<Int, Intent>>
}
