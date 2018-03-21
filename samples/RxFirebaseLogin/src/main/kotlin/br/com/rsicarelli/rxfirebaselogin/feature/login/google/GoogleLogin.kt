package br.com.rsicarelli.rxfirebaselogin.feature.login.google

import android.content.Intent
import br.com.rsicarelli.rxfirebaselogin.R
import br.com.rsicarelli.rxfirebaselogin.feature.login.LoginActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import javax.inject.Inject

open class GoogleLogin @Inject constructor(
    private val activity: LoginActivity
) {

  private val gso by lazy {
    GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(activity.getString(R.string.default_web_client_id))
        .requestEmail()
        .build()
  }

  private val googleSignInClient by lazy {
    GoogleSignIn.getClient(activity, gso)
  }

  fun startActivityForResult() {
    activity.startActivityForResult(googleSignInClient.signInIntent, GOOGLE_REQUEST)
  }

  open fun getAccountFromIntent(intent: Intent): GoogleSignInAccount {
    return GoogleSignIn.getSignedInAccountFromIntent(intent).getResult(ApiException::class.java)
  }

  companion object {
    const val GOOGLE_REQUEST = 1234
  }
}
