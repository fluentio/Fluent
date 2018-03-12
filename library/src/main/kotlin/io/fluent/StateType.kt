package io.fluent

open class StateType {
  object Initial : StateType()
  object Loading : StateType()
  object Success : StateType()
  object Error : StateType()
}
