package io.fluent

sealed class Type {
  object Default : Type()
  object Loading : Type()
  object Success : Type()
  object Error : Type()
}