package com.example.testwebsocket.utils

import com.example.testwebsocket.applySchedulers
import com.example.testwebsocket.event.MessageEvent
import com.example.testwebsocket.schedulersFlowableTransformer
import com.example.testwebsocket.socket.*
import io.reactivex.Observer
import io.reactivex.subjects.PublishSubject
import timber.log.Timber
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList

object SocketManager {
    var token: String? = null
    val url: String by lazy {
        return@lazy "ws://10.252.30.1:5555"
    }

    private val observers = CopyOnWriteArrayList<Observer<MessageEvent>>()
    private var mStompClient: SocketClient? = null
    private var intervalGen: IntervalGenerator =
        IntervalGenerator()
    private var timer: Timer? = Timer()
    private var retry: Boolean = true

    val socketStatus: PublishSubject<LifecycleEvent.Type> = PublishSubject.create()

    private var typeStatusSocket: LifecycleEvent.Type? = null

    @Synchronized
    fun open() {
        Timber.e("----------->Action open")
        if (token.isNullOrEmpty()) {
            Timber.e("----------->Action change token")
            //token = AppUtils.drjoyApp().appComponent().makeOAuthRepos().getLocalOAuthToken().blockingSingle()
            token ="token"
        }
        tryConnect()
    }

    fun reset() {
        Timber.e("----------->Action reset")
        mStompClient = null
        typeStatusSocket = null

    }

    fun close() {
        Timber.e("----------->Action Close")
        socketStatus.onNext(LifecycleEvent.Type.CLOSED)
        cancelTimer()
        token = "tocken"
        retry = false
        mStompClient?.disconnect()
        intervalGen.reset()
        reset()
    }


    fun addObserver(observer: Observer<MessageEvent>) {
        synchronized(this) {
            if (!observers.contains(observer)) {
                observers.add(observer)
            }
        }
    }

    fun removeObserver(observer: Observer<MessageEvent>) {
        synchronized(this) {
            if (observers.contains(observer)) {
                observers.remove(observer)
            }
        }
    }

    private fun notifyEvent(event: MessageEvent) {
        Timber.e("Receiver : -----------> ${event.toJson()}")
        synchronized(this) {
            val obIterator = observers.iterator()
            while (obIterator.hasNext()) {
                obIterator.next().onNext(event)
            }
        }
    }

    private fun notifyEventError(event: MessageEvent) {
        if (event is Error && !event.requestId.isNullOrEmpty()) {
            notifyEvent(event)
        }
    }

    fun postEvent(event: MessageEvent) {
        Timber.e("Emit : -----------> ${event.toJson()}")
        mStompClient?.let {
            if ((!it.isConnected && !it.isConnecting) || it.isConnecting) return@let
            it.send( event.toJson())
                .compose(applySchedulers())
                .subscribe({}, Timber::e)
        }
    }

    @Synchronized
    private fun tryConnect() {
        Timber.e("------------>Action Stomp try connect")
        if (token.isNullOrEmpty() || (mStompClient != null && (isContenting() || isContented()))) return
        Timber.e("------------> Stomp try connecting")
        mStompClient?.disconnect()
        retry = true
        mStompClient = SocketClient(
            WebSocketsConnectionProvider(url)
        )
        typeStatusSocket = null
        mStompClient?.let {
            it.onMessage()
                .compose(schedulersFlowableTransformer())
                .subscribe({ topicMessage ->
                    notifyEvent(MessageEvent.fromJson(topicMessage))
                }, Timber::e)

            it.connect(
                SocketHeader(
                    "Auth-Token",
                    token!!
                ), lifecycleCallback = {
                Timber.e(it.name)
                typeStatusSocket = it
                when (it) {
                    LifecycleEvent.Type.OPENED -> {
                        Timber.e("----------->Stomp connect OPENED")
                        cancelTimer()
                    }
                    LifecycleEvent.Type.CLOSED -> {
                        Timber.e("----------->Stomp connect CLOSED")
                        //retry()
                    }
                    LifecycleEvent.Type.ERROR -> {
                        Timber.e("----------->Stomp connect ERROR")
                        retry()
                    }
                }
                socketStatus.onNext(it)
            })
        }
    }


    @Synchronized
    private fun retry() {
        Timber.e("----------->Stomp retry")
        if (!retry) return
        Timber.e("----------->Stomp retry start")
        cancelTimer()
        if (timer == null) {
            timer = Timer()
        }
        timer?.schedule(object : TimerTask() {
            override fun run() {
                if (!isConnected() && !isContenting()) {
                    Timber.e("----------->Stomp retrying")
                    mStompClient?.disconnect()
                    reset()
                    tryConnect()
                }
            }
        }, intervalGen.next())
    }

    private fun cancelTimer() {
        timer?.cancel()
        timer?.purge()
        timer = null
    }

    fun isContented(): Boolean {
        return mStompClient?.isConnected == true
    }

    fun isContenting(): Boolean {
        return mStompClient?.isConnecting == true
    }

    fun getStatusSocket(): LifecycleEvent.Type {
        return typeStatusSocket
            ?: LifecycleEvent.Type.CLOSED
    }

    fun isConnected(): Boolean {
        return (typeStatusSocket == LifecycleEvent.Type.OPENED)
    }
}