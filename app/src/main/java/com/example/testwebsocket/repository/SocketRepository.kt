package com.example.testwebsocket.repository

import com.example.testwebsocket.event.MessageEvent
import io.reactivex.Observable

interface SocketRepository {
    fun publishEventObservable(): Observable<MessageEvent>

}