package br.com.rsicarelli.rxfirebaselogin.feature.login.google

import android.content.Intent
import br.com.rsicarelli.rxfirebaselogin.feature.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import io.reactivex.Single
import timber.log.Timber
import javax.inject.Inject

open class GoogleFirebaseAuth @Inject constructor(
    private val activity: LoginActivity,
    private val googleLogin: GoogleLogin
) {

  open fun firebaseAuthWith(intent: Intent): Single<User> {
    return Single.create<User>({ emitter ->
      val accountFromIntent = googleLogin.getAccountFromIntent(intent)
      val auth = FirebaseAuth.getInstance()
      val credential = GoogleAuthProvider.getCredential(accountFromIntent.idToken, null)

      auth.signInWithCredential(credential).addOnCompleteListener(activity, { task ->
        if (task.isSuccessful) {
          auth.currentUser?.let {
            emitter.onSuccess(User(it.email))
          }
        } else {
          task.exception?.let {
            Timber.e(it)
            emitter.onError(it)
          }
        }
      })
    })
  }

}

data class User(
    val email: String?
)
