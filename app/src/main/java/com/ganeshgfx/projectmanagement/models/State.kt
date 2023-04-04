package com.ganeshgfx.projectmanagement.models

sealed class State<out T> {
    data class Success<out T : Any>(val data: T) : State<T>()
    data class Error(val error: String) : State<Nothing>()
    object Loading : State<Nothing>()
}