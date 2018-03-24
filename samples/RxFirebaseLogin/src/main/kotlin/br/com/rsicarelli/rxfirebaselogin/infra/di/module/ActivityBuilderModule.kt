package br.com.rsicarelli.rxfirebaselogin.infra.di.module

import br.com.rsicarelli.rxfirebaselogin.feature.login.LoginActivity
import br.com.rsicarelli.rxfirebaselogin.feature.login.LoginModule
import br.com.rsicarelli.rxfirebaselogin.infra.di.ActivityScoped
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuilderModule {

  @ActivityScoped
  @ContributesAndroidInjector(modules = [(LoginModule::class)])
  internal abstract fun loginActivity(): LoginActivity
}
