package com.example.testwebsocket.socket

import io.reactivex.Completable
import io.reactivex.Observable

interface ConnectionProvider {
    /**
     * Subscribe this for receive stomp messages
     */
    fun messages(): Observable<String>

    /**
     * Sending stomp messages via you ConnectionProvider.
     * onError if not connected or error detected will be called, or onCompleted id sending started
     * TODO: send messages with ACK
     */
    fun send(stompMessage: String): Completable

    /**
     * Subscribe this for receive #LifecycleEvent events
     */
    fun lifecycle(): Observable<LifecycleEvent>

    /**
     * Disconnects from server. This is basically a Callable.
     * Automatically emits Lifecycle.CLOSE
     */
    fun disconnect(): Completable

    fun setHeartbeat(ms: Int): Completable
}