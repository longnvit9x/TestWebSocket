package com.example.testwebsocket.presenter

interface Presenter<V> {
    fun attachView(view: V)
    fun detachView()
}