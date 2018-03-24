package br.com.rsicarelli.rxfirebaselogin.feature.login

import android.content.Intent
import br.com.rsicarelli.rxfirebaselogin.feature.login.jobs.DoGoogleLoginJob
import br.com.rsicarelli.rxfirebaselogin.feature.login.jobs.RequestGoogleLoginJob
import dagger.Module
import dagger.Provides
import io.fluent.rx.RxHub
import io.fluent.rx.RxJob
import io.fluent.rx.RxStore

@Module
class LoginModule {

  @Provides
  fun providesLoginActivity(activity: LoginActivity): LoginView = activity

  @Provides
  fun providesLoginStore() = RxStore(LoginState())

  @Provides
  fun providesDoGoogleLoginJob(doGoogleLoginJob: DoGoogleLoginJob): RxJob<Intent> {
    return doGoogleLoginJob
  }

  @Provides
  fun providesRequestGoogleLoginJob(requestGoogleLoginJob: RequestGoogleLoginJob): RxJob<Unit> {
    return requestGoogleLoginJob
  }

  @Provides
  fun providesLoginHub(loginHub: LoginHub): RxHub<LoginView> {
    return loginHub
  }
}
