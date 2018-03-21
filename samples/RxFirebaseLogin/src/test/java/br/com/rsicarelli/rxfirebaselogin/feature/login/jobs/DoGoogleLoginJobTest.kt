package br.com.rsicarelli.rxfirebaselogin.feature.login.jobs

import android.content.Intent
import br.com.rsicarelli.rxfirebaselogin.feature.login.LoginState
import br.com.rsicarelli.rxfirebaselogin.feature.login.google.GoogleFirebaseAuth
import br.com.rsicarelli.rxfirebaselogin.feature.login.google.User
import com.nhaarman.mockito_kotlin.whenever
import io.fluent.StateType
import io.fluent.rx.RxStore
import io.reactivex.Single
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Spy
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class DoGoogleLoginJobTest {

  @Spy
  var store: RxStore<LoginState> = RxStore(LoginState())

  @Mock
  lateinit var intent: Intent

  @Mock
  lateinit var firebase: GoogleFirebaseAuth

  @InjectMocks
  lateinit var target: DoGoogleLoginJob

  @Test
  fun success_WithUser_ShouldDoLogin() {
    withUser {
      target.bind(intent)
          .test()
          .assertComplete()
          .assertNoErrors()

      store.stateChanges()
          .test()
          .assertValue { it.type == StateType.Success }
    }
  }

  @Test
  fun error_WithoutUser_ShouldNotDoLogin() {
    withError {
      target.bind(intent)
          .test()
          .assertComplete()
          .assertNoErrors()

      store.stateChanges()
          .test()
          .assertValue { it.type == StateType.Error }
    }
  }

  private fun withUser(action: () -> Unit) {
    whenever(firebase.firebaseAuthWith(intent)).thenReturn(Single.just(User("a@a.com")))
    action()
  }

  private fun withError(action: () -> Unit) {
    whenever(firebase.firebaseAuthWith(intent)).thenReturn(Single.error(Exception()))
    action()
  }
}
