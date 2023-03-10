package com.ganeshgfx.projectmanagement.viewModels

interface Factory<T>{
    fun create() : T
}