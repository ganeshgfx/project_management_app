package com.ganeshgfx.projectmanagement.models

sealed class State<out T> {
    object Loading : State<Nothing>()
    data class Success<out T>(val data: T) : State<T>()
    data class Failure(val error: String?) : State<Nothing>()
}