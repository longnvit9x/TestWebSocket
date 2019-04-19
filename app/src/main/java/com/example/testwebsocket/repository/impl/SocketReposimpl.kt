package com.example.testwebsocket.repository.impl

import com.example.testwebsocket.event.MessageEvent
import com.example.testwebsocket.repository.SocketRepository
import com.example.testwebsocket.utils.SocketManager
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import timber.log.Timber

class SocketReposimpl : SocketRepository {
    override fun publishEventObservable(): Observable<MessageEvent> {
        val ret: Subject<MessageEvent> = PublishSubject.create()
        SocketManager.addObserver(ret)

        return ret.filter { e ->
            e !is Error
        }.doOnDispose {
            Timber.d("xyz--publishEventObservable--")
            SocketManager.removeObserver(ret)
        }
    }
}