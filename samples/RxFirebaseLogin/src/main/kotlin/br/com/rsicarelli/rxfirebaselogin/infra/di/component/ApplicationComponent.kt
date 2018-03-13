package br.com.rsicarelli.rxfirebaselogin.infra.di.component

import android.app.Application
import br.com.rsicarelli.rxfirebaselogin.App
import br.com.rsicarelli.rxfirebaselogin.infra.di.module.ActivityBuilderModule
import br.com.rsicarelli.rxfirebaselogin.infra.di.module.ApplicationModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [
  (AndroidSupportInjectionModule::class),
  (ApplicationModule::class),
  (ActivityBuilderModule::class)
])
interface ApplicationComponent : AndroidInjector<App> {

  @Component.Builder
  interface Builder {
    @BindsInstance
    fun application(application: Application): Builder

    fun build(): ApplicationComponent
  }
}
