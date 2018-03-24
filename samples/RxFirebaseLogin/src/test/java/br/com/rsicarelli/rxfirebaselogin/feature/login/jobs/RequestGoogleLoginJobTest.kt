package br.com.rsicarelli.rxfirebaselogin.feature.login.jobs

import br.com.rsicarelli.rxfirebaselogin.feature.login.google.GoogleLogin
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Completable
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class RequestGoogleLoginJobTest {

  @Mock
  lateinit var googleLogin: GoogleLogin

  @InjectMocks
  lateinit var target: RequestGoogleLoginJob

  @Test
  fun startForResult_ShouldStartActivityForResult() {
    whenever(googleLogin.startActivityForResult()).thenReturn(Completable.complete())

    target.bind(Unit)
        .test()
        .assertComplete()
        .assertNoErrors()
  }
}