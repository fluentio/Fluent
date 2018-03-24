package br.com.rsicarelli.rxfirebaselogin.feature.login

import io.fluent.State
import io.fluent.StateType

data class LoginState(
    val type: StateType = StateType.Initial
) : State {

  override fun type() = type

  fun setType(newType: StateType) = copy(type = newType)
}
